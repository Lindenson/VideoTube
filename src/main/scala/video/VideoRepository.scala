package video;

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.TableQuery.Extract

import scala.concurrent.Future

object VideoRepository {

  class Users(tag: Tag) extends Table[User](tag, "users") {
    def username = column[String]("username")
    def password = column[String]("password")
    def * = (username, password).mapTo[User]
  }

  private val db = Database.forConfig("mydb")
  private val users = TableQuery[Users]

  def findUser(user: User): Future[Option[Extract[Users]]] =
    val query = users.filter(u => u.username === user.username && u.password === user.password).result.headOption
    db.run(query)
}
