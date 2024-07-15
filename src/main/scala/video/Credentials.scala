package video

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.Route
import com.typesafe.config.ConfigFactory
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import spray.json.DefaultJsonProtocol.*
import video.FileUploader.clientFileUpload

import java.time.{Clock, Instant}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

private val appConfig = ConfigFactory.load("application.conf")
private val secretKey: String = appConfig.getString("akka.secretKey")
private val tokenExpiration: Int = appConfig.getString("akka.tokenExpiration").toInt


case class User(username: String, password: String)

case class UserToken(token: String)


trait UserProtocol {

  import spray.json.{DefaultJsonProtocol, RootJsonFormat}

  implicit val userJsonFormat: RootJsonFormat[User] = jsonFormat2(User)
  implicit val tokenJsonFormat: RootJsonFormat[UserToken] = jsonFormat1(UserToken)
}

implicit val clock: Clock = Clock.systemUTC()
private def checkCredentials(user: User): Future[Option[JwtClaim]] = {
  Future {
    //toDO Database
    if (user.username == "admin" && user.password == "admin") {
      Some(JwtClaim(subject = Some(user.username)).issuedNow.expiresIn(tokenExpiration))
    } else {
      None
    }
  }
}

private def checkCredentialsAndGenerateToken(user: User): Future[Option[String]] = {
  checkCredentials(user).map {
    _.map(claim => Jwt.encode(claim, secretKey, JwtAlgorithm.HS256))
  }
}

private def checkTokenAndGo(authHeader: String, action: Route) = {
  val token = authHeader.substring("Bearer ".length)
  Jwt.decode(token, secretKey, Seq(JwtAlgorithm.HS256)) match
    case Failure(_) => complete(StatusCodes.Unauthorized)
    case Success(claim) =>
      isTokenValid(claim) match {
        case Some(true) => clientFileUpload
        case _ => complete(StatusCodes.Unauthorized, "Token not valid")
      }
}

def isTokenValid(claim: JwtClaim): Option[Boolean] =
  claim.expiration match {
    case Some(exp) => Some(Instant.ofEpochSecond(exp).isAfter(Instant.now))
    case None => None
  }