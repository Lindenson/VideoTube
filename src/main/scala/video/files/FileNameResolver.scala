package video.files

import com.typesafe.config.ConfigFactory

import scala.concurrent.Future

private val appConfig = ConfigFactory.load("application.conf")
private val dirWithVideo: String = appConfig.getString("akka.videoDir")

def nameFromID(fileID: Int): Future[String] = {
  Future.successful {
    val n = fileID  % 6 + 1
    s"$dirWithVideo/$n.mp4"
  }
}

def nameToSave(originalName: String) = {
  s"$dirWithVideo/$originalName"
}
