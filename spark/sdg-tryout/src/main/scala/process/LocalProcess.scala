package sdg.tryout
package process

import org.apache.spark.sql.SparkSession
import net.liftweb.json._
import sdg.tryout.utils.JsonFunctions

import scala.io.Source

object LocalProcess {
  def init(sparkSession: SparkSession): Unit = {
    val entryDf = sparkSession.read
      .option("multiline","true")
      .json("src/main/resources/data/entry.json")

    val dataflowRawList: JsonAST.JValue = parse(Source.fromResource("data/dataflows.json").mkString)
      .children.head

    DataflowProcess.processDataflowRawList(dataflowRawList)
  }
}
