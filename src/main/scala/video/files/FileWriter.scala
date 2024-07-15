package video.files

import akka.stream.scaladsl.{FileIO, Source}
import akka.stream.{IOResult, Materializer}
import akka.util.ByteString
import video.api.VideoWriter

import scala.concurrent.Future

class FileWriter extends VideoWriter {
  override def write(fileStream: Source[ByteString, Any], filename: String, tag: String)
                    (implicit mat: Materializer): Future[IOResult] = {
    val filePath = getFileToSave(filename)
    val fileSink = FileIO.toPath(filePath)
    fileStream.runWith(fileSink)
  }
}

