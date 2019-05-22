package pl.gzatorski.sensors

import scala.util.{Failure, Success}

object Launcher {
  
  val usage = "Usage: java -jar sensors-all.jar [path to directory]\n"
  
  def main(args: Array[String]): Unit = {

    val dataDirectory = Utils.parseDirectory(args) match {
      case Success(e) => 
        println(s"Data directory is $e")
        e
      case Failure(n) =>
        println(s"$n\n $usage")
        System.exit(-1)
    }

  }
}
