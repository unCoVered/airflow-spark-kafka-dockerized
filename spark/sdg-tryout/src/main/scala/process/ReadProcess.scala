package sdg.tryout
package process

import net.liftweb.json.parse
import org.apache.spark.sql.SparkSession
import sdg.tryout.utils.CommonFuntions

object ReadProcess {
  def init(sparkSession: SparkSession, dataflowsFile: String, postgresDb: String,
           postgresUser: String, postgresPwd: String): Unit = {
    val dataflowsJson = parse(dataflowsFile)
    println(dataflowsJson)

    val entryDf = CommonFuntions.readDfPostgres(sparkSession, "public.entry", postgresDb, postgresUser, postgresPwd)
    entryDf.show()
  }
}
