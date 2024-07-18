package video.rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, onComplete}
import spray.json.DefaultJsonProtocol.immSeqFormat
import spray.json.enrichAny
import video.dto.{VideoNames, VideoNamesProtocol}
import video.repository.findByName

object TextSearch extends SprayJsonSupport with VideoNamesProtocol {

  def fullTextSearch(name: String) = {
    onComplete(findByName(name)) {
      case scala.util.Success(videos) =>
        val videoNames = videos.map(v => VideoNames(v.name, v.videoTag))
        complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, videoNames.toJson.toString)))
      case scala.util.Failure(exception) =>
        complete(HttpResponse(StatusCodes.InternalServerError, entity = s"An error occurred: ${exception.getMessage}"))
    }
  }
}