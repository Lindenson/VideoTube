package video.files

import com.typesafe.config.ConfigFactory
import video.dto.Video
import video.repository.{findAllVideos, saveVideo}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

private val appConfig = ConfigFactory.load("application.conf")
private val dirWithVideo: String = appConfig.getString("akka.videoDir")
val pageSize = 6


def nameFromID(fileID: Int)(implicit ec: ExecutionContext): Future[String] = {
  val from = ((fileID - 1) / pageSize) * pageSize
  val to = from + pageSize
  val index : Int = (fileID - 1) % pageSize
  findAllVideos(from, to).map { videoSeq =>
    val video = videoSeq(index)
    s"$dirWithVideo/${video.fileName}"
  }
}

def nameToSave(name: String, tag: String)(implicit ec: ExecutionContext): Future[String] = {
  Future { Video(name, UUID.randomUUID().toString + ".mp4", tag) }
    .flatMap { video => saveVideo(video).map { _ => s"$dirWithVideo/${video.fileName}" }}
}