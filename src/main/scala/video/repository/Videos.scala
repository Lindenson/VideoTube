package video.repository;

import org.slf4j.LoggerFactory
import slick.jdbc.PostgresProfile.api.*
import video.cache.Manager.CacheManager
import video.dto.Video
import video.rest.Controller.getClass

import scala.concurrent.{ExecutionContext, Future};
private val logger = LoggerFactory.getLogger(getClass)

class Videos(tag: Tag) extends Table[Video](tag, "videos") {
  def name = column[String]("name")
  def fileName = column[String]("filename")
  def videoTag = column[String]("tag")
  def * = (name, fileName, videoTag).mapTo[Video]
}

private val videos = TableQuery[Videos]

def saveVideo(video: Video)(implicit ec: ExecutionContext): Future[Int] = {
  db.run(videos += video).andThen {
    case scala.util.Success(_) => CacheManager.invalidate
    case scala.util.Failure(exception) => logger.error(s"Error in database $exception")
  }
}

def findAllVideos(offset: Long, limit: Long)(implicit ec: ExecutionContext): Future[Seq[Video]] = {
  val cacheKey = s"allVideos-$offset-$limit"
  CacheManager.get[Seq[Video]](cacheKey).fold {
    val result = db.run(videos.sortBy(_.name.desc).drop(offset).take(limit).result)
    result.andThen {
      case scala.util.Success(videos) => CacheManager.put(cacheKey, videos)
      case scala.util.Failure(exception) => logger.error(s"Error in database $exception")
    }
  }(Future.successful)
}

def findByName(name: String): Future[Option[Video]] = {
  db.run(videos.filter(_.name === name).result.headOption)
}
