package pl.gzatorski.sensors


case class FinalResult(sensorId: String, min: Option[Int], avg: Option[Int], max: Option[Int])


class ComputationAggregator(results: List[ComputationResult]) {

  def getSortedFinalResults(): List[FinalResult] = {
    getGroupedBySensorId()
      .map(_.asFinalResult)
      .sortBy(_.avg)(Ordering[Option[Int]].reverse)
  }

  def getNumberOfMeasurements(): Long = {
    results.map(_.numberOfMeasurement).sum + getNumberOfMeasurementsFailed()
  }

  def getNumberOfMeasurementsFailed(): Long = {
    results.map(_.numberOfMeasurementFailed).sum
  }

  private def getGroupedBySensorId(): List[ComputationResult] = {
    results
      .groupBy(res => res.sensorName)
      .map { case (groupId, values) =>
        val min = values.map(_.minHumidity).min
        val max = values.map(_.maxHumidity).max
        val sum = values.map(_.sumHumidity).sum
        val measurements = values.map(_.numberOfMeasurement).sum
        val failed = values.map(_.numberOfMeasurementFailed).sum

        ComputationResult(groupId, min, max, sum, measurements, failed)
      }.toList

  }

}
