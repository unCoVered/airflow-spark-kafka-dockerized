package sdg.tryout
package utils

import utils.Constants.TransformationsType.{ADD_FIELDS, VALIDATE_FIELDS}
import utils.JsonFunctions.{Field, Transformation}

import org.apache.spark.sql.catalyst.ScalaReflection.universe.Quasiquote
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.DataFrame

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
    val temp = entryDf
      .where(s"${field} is ${validation}")
    println("validate fields")
    temp.show

    temp
  }

  val addFields: Field => DataFrame => DataFrame = field => entryDf => {
    val temp = entryDf
      .withColumn(s"${field.name}", lit(q"""field.function"""))
    println("add fields")
    temp.show

    temp
  }
}
