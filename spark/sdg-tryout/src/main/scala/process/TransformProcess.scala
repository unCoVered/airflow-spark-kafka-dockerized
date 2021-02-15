package sdg.tryout
package process

import net.liftweb.json.JsonParser.ParseException
import net.liftweb.json.{JsonAST, parse}
import org.apache.spark.sql.{DataFrame, SparkSession}
import sdg.tryout.utils.JsonFunctions.DataFlow
import sdg.tryout.utils.{Constants, JsonFunctions, SparkFuntions}

import scala.io.Source

object TransformProcess {
  def init(sparkSession: SparkSession, dataflowsFile: String, postgresDb: String,
           postgresUser: String, postgresPwd: String): Unit = {
    try {
      val dataflowRawList = parse(Source.fromFile(dataflowsFile).mkString)
        .children.head

      val dataflowClassList = JsonFunctions.parseDataflowJson(dataflowRawList)

      for (dataflow <- dataflowClassList) {
        TransformProcess.processDataflow(sparkSession, dataflow, postgresDb, postgresUser, postgresPwd)
      }


    } catch {
      case e: ParseException => println("Error parsing dataflows json")
    }
  }

  def processDataflow(sparkSession: SparkSession, dataflow: DataFlow, postgresDb: String,
                      postgresUser: String, postgresPwd: String): Unit = {
    val name = dataflow.name
    val sourceList = dataflow.sources
    val transformations = dataflow.transformations
    val sinks = dataflow.sinks

    val entryDf = SparkFuntions.readMultiLineJson(sparkSession,
      Constants.Routes.RESOURCES_ROUTE + sourceList.head.path)

    SparkFuntions.writeDfPostgres(entryDf, s"public.$name", postgresDb, postgresUser, postgresPwd)
  }
}
