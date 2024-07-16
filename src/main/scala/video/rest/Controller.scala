package video.rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.*
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import org.slf4j.LoggerFactory
import video.dto.*
import video.rest.Pager.getVideoNamesPage
import video.security.{checkCredentialsAndGenerateToken, checkTokenAndGo}


object Controller extends SprayJsonSupport with UserProtocol {

  private val logger = LoggerFactory.getLogger(getClass)

  val route: Route =
    handleExceptions(exceptionHandler) {
      concat(
        pathPrefix("files" / IntNumber)(streamVideo),
        get {
          concat(
            pathSingleSlash {
              getFromResource("static/index.html")
            },
            path("static" / Remaining) { resource =>
              getFromResource(s"static/$resource")
            }
          )
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
          post {
            optionalHeaderValueByName("Authorization") {
              case Some(authHeader) if authHeader.startsWith("Bearer ") =>
                checkTokenAndGo(authHeader, uploadVideo)
              case _ => complete(StatusCodes.Unauthorized)
            }
          }
        },
        path("names" / IntNumber) { limit =>
          get {
            extractRequestContext { ctx =>
              implicit val ec = ctx.executionContext
              getVideoNamesPage(limit)
            }
          }
        }
      )
    }

  private def exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case EntityStreamSizeException(limit, size) =>
        logger.error(s"File size too mach $size for limit $limit")
        complete(HttpResponse(InternalServerError, entity = "Limit exceeded"))
      case _ =>
        extractUri { uri =>
          logger.error(s"Internal service error $uri")
          complete(HttpResponse(InternalServerError, entity = "Some error"))
        }
    }
}





