package pl.gzatorski

import scala.language.implicitConversions


package object sensors {

  implicit class RichString(value: String) {
    
    implicit def asOptionInt: Option[Int] = {
      if (value == "NaN") None
      else Some(value.toInt)
    }
  }

}