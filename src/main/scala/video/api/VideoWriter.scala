package video.api

import akka.stream.scaladsl.{FileIO, Source}
import akka.stream.{IOResult, Materializer}
import akka.util.ByteString
import video.files.FileWriter

import scala.concurrent.Future

trait VideoWriter {
  def write(fileStream: Source[ByteString, Any], filename: String, tag: String)
           (implicit mat: Materializer): Future[IOResult]
}

object VideoWriter {
  def getWriter: FileWriter = FileWriter()
}
