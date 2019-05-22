package pl.gzatorski.sensors

import scala.util.{Failure, Success, Try}

object Utils {

  def parseDirectory(args: Array[String]): Try[String] = {
    if (args.length != 1)
      Failure(new IllegalArgumentException("Illegal number of params. Data directory path required"))
    else {
      Success(args(0))
    }

  }
}
