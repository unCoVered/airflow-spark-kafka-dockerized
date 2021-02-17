package sdg.tryout
package process

import net.liftweb.json.JsonParser.ParseException
import org.apache.spark.sql.SparkSession
import net.liftweb.json._
import utils.{JsonFunctions, SparkFuntions, TransformFunctions}

import sdg.tryout.utils.Constants.Kafka.KAFKA_HOST_LOCAL
import sdg.tryout.utils.Constants.SinkInputs.{OK_WITH_DATE, VALIDATION_KO}

import scala.io.Source

object LocalProcess {
  def init(sparkSession: SparkSession): Unit = {
    val dataflowRawList: JsonAST.JValue = parse(Source.fromResource("data/dataflows.json").mkString)
      .children.head

    try {
      val dataflowClassList = JsonFunctions.parseDataflowJson(dataflowRawList)

      for (dataflow <- dataflowClassList) {
        val sourceList = dataflow.sources
        val transformations = dataflow.transformations
        val sinks = dataflow.sinks

        val resourcesRoute = "src/main/resources"

        val entryDf = SparkFuntions.readMultiLineJson(sparkSession,
          resourcesRoute + sourceList.head.path)

        val (validRecordsDf, invalidRecordsDf) = TransformFunctions.applyDataflowTransformations(entryDf, transformations)

        for (sink <- sinks) {
          val fileName = sink.name
          val format = sink.format
          val saveMode = sink.saveMode

          sink.input match {
            case OK_WITH_DATE =>
              for (path <- sink.paths) {
                SparkFuntions.writeDf(validRecordsDf, resourcesRoute + path, fileName, format.toLowerCase, saveMode.toLowerCase)
                SparkFuntions.writeDfKafka(validRecordsDf, "kafka", fileName, KAFKA_HOST_LOCAL)
              }
            case VALIDATION_KO =>
              for (path <- sink.paths) {
                SparkFuntions.writeDf(invalidRecordsDf, resourcesRoute + path, fileName, format.toLowerCase, saveMode.toLowerCase)
                SparkFuntions.writeDfKafka(validRecordsDf, "kafka", fileName, KAFKA_HOST_LOCAL)
              }
          }
        }
      }
    } catch {
      case e: ParseException => println("Error parsing dataflows json")
    }
  }
}
