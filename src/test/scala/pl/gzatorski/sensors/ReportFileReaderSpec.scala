package pl.gzatorski.sensors

import org.scalatest._


class ReportFileReaderSpec extends Matchers with WordSpecLike {

  private val dirFilePath = getClass.getResource("/reports").getPath

  "ReportFileReader" should {

    "list csv files only" in {
      val files = ReportFileReader.getReportFilesPaths(dirFilePath).map { file => file.getName }
      files should equal(List("leader-1.csv", "leader-2.csv"))
    }

    "return empty list when directory no exists" in {
      val files = ReportFileReader.getReportFilesPaths("/no/exists/dir").map { file => file.getName }
      files should equal(List())
    }

  }
}
