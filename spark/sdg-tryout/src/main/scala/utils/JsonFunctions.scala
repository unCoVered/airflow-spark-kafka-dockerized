package sdg.tryout
package utils

import net.liftweb.json.{DefaultFormats, JsonAST}

object JsonFunctions {

  implicit val formats: DefaultFormats.type = DefaultFormats

  def parseDataflowJson(dataflowListRaw: JsonAST.JValue): List[DataFlow] = {
    dataflowListRaw.extract[List[DataFlow]]
  }

  case class DataFlow(name: String, sources: List[Source], transformations: List[Transformation], sinks: List[Sink])
  case class Source(name: String, path: String, format: String)
  case class Transformation(name: String, `type`: String, params: Parameter)
  case class Sink(input: String, name: String, paths: List[String], format: String, saveMode: String)
  case class Parameter(input: String, validations: Option[List[Validation]], addFields: Option[List[Field]])
  case class Validation(field: String, validations: List[String])
  case class Field(name: String, function: String)
}
