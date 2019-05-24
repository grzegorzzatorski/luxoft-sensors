package pl.gzatorski.sensors

import akka.actor.{Actor, ActorRef, Props}
import scala.io.Source

case class ProcessFile()

case class Measurement(sensorId: String, humidity: Option[Int] = None)

class FileActor(filepath: String) extends Actor {
  private var dataAggregator: Option[ActorRef] = None
  private val sensorActors = scala.collection.mutable.Map.empty[String, ActorRef]
  private var partialComputations = new scala.collection.mutable.ListBuffer[ComputationResult]
  private var receivedPartials = 0

  def receive = {
    case ProcessFile() =>
      dataAggregator = Some(sender())
      Source.fromFile(filepath).getLines.drop(1).foreach { line =>
        val measurement = line.split(",") match {
          case Array(id, humidity) => Measurement(id, humidity.asOptionInt)
        }

        val worker = sensorActors.getOrElseUpdate(measurement.sensorId, context.actorOf(Props[SensorActor]))
        worker ! ProcessSingleMeasurement(measurement)
      }
      sensorActors.values.foreach(_ ! GetStatistics())

    case PartialComputation(value) =>
      partialComputations += value
      receivedPartials += 1
      if (sensorActors.size == receivedPartials) {
        dataAggregator.foreach(_ ! partialComputations.toList)
      }
    case _ => println(s" File $filepath actor: Message unknown")
  }
}