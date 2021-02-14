package sdg.tryout
package process

import org.apache.spark.sql.SparkSession
import sdg.tryout.utils.SparkFuntions


object LoadProcess {
  def init(sparkSession: SparkSession, entryFile: String, postgresDb: String,
           postgresUser: String, postgresPwd: String): Unit = {
    val entryDf = SparkFuntions.readMultiLineJson(sparkSession, entryFile)

    entryDf.show

    SparkFuntions.writeDfPostgres(entryDf, "public.entry", postgresDb, postgresUser, postgresPwd)
  }
}
