package sdg.tryout
package process

import net.liftweb.json.JsonParser.ParseException
import org.apache.spark.sql.SparkSession
import net.liftweb.json._
import sdg.tryout.utils.{Constants, JsonFunctions, SparkFuntions}

import scala.io.Source

object LocalProcess {
  def init(sparkSession: SparkSession): Unit = {
    val dataflowRawList: JsonAST.JValue = parse(Source.fromResource("data/dataflows.json").mkString)
      .children.head

    try {
      val dataflowClassList = JsonFunctions.parseDataflowJson(dataflowRawList)

      for (dataflow <- dataflowClassList) {
        val name = dataflow.name
        val sourceList = dataflow.sources
        val transformations = dataflow.transformations
        val sinks = dataflow.sinks

        val entryDf = SparkFuntions.readMultiLineJson(sparkSession,
          "data/input/events/person" + sourceList.head.path)
      }
    } catch {
    case e: ParseException => println("Error parsing dataflows json")
    }
  }
}
