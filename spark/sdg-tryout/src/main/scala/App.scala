package sdg.tryout

import org.apache.spark.sql.SparkSession

object App {
  def main(args: Array[String]): Unit = {
    val logFile = "/usr/local/spark/resources/data/movies.csv" // Should be some file on your system
    val spark = SparkSession.builder.appName("SDG-tryout").getOrCreate()
    val logData = spark.read.textFile(logFile).cache()
    val numAs = logData.filter(line => line.contains("a")).count()
    val numBs = logData.filter(line => line.contains("b")).count()
    println("==========================================")
    println("==========================================")
    println(s"Lines with a: $numAs, Lines with b: $numBs")
    println("==========================================")
    println("==========================================")

    spark.stop()
  }
}
