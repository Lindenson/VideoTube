package video

import akka.event.Logging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.*
import akka.http.scaladsl.server.Directives.{complete, get, optionalHeaderValueByName, path, *}
import akka.http.scaladsl.server.Route
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import video.FileUploader.clientFileUpload

import java.time.Clock
import scala.util.{Failure, Success}


object Controller extends SprayJsonSupport with UserProtocol {

  implicit val clock: Clock = Clock.systemUTC()
  private val secretKey = "mishaAdmin"
  private val uploadLimit = 1000000000


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
      post {
        path("login") {
          post {
            entity(as[User]) { user =>
              if (user.username == "admin" && user.password == "admin") {
                val claim = JwtClaim(subject = Some(user.username)).issuedNow.expiresIn(3600)
                val token = Jwt.encode(claim, secretKey, JwtAlgorithm.HS256)
                complete(Token(token))
              } else {
                complete(StatusCodes.Unauthorized)
              }
            }
          }
        }
      },
      path("upload") {
        withSizeLimit(uploadLimit) {
          post {
            optionalHeaderValueByName("Authorization") {
              case Some(authHeader) if authHeader.startsWith("Bearer ") =>
                val token = authHeader.substring("Bearer ".length)
                Jwt.decode(token, secretKey, Seq(JwtAlgorithm.HS256)) match
                  case Failure(_) => complete(StatusCodes.Unauthorized)
                  case Success(claim) => clientFileUpload
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
