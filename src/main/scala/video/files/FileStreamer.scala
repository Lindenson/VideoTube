package video.files

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

  private val buffer: Int = 8096
  private val logger = LoggerFactory.getLogger(getClass)
  private val byteString: ByteString = ByteString(0x12.toByte, 0x34.toByte)

  override def stream(rangeHeader: String, fileName: String): (Source[ByteString, Future[IOResult]], FileSize) = {
    val file: File = new File(fileName)
    anyErrorWithFile(file) match {
      case None =>
        val fileSize: Long = file.length()
        val (start: Int, end: Long, contentLength: Long) = getSize(rangeHeader, fileSize)
        logger.debug(s"streaming: $start-$end/$contentLength")
        (getSource(file, start, end), FileSize(start, end, fileSize))
      case Some(fileStub) =>
        logger.error(s"File read error: $fileName")
        (fileStub, FileSize(0, 0, 0))
    }
  }

  private def anyErrorWithFile(file: File) = {
    if (file.exists() && file.isFile && file.canRead) None
    else Some(emptySource)
  }

  private def getSource(file: File, start: Int, end: Long) = {
    if (ifMobile(end)) twoBiteSource
    else FileIO.fromPath(file.toPath, buffer, start)
  }

  private def getSize(rangeHeader: String, fileSize: Long) = {
    val range = rangeHeader.split("=")(1).split("-").map(_.toInt)
    val start = range(0)
    val end = if (range.length > 1 && range(1) == 1) 1 else fileSize - 1
    val contentLength = if (ifMobile(end)) 2 else fileSize
    (start, end, contentLength)
  }

  private def emptySource =
    Source.single(ByteString.empty).mapMaterializedValue(_ => Future.successful(IOResult.createSuccessful(0)))

  private def twoBiteSource = Source.single(byteString)
    .mapMaterializedValue(_ => Future.successful(IOResult.createSuccessful(2)))

  private def ifMobile(end: Long) = end == 1
}
