package video.api

import akka.stream.IOResult
import akka.stream.scaladsl.Source
import akka.util.ByteString
import video.dto.FileSize
import video.files.FileStreamer

import scala.concurrent.Future

trait VideoStreamer {
  def stream(rangeHeader: String, fileID: String): (Source[ByteString, Future[IOResult]], FileSize)
}

object VideoStreamer {
  def getStreamer: FileStreamer = FileStreamer()
}
