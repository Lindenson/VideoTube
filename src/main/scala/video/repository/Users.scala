package video.repository

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import slick.jdbc.PostgresProfile.api.*
import video.dto.User

import scala.concurrent.Future

class Users(tag: Tag) extends Table[User](tag, "users") {
  def username = column[String]("username")
  def password = column[String]("password")
  def * = (username, password).mapTo[User]
}

private val users = TableQuery[Users]

def findUser(user: User): Future[?] =
  val query = users.filter(u => u.username === user.username && u.password === user.password).result.headOption
  db.run(query)

