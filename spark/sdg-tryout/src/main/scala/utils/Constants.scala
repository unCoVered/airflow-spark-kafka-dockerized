package sdg.tryout
package utils

object Constants {
  object AppModes{
    val MODE_LOCAL = "local"
    val MODE_REMOTE = "remote"
  }

  object Routes {
    val RESOURCES_ROUTE = "/usr/local/spark/resources"
  }

  object TransformationsType {
    val VALIDATE_FIELDS = "validate_fields"
    val ADD_FIELDS = "add_fields"
  }

  object Validations {
    val NOT_EMPTY = "notEmpty"
    val NOT_NULL = "notNull"
    val CURRENT_TIMESTAMP = "current_timestamp"
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
