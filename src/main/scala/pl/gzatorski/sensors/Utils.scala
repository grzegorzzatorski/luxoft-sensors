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

  def min(first: Option[Int], second: Option[Int]): Option[Int] = (first, second) match {
    case (Some(a), Some(b)) => if (a < b) Some(a) else Some(b)
    case (None, None)       => None
    case (None, b)          => b
    case (a, None)          => a
  }

  def max(first: Option[Int], second: Option[Int]): Option[Int] = (first, second) match {
    case (Some(a), Some(b)) => if (a > b) Some(a) else Some(b)
    case (None, None)       => None
    case (None, b)          => b
    case (a, None)          => a
  }
  
}
