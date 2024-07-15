package video.files

import akka.http.scaladsl.model.*
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString
import org.slf4j.LoggerFactory
import video.api.VideoStreamer
import video.dto.FileSize
import video.rest.Controller.getClass

import java.io.File
import scala.concurrent.Future

class FileStreamer extends VideoStreamer {

  private val buffer: Int = 4048
  private val logger = LoggerFactory.getLogger(getClass)
  private val byteString: ByteString = ByteString(0x12.toByte, 0x34.toByte)

  override def stream(rangeHeader: String, fileName: String): (Source[ByteString, Future[IOResult]], FileSize) = {
    val file: File = new File(fileName)
    val fileSize: Long = file.length()
    val (start: Int, end: Long, contentLength: Long) = getSize(rangeHeader, fileSize)
    logger.info(s"streaming: $start-$end/$contentLength")
    (getSource(file, start, end), FileSize(start, end, fileSize))
  }

  private def getSource(file: File, start: Int, end: Long) = {
    val fileSource: Source[ByteString, Future[IOResult]] =
      if (ifMobile(end)) getTwoBiteSource
      else FileIO.fromPath(file.toPath, buffer, start)
    fileSource
  }

  private def getSize(rangeHeader: String, fileSize: Long) = {
    val range = rangeHeader.split("=")(1).split("-").map(_.toInt)
    val start = range(0)
    val end = if (range.length > 1 && range(1) == 1) 1 else fileSize - 1
    val contentLength = if (ifMobile(end)) 2 else fileSize
    (start, end, contentLength)
  }

  private def getTwoBiteSource = Source.single(byteString)
    .mapMaterializedValue(_ => Future.successful(IOResult.createSuccessful(2)))

  private def ifMobile(end: Long) = end == 1
}
