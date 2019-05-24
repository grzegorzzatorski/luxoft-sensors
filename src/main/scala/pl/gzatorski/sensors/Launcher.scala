package pl.gzatorski.sensors

import pl.gzatorski.sensors.FinalResultPrettyFormatter._
import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.ask
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object Launcher {

  def main(args: Array[String]): Unit = {
    val app = new Launcher(args)
    app.run()
  }

}


class Launcher(args: Array[String]) {

  private val usage = "Usage: java -jar sensors-all.jar [path to directory]\n"

  def run() = {
    Utils.parseDirectory(args) match {
      case Success(dataDir) =>
        val system = ActorSystem("System")
        implicit val executionContext = system.dispatcher
        implicit val timeout = Timeout(10 minutes)

        val futures = ReportFileReader.getReportFilesPaths(dataDir).map { file =>
          val actor = system.actorOf(Props(new FileActor(file.getAbsolutePath)))
          (actor ? ProcessFile()).mapTo[List[ComputationResult]]
        }

        val futuresAggregates = Future.sequence(futures).map(_.flatten)

        futuresAggregates.onComplete {
          case Success(results) => printDetails(results, dataDir)
          case Failure(e) => println(s"Something goes wrong: ${e.getMessage}")
        }

        Await.result(Future.sequence(futures), 10.minutes)
        system.terminate()

      case Failure(n) =>
        println(s"$n\n $usage")
        System.exit(-1)
    }
  }

  private def printDetails(results: List[ComputationResult], dataDir: String) = {

    val header =
      """
        |Sensors with highest avg humidity
        |
        |sensor-id,min,avg,max
      """.stripMargin

    val aggregator = new ComputationAggregator(results)
    val completeResults = aggregator.getSortedFinalResults()
    println(s"Num of processed files: ${ReportFileReader.getReportFilesPaths(dataDir).size}")
    println(s"Num of processed measurements: ${aggregator.getNumberOfMeasurements()}")
    println(s"Num of failed measurements: ${aggregator.getNumberOfMeasurementsFailed()}")
    println(header)
    println(s"${getPrettyString(completeResults)}")
  }

}