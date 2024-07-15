package video.rest

import akka.Done
import akka.http.scaladsl.model.*
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.FileInfo
import akka.stream.IOResult
import akka.stream.scaladsl.Source
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import video.api.VideoWriter

import scala.util.{Failure, Success}

class FileUploader extends VideoUploader {

  private val appConfig = ConfigFactory.load("application.conf")
  private val uploadLimit: Long = appConfig.getString("akka.uploadLimit").toLong
  private val logger = LoggerFactory.getLogger(getClass)

  private val videoWriter = VideoWriter.getWriter

  override def upload: Route = {
    withSizeLimit(uploadLimit) {
      formFields("fileName", "tag") { (fileName, tag) =>
        extractRequestContext { ctx =>
          fileUpload("file") {
            case (metadata, fileStream) =>
              validateContentType(metadata)
              onComplete(videoWriter.write(fileStream, fileName, tag)(ctx.materializer)) {
                case Success(statusTry) => statusTry.status match {
                  case Success(Done) =>
                    val count = statusTry.count
                    logger.info(s"File upload: $fileName, size: $count")
                    complete(StatusCodes.OK, s"$count")
                  case _ => complete(StatusCodes.InternalServerError -> statusTry.status)
                }
                case Failure(ex) => complete(StatusCodes.InternalServerError -> ex.toString)
              }
          }
        }
      }
    }
  }


  private def validateContentType(metadata: FileInfo) = {
    val contentType = metadata.contentType
    if (contentType.mediaType != MediaTypes.`video/mp4`)
      complete(StatusCodes.UnsupportedMediaType, "Only MPEG4 files are allowed.")
  }
}
