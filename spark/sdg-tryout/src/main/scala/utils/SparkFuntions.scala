package sdg.tryout
package utils

import org.apache.spark.sql.{DataFrame, SparkSession}

import sdg.tryout.utils.Constants.SparkOptions._

object SparkFuntions {

  def readMultiLineJson(sparkSession: SparkSession, uri: String): DataFrame = {
    sparkSession.read
      .option("multiline","true")
      .json(uri)
  }

  def readJson(sparkSession: SparkSession, uri: String): DataFrame = {
    sparkSession.read
      .json(uri)
  }

  def readDfPostgres(sparkSession: SparkSession, table: String, postgresDb: String,
                     postgresUser: String, postgresPwd: String): DataFrame = {
    sparkSession.read
      .format(FORMAT_JDBC)
      .option(OPT_URL, postgresDb)
      .option(OPT_DB_TABLE, table)
      .option(OPT_USER, postgresUser)
      .option(OPT_PASSWORD, postgresPwd)
      .load()

  }

  def writeDfPostgres(df: DataFrame, table: String, postgresDb: String,
                      postgresUser: String, postgresPwd: String): Unit = {
    println("Writing DF in PostgreSQL")

    df.write
      .format(FORMAT_JDBC)
      .option(OPT_URL, postgresDb)
      .option(OPT_DB_TABLE, table)
      .option(OPT_USER, postgresUser)
      .option(OPT_PASSWORD, postgresPwd)
      .mode(WRITE_MODE_OVERWRITE)
      .save()
  }
}
