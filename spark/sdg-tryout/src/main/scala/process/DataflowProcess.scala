package sdg.tryout.process

import net.liftweb.json.JsonAST
import sdg.tryout.utils.JsonFunctions
import sdg.tryout.utils.JsonFunctions.DataFlow

object DataflowProcess {
  def processDataflowRawList(dataflowRawList: JsonAST.JValue): Unit = {

    val dataflowClassList = JsonFunctions.parseDataflowJson(dataflowRawList)

    for (dataflow <- dataflowClassList) {
      DataflowProcess.processDataflow(dataflow)
    }
  }

  def processDataflow(dataflow: DataFlow): Unit = {
    val name = dataflow.name
    val sourceList = dataflow.sources
    val transformations = dataflow.transformations
    val sinks = dataflow.sinks

    println("HERE")
  }
}
