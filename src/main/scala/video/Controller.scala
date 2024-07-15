package video

import akka.event.Logging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.*
import akka.http.scaladsl.server.Directives.{complete, get, optionalHeaderValueByName, path, *}
import akka.http.scaladsl.server.Route
import com.typesafe.config.ConfigFactory
import video.FileUploader.clientFileUpload


object Controller extends SprayJsonSupport with UserProtocol {

  private val appConfig = ConfigFactory.load("application.conf")
  private val uploadLimit: Int = appConfig.getString("akka.uploadLimit").toInt

  val route: Route =
    concat(
      pathPrefix("files" / IntNumber)(getStream),
      get {
        pathSingleSlash {
          getFromResource("static/index.html")
        } ~
          path("static" / Remaining) { resource =>
            getFromResource(s"static/$resource")
          }
      },
      path("login") {
        post {
          entity(as[User]) { user =>
            onSuccess(checkCredentialsAndGenerateToken(user)) {
              case Some(token) => complete(UserToken(token))
              case None => complete(StatusCodes.Unauthorized)
            }
          }
        }
      },
      path("upload") {
        withSizeLimit(uploadLimit) {
          post {
            optionalHeaderValueByName("Authorization") {
              case Some(authHeader) if authHeader.startsWith("Bearer ") =>
                checkTokenAndGo(authHeader, clientFileUpload)
              case _ => complete(StatusCodes.Unauthorized)
            }
          }
        }
      }
    )

  private def getStream(orderId: Int): Route =
    concat(
      pathEnd {
        concat(
          get {
            logRequest("GET-VIDEO", Logging.InfoLevel) {
              optionalHeaderValueByName("Range") {
                case None => complete(StatusCodes.RangeNotSatisfiable)
                case Some(range) => complete(FileStreamer.stream(range, orderId))
              }
            }
          })
      })
}



