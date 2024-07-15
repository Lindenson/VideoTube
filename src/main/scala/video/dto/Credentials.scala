package video.dto

import spray.json.DefaultJsonProtocol.{StringJsonFormat, jsonFormat1, jsonFormat2}

case class User(username: String, password: String)
case class UserToken(token: String)

trait UserProtocol {

  import spray.json.{DefaultJsonProtocol, RootJsonFormat}

  implicit val userJsonFormat: RootJsonFormat[User] = jsonFormat2(User)
  implicit val tokenJsonFormat: RootJsonFormat[UserToken] = jsonFormat1(UserToken)
}

