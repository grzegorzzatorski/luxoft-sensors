package pl.gzatorski.sensors

import akka.actor.{Actor, ActorRef, Props}
import scala.io.Source

case class ProcessFile()

case class Measurement(sensorId: String, humidity: Option[Int] = None)

class FileActor(filepath: String) extends Actor {
  var dataRequester: Option[ActorRef] = None
  var workers = scala.collection.mutable.Map.empty[String, ActorRef]
  var partialComputations = new scala.collection.mutable.ListBuffer[ComputationResult]
  var receivedPartials = 0

  def receive = {
    case ProcessFile() =>
      dataRequester = Some(sender())
      Source.fromFile(filepath).getLines.drop(1).foreach { line =>
        val measurement = line.split(",") match {
          case Array(id, humidity) => Measurement(id, humidity.asOptionInt)
        }

        val worker = workers.getOrElseUpdate(measurement.sensorId, context.actorOf(Props[SensorActor]))
        worker ! ProcessSingleMeasurement(measurement)
      }
      workers.values.foreach(_ ! FinishProcessing())

    case PartialComputation(value) =>
      partialComputations += value
      receivedPartials += 1
      if(workers.size == receivedPartials){
        dataRequester.foreach(_ ! partialComputations.toList)
      }
    case _ => println("Message dropped!")
  }
}