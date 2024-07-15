package video.repository;

import slick.jdbc.PostgresProfile.api.*
import video.dto.Video

import scala.concurrent.Future;

class Videos(tag: Tag) extends Table[Video](tag, "videos") {
  def name = column[String]("name")
  def fileName = column[String]("filename")
  def videoTag = column[String]("filename")
  def * = (name, fileName, videoTag).mapTo[Video]
}

private val videos = TableQuery[Videos]

def saveVideo(video: Video): Future[Int] = {
  db.run(videos += video)
}

def findAllVideos: Future[Seq[Video]] = {
  db.run(videos.result)
}

def findByName(name: String): Future[Option[Video]] = {
  db.run(videos.filter(_.name === name).result.headOption)
}
