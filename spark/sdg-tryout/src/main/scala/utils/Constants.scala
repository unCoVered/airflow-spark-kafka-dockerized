package sdg.tryout.utils

object Constants {
  object AppModes{
    val MODE_LOCAL = "local"
    val MODE_LOAD = "load"
    val MODE_READ = "read"
  }

  object SparkOptions {
    val FORMAT_JDBC = "jdbc"
    val OPT_URL = "url"
    val OPT_DB_TABLE = "dbtable"
    val OPT_USER = "user"
    val OPT_PASSWORD = "password"
    val WRITE_MODE_OVERWRITE = "overwrite"
  }
}
