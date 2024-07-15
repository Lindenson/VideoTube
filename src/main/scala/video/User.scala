package video

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import spray.json.DefaultJsonProtocol.*

case class User(username: String, password: String)

case class Token(token: String)

trait UserProtocol extends DefaultJsonProtocol {
  implicit val userJsonFormat: RootJsonFormat[User] = jsonFormat2(User)
  implicit val tokenJsonFormat: RootJsonFormat[Token] = jsonFormat1(Token)

}

