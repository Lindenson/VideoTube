package video.rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, onComplete}
import video.dto.{VideoNames, VideoNamesProtocol}
import video.repository.findAllVideos
import spray.json.DefaultJsonProtocol.{BooleanJsonFormat, immSeqFormat, tuple2Format}
import spray.json.enrichAny
import video.files.pageSize

import scala.concurrent.ExecutionContext

object Pager extends SprayJsonSupport with VideoNamesProtocol{
  def getVideoNamesPage(limit: Int)(implicit ec: ExecutionContext) = {
    val (from, to) = (limit * pageSize, (limit * pageSize) + pageSize)
    onComplete(findAllVideos(from, to)) {
      case scala.util.Success(videos) =>
        val existMore = videos.size > pageSize
        val videoNames = (videos.take(pageSize).map(v => VideoNames(v.name, v.videoTag)), existMore)
        complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, videoNames.toJson.toString)))
      case scala.util.Failure(exception) =>
        complete(HttpResponse(StatusCodes.InternalServerError, entity = s"An error occurred: ${exception.getMessage}"))
    }
  }
}

