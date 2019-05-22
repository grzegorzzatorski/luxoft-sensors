package pl.gzatorski.sensors

import akka.actor.Actor
import scala.io.Source

case class ProcessFile()

class StatisticCounterActor(filepath: String) extends Actor {
  
  def receive = {
    case ProcessFile() =>
        Source.fromFile(filepath).getLines.foreach { line =>
          //TODO: Process line stats
        }
      Some(sender).foreach(_ ! 0)

    case _ => println("Message dropped!")
  }
}