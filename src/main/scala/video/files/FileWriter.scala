package video.files

import akka.stream.scaladsl.{FileIO, Source}
import akka.stream.{IOResult, Materializer}
import akka.util.ByteString
import video.api.VideoWriter

import java.nio.file.Paths
import scala.concurrent.{ExecutionContext, Future}

class FileWriter extends VideoWriter {
  override def write(fileStream: Source[ByteString, Any], name: String, tag: String)
                    (implicit mat: Materializer, ec: ExecutionContext): Future[IOResult] = {
    nameToSave(name, tag).map(name => Paths.get(name)).map(file => FileIO.toPath(file))
      .flatMap(fileSink => fileStream.runWith(fileSink))
  }
}

