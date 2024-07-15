package video

import akka.Done
import akka.http.scaladsl.model.*
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.FileIO
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import java.nio.file.Paths
import scala.util.{Failure, Success}


object FileUploader {

  private val appConfig = ConfigFactory.load("application.conf")
  private val dirWithVideo: String = appConfig.getString("akka.videoDir")
  private val logger = LoggerFactory.getLogger(getClass)

  def clientFileUpload: Route = {
    extractRequestContext { ctx =>
      implicit val materializer = ctx.materializer
      fileUpload("file") {
        case (metadata, fileStream) =>
          val contentType = metadata.contentType
          if (contentType.mediaType != MediaTypes.`video/mp4`)
            complete(StatusCodes.UnsupportedMediaType, "Only MPEG4 files are allowed.")
          val filename = metadata.fileName
          val filePath = Paths.get(s"$dirWithVideo/$filename")
          val fileSink = FileIO.toPath(filePath)
          val writeResult = fileStream.runWith(fileSink)
          onComplete(writeResult) {
            case Success(statusTry) => statusTry.status match {
              case Success(Done) =>
                val count = statusTry.count
                logger.info(s"File upload: $filename, size: $count")
                complete(StatusCodes.OK, s"$count")
              case _ => complete(StatusCodes.InternalServerError -> statusTry.status)
            }
            case Failure(ex) => complete(StatusCodes.InternalServerError -> ex.toString)
          }
      }
    }
  }
}
