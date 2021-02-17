package sdg.tryout
package utils

import org.apache.spark.sql.{DataFrame, SparkSession}
import utils.Constants.SparkOptions._

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

  def writeDfPostgres(df: DataFrame, table: String, mode: String, postgresDb: String,
                      postgresUser: String, postgresPwd: String): Unit = {
    df.write
      .format(FORMAT_JDBC)
      .option(OPT_URL, postgresDb)
      .option(OPT_DB_TABLE, table)
      .option(OPT_USER, postgresUser)
      .option(OPT_PASSWORD, postgresPwd)
      .mode(mode)
      .save()
  }

  def writeDf(df: DataFrame, route: String, fileName: String, format: String, mode: String): Unit = {
    df.coalesce(1)
      .write
      .format(format)
      .mode(mode)
      .save(route + "/" + fileName)
  }

  def writeDfKafka(df: DataFrame, format: String, topic: String, kafkaHost: String): Unit = {
    println("Writing in Kafka")
    println(s"format: $format")
    println(s"KAFKA_HOST: $kafkaHost")
    println(s"topic: $topic")
    df
      .toJSON
      .selectExpr("CAST(value AS STRING)")
      .write
      .format(format)
      .option(KAFKA_BS_SERVERS, kafkaHost)
      .option(TOPIC, topic)
      .save()

    println("Written in KAFKA")
  }
}
