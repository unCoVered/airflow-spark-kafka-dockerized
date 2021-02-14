package sdg.tryout
package process

import net.liftweb.json.JsonParser.ParseException
import net.liftweb.json.parse
import org.apache.spark.sql.SparkSession
import sdg.tryout.utils.{JsonFunctions, SparkFuntions}

import scala.io.Source

object ReadProcess {
  def init(sparkSession: SparkSession, dataflowsFile: String, postgresDb: String,
           postgresUser: String, postgresPwd: String): Unit = {
    println("Parsing json")
   try {
     val dataflowRawList = parse(Source.fromFile(dataflowsFile).mkString)
       .children.head

     DataflowProcess.processDataflowRawList(dataflowRawList)
   } catch {
     case e: ParseException => println("Error parsing dataflows json")
   }


    val entryDf = SparkFuntions.readDfPostgres(sparkSession, "public.entry", postgresDb, postgresUser, postgresPwd)
    entryDf.show()
  }
}
