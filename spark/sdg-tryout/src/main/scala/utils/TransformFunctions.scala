package sdg.tryout
package utils

import utils.Constants.TransformationsType.{ADD_FIELDS, VALIDATE_FIELDS}
import utils.JsonFunctions.{Field, Transformation}

import org.apache.spark.sql.functions.{col, current_timestamp, lit}
import org.apache.spark.sql.{Column, DataFrame}
import utils.Constants.Validations.{CURRENT_TIMESTAMP, NOT_EMPTY, NOT_NULL}

object TransformFunctions {

  def applyDataflowTransformations(entryDf: DataFrame, transformations: List[Transformation]): (DataFrame, DataFrame) = {
    var validRecordsDf = entryDf
    var invalidRecordsDf = entryDf

    for (transformation <- transformations) {
      transformation.`type` match {
        case VALIDATE_FIELDS =>
          for (validation <- transformation.params.validations.get) {
            for (validationString <- validation.validations) {
              validRecordsDf = validRecordsDf.transform(validateFields(validation.field, validationString))
            }
          }
        case ADD_FIELDS =>
          invalidRecordsDf = entryDf.except(validRecordsDf)

          for (field <- transformation.params.addFields.get) {
            validRecordsDf = validRecordsDf.transform(addFields(field))
            invalidRecordsDf = invalidRecordsDf.transform(addFields(field))
          }
      }
    }

    (validRecordsDf, invalidRecordsDf)
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
