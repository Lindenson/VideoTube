package video.files

import com.typesafe.config.ConfigFactory
import video.dto.Video
import video.repository.{findAllVideos, saveVideo}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

private val appConfig = ConfigFactory.load("application.conf")
private val dirWithVideo: String = appConfig.getString("akka.videoDir")

def nameFromID(fileID: Int)(implicit ec: ExecutionContext): Future[String] = {
  val from: Int = fileID / 6
  val to: Int = from + 5
  findAllVideos(from, to)
    .map(videoSeq => videoSeq.slice(fileID - 1, fileID))
    .map(video => video.head.fileName)
    .map(name => s"$dirWithVideo/$name")
}

def nameToSave(name: String, tag: String)(implicit ec: ExecutionContext): Future[String] = {
  Future { Video(name, UUID.randomUUID().toString + ".mp4", tag) }
    .flatMap { video => saveVideo(video).map { _ => s"$dirWithVideo/${video.fileName}" }}
}