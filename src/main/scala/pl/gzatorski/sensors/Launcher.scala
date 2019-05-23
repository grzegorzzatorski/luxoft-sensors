package pl.gzatorski.sensors

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.ask
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object Launcher {
  val usage = "Usage: java -jar sensors-all.jar [path to directory]\n"
  def main(args: Array[String]): Unit = {
    
    Utils.parseDirectory(args) match {
      case Success(dataDir) =>
        val system = ActorSystem("System")
        implicit val executionContext = system.dispatcher
        implicit val timeout = Timeout(60 seconds)
        val futures = ReportFileReader.getReportFilesPaths(dataDir).map { file =>
          println(s"Staring to process ${file.getName}")
          val actor = system.actorOf(Props(new StatisticCounterActor(file.getAbsolutePath)))
          actor ? ProcessFile()
        }

        futures.map { future =>
          future.map { result =>
            //TODO: Aggregate data
          }
        }
        Await.result(Future.sequence(futures), 10.minutes)
        system.terminate()
      case Failure(n) =>
        println(s"$n\n $usage")
        System.exit(-1)
    }

  }
}
