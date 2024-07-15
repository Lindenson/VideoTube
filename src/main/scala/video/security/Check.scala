package video.security

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.Route
import com.typesafe.config.ConfigFactory
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import video.dto.User
import video.repository.findUser

import java.time.{Clock, Instant}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

private val appConfig = ConfigFactory.load("application.conf")
private val secretKey: String = appConfig.getString("akka.secretKey")
private val tokenExpiration: Int = appConfig.getString("akka.tokenExpiration").toInt


implicit val clock: Clock = Clock.systemUTC()

def checkCredentials(user: User): Future[Option[JwtClaim]] = {
  findUser(user).map {
    case Some(_) =>
      Some(JwtClaim(subject = Some(user.username)).issuedNow.expiresIn(tokenExpiration))
    case None =>
      None
  }
}

def checkCredentialsAndGenerateToken(user: User): Future[Option[String]] = {
  checkCredentials(user).map {
    _.map(claim => Jwt.encode(claim, secretKey, JwtAlgorithm.HS256))
  }
}

def checkTokenAndGo(authHeader: String, action: Route) = {
  val token = authHeader.substring("Bearer ".length)
  Jwt.decode(token, secretKey, Seq(JwtAlgorithm.HS256)) match {
    case Failure(_) => complete(StatusCodes.Unauthorized)
    case Success(claim) =>
      isTokenValid(claim) match {
        case Some(true) => action
        case _ => complete(StatusCodes.Unauthorized, "Token not valid")
      }
  }
}

private def isTokenValid(claim: JwtClaim): Option[Boolean] =
  claim.expiration match {
    case Some(exp) => Some(Instant.ofEpochSecond(exp).isAfter(Instant.now))
    case None => None
  }
