package sdg.tryout
package utils

import utils.Constants.TransformationsType.{ADD_FIELDS, VALIDATE_FIELDS}
import utils.JsonFunctions.{Field, Transformation}

import org.apache.spark.sql.catalyst.ScalaReflection.universe.Quasiquote
import org.apache.spark.sql.functions.{col, current_timestamp, lit}
import org.apache.spark.sql.{Column, DataFrame}
import sdg.tryout.utils.Constants.Validations.{CURRENT_TIMESTAMP, NOT_EMPTY, NOT_NULL}

object TransformFunctions {

  def applyDataflowTransformations(entryDf: DataFrame, transformations: List[Transformation]): DataFrame = {
    var modifiedDf = entryDf

    for (transformation <- transformations) {
      transformation.`type` match {
        case VALIDATE_FIELDS =>
          for (validation <- transformation.params.validations.get) {
            for (validationString <- validation.validations) {
              modifiedDf = modifiedDf.transform(validateFields(validation.field, validationString))
            }
          }
        case ADD_FIELDS =>
          for (field <- transformation.params.addFields.get) {
            modifiedDf = modifiedDf.transform(addFields(field))
          }
      }
    }

    modifiedDf
  }

  val validateFields: (String, String) => DataFrame => DataFrame = (field, validation) => entryDf => {
    val parsedValidation = parseValidation(validation);
    entryDf
      .where(s"${field} ${parsedValidation}")
  }

  val addFields: Field => DataFrame => DataFrame = field => entryDf => {
    val parsedFunction = parseFunction(field.function)
    entryDf
      .withColumn(s"${field.name}", parsedFunction)
  }

  def parseValidation(validation: String): String = {
    validation match {
      case NOT_EMPTY => " <> ''"
      case NOT_NULL => "IS NOT NULL"
    }
  }

  def parseFunction(function: String): Column = {
    function match {
      case CURRENT_TIMESTAMP => current_timestamp
    }
  }
}
