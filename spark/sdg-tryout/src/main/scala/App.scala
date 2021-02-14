package sdg.tryout

import org.apache.spark.sql.SparkSession
import process._

import utils.Constants.AppModes._

object App {
  def main(args: Array[String]): Unit = {
    val mode = args(0)
    val postgresDb = args(1)
    val postgresUser = args(2)
    val postgresPwd = args(3)

    val spark = Either.cond(mode == MODE_LOCAL, localSpark(), clusterSpark())

    spark match {
      case Right(sparkSession) =>
        LocalProcess.init(sparkSession)
        sparkSession.stop()
      case Left(sparkSession) =>
        if (mode == MODE_LOAD) {
          val entryFile = args(4)

          LoadProcess.init(sparkSession, entryFile, postgresDb, postgresUser, postgresPwd)
        }
        if (mode == MODE_READ) {
          val dataflowsFile = args(4)

          ReadProcess.init(sparkSession, dataflowsFile, postgresDb, postgresUser, postgresPwd)
        }

        sparkSession.stop()
    }
  }

  def localSpark(): SparkSession = {
    val spark = SparkSession
      .builder
      .appName("SDG-tryout")
      .master("local")
      .config("spark.jars","src/main/resources/jars/postgresql-42.2.18.jar")
      .config("spark.jars.packages", "org.postgresql:postgresql:42.2.18")
      .getOrCreate()

    spark
  }

  def clusterSpark(): SparkSession = {
    val spark = SparkSession
      .builder
      .appName("SDG-tryout")
      .getOrCreate()

    spark
  }
}
