package sdg.tryout
package process

import net.liftweb.json.JsonParser.ParseException
import net.liftweb.json.parse
import org.apache.spark.sql.SparkSession
import utils.JsonFunctions.DataFlow
import utils.{Constants, JsonFunctions, SparkFuntions, TransformFunctions}

import sdg.tryout.utils.Constants.Routes.RESOURCES_ROUTE
import sdg.tryout.utils.Constants.SinkInputs.{OK_WITH_DATE, VALIDATION_KO}

import scala.io.Source

object ClusterProcess {
  def init(sparkSession: SparkSession, dataflowsFile: String, postgresDb: String,
           postgresUser: String, postgresPwd: String): Unit = {
    try {
      val dataflowRawList = parse(Source.fromFile(dataflowsFile).mkString)
        .children.head

      val dataflowClassList = JsonFunctions.parseDataflowJson(dataflowRawList)

      for (dataflow <- dataflowClassList) {
        ClusterProcess.processDataflow(sparkSession, dataflow, postgresDb, postgresUser, postgresPwd)
      }


    } catch {
      case e: ParseException => println("Error parsing dataflows json")
    }
  }

  def processDataflow(sparkSession: SparkSession, dataflow: DataFlow, postgresDb: String,
                      postgresUser: String, postgresPwd: String): Unit = {
    val sourceList = dataflow.sources
    val transformations = dataflow.transformations
    val sinks = dataflow.sinks

    val entryDf = SparkFuntions.readMultiLineJson(sparkSession,
      RESOURCES_ROUTE + sourceList.head.path)

    val (validRecordsDf, invalidRecordsDf)  = TransformFunctions.applyDataflowTransformations(entryDf, transformations)

    for (sink <- sinks) {
      val fileName = sink.name
      val format = sink.format
      val saveMode = sink.saveMode

      sink.input match {
        case OK_WITH_DATE =>
          for (path <- sink.paths) {
            SparkFuntions.writeDf(validRecordsDf, RESOURCES_ROUTE + path, fileName, format.toLowerCase, saveMode.toLowerCase)
            SparkFuntions.writeDfPostgres(entryDf, s"public.$fileName", saveMode, postgresDb, postgresUser, postgresPwd)
          }
        case VALIDATION_KO =>
          for (path <- sink.paths) {
            SparkFuntions.writeDf(invalidRecordsDf, RESOURCES_ROUTE + path, fileName, format.toLowerCase, saveMode.toLowerCase)
          }
      }
    }

  }
}
