package sdg.tryout
package process

import org.apache.spark.sql.SparkSession
import sdg.tryout.utils.CommonFuntions


object LoadProcess {
  def init(sparkSession: SparkSession, entryFile: String, postgresDb: String,
           postgresUser: String, postgresPwd: String): Unit = {
    val entryDf = CommonFuntions.readMultiLineJson(sparkSession, entryFile)

    entryDf.show

    CommonFuntions.writeDfPostgres(entryDf, "public.entry", postgresDb, postgresUser, postgresPwd)
  }
}
