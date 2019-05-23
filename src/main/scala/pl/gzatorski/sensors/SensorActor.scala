package pl.gzatorski.sensors

import akka.actor.{Actor, Stash}


case class ProcessSingleMeasurement(measurement: Measurement)

case class ComputationResult(sensorName: String,
                             minHumidity: Option[Int],
                             maxHumidity: Option[Int],
                             sumHumidity: Long,
                             numberOfMeasurement: Long,
                             numberOfMeasurementFailed: Long)

case class PartialComputation(value: ComputationResult)

case class FinishProcessing()

class SensorActor extends Actor with Stash {
  var minHumidity: Option[Int] = None
  var maxHumidity: Option[Int] = None
  var sumHumidity: Long = 0
  var numOfMeasurements: Long = 0
  var numOfFailed: Long = 0

  def receive: Receive = {
    case ProcessSingleMeasurement(measurement) =>
      updateStatisctics(measurement)
      unstashAll()
      context become initialized(measurement.sensorId)
    case _ => stash()
  }

  def initialized(sensorId: String): Receive = {
    case ProcessSingleMeasurement(measurement) => {
      updateStatisctics(measurement)
    }

    case FinishProcessing() => {
      val partial = ComputationResult(sensorId, minHumidity, maxHumidity, sumHumidity, numOfMeasurements, numOfFailed)
      sender ! PartialComputation(partial)
    }
    case _ => println("Message unknown")
  }
  
  private def updateStatisctics(measurement: Measurement) = {
    minHumidity = Utils.min(measurement.humidity, minHumidity)
    maxHumidity = Utils.max(measurement.humidity, maxHumidity)

    measurement.humidity.foreach { current =>
      sumHumidity += current
      numOfMeasurements += 1
    }

    if (measurement.humidity.isEmpty) {
      numOfFailed += 1
    }
  }

}