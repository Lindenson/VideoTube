package video.rest

import akka.event.Logging
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, extractRequestContext, get, logRequest, onComplete, optionalHeaderValueByName, pathEnd}
import akka.http.scaladsl.server.Route
import video.api.VideoStreamer
import video.dto.FileSize
import video.files.nameFromID

private val streamer: VideoStreamer = VideoStreamer.getStreamer

private def getHeaders(fileSize: FileSize) = {
  val headers = List(
    RawHeader("content-range", s"bytes ${fileSize.start}-${fileSize.end}/${fileSize.contentLength}"),
    RawHeader("cache-control", "public, max-age=31536000"),
    RawHeader("Accept-Ranges", "bytes")
  )
  headers
}

def streamVideo(fileID: Int): Route =
  pathEnd {
    get {
      extractRequestContext { ctx =>
        logRequest("GET-VIDEO", Logging.DebugLevel) {
          optionalHeaderValueByName("Range") {
            case None => complete(StatusCodes.RangeNotSatisfiable)
            case Some(range) =>
              onComplete(nameFromID(fileID)(ctx.executionContext)) {
                case scala.util.Success(fileName) =>
                  val (source, fileSize) = streamer.stream(range, fileName)
                  val responseEntity = HttpEntity(MediaTypes.`video/mpeg`, source)
                  val entity = HttpResponse(StatusCodes.PartialContent, getHeaders(fileSize), responseEntity)
                  complete(entity)
                case scala.util.Failure(exception) =>
                  complete(HttpResponse(StatusCodes.InternalServerError, entity = s"An error occurred: ${exception.getMessage}"))
              }
          }
        }
      }
    }
  }

  
    
