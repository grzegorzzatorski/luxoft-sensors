package pl.gzatorski.sensors

import akka.actor.{Actor, Stash}
import pl.gzatorski.sensors.Utils._


case class ProcessSingleMeasurement(measurement: Measurement)

case class GetStatistics()

case class ComputationResult(sensorName: String,
                             minHumidity: Option[Int],
                             maxHumidity: Option[Int],
                             sumHumidity: Long,
                             numberOfMeasurement: Long,
                             numberOfMeasurementFailed: Long)

case class PartialComputation(value: ComputationResult)


class SensorActor extends Actor with Stash {
  private var minHumidity: Option[Int] = None
  private var maxHumidity: Option[Int] = None
  private var sumHumidity: Long = 0
  private var numOfMeasurements: Long = 0
  private var numOfFailed: Long = 0

  def receive: Receive = {
    case ProcessSingleMeasurement(measurement) =>
      updateStatistics(measurement)
      unstashAll()
      context become initialized(measurement.sensorId)
    case _ => stash()
  }

  def initialized(sensorId: String): Receive = {
    case ProcessSingleMeasurement(measurement) => updateStatistics(measurement)
    case GetStatistics() =>
      val partial = ComputationResult(sensorId, minHumidity, maxHumidity, sumHumidity, numOfMeasurements, numOfFailed)
      sender ! PartialComputation(partial)
    case _ => println(s"Sensor $sensorId actor: Message unknown")
  }

  private def updateStatistics(measurement: Measurement) = {
    minHumidity = min(measurement.humidity, minHumidity)
    maxHumidity = max(measurement.humidity, maxHumidity)

    measurement.humidity.foreach { current =>
      sumHumidity += current
      numOfMeasurements += 1
    }

    if (measurement.humidity.isEmpty) {
      numOfFailed += 1
    }
  }

}