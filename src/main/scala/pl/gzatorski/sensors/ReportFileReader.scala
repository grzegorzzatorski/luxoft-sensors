package pl.gzatorski.sensors

import java.io.File

object ReportFileReader {
  
  private val csvExtension = "csv"

  def getReportFilesPaths(directoryPath: String): List[File] = {
    val dir = new File(directoryPath)

    if (dir.exists()) {
      dir.listFiles
        .filter(_.isFile).toList
        .filter(_.getName.endsWith(csvExtension))
    }
    else List()
  }

}
