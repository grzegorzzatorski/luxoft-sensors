package pl.gzatorski.sensors

object FinalResultPrettyFormatter {

  def getPrettyString(results: List[FinalResult]): String = {
    results.map(asString(_)).mkString("\n")
  }

  private def asString(el: FinalResult) = {
    s"${el.sensorId},${getOrNaN(el.min)},${getOrNaN(el.avg)},${getOrNaN(el.max)}"
  }

  private def getOrNaN(value: Option[Int]) = {
    value.map(_.toString).getOrElse("NaN")
  }

}
