package pl.gzatorski

import scala.language.implicitConversions


package object sensors {

  implicit class RichString(value: String) {

    implicit def asOptionInt: Option[Int] = {
      if (value == "NaN") None
      else Some(value.toInt)
    }
  }


  implicit class RichComputationResult(result: ComputationResult) {

    implicit def asFinalResult: FinalResult = {
      val average = (result.sumHumidity / (result.numberOfMeasurementFailed + result.numberOfMeasurement)).toInt
      val formattedAvg = if (average == 0) None else Some(average)
      FinalResult(result.sensorName, result.minHumidity, formattedAvg, result.maxHumidity)
    }

  }

}