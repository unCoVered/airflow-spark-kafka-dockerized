package sdg.tryout
package process

import org.apache.spark.sql.SparkSession
import net.liftweb.json._

import scala.io.Source

object LocalProcess {
  def init(sparkSession: SparkSession): Unit = {
    val entryDf = sparkSession.read
      .option("multiline","true")
      .json("src/main/resources/data/entry.json")

    val dataflowsJson = parse(Source.fromResource("data/dataflows.json").mkString)

    println(dataflowsJson)
  }
}
