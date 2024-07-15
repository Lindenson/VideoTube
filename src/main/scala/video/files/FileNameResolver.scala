package video.files

import com.typesafe.config.ConfigFactory

import java.io.File
import java.nio.file.Paths

private val appConfig = ConfigFactory.load("application.conf")
private val dirWithVideo: String = appConfig.getString("akka.videoDir")


def getFileToStream(orderId: Int) = {
  val n = orderId % 6 + 1
  val path = s"$dirWithVideo/$n.mp4"
  new File(path)
}

def getFileToSave(filename: String) = {
  Paths.get(s"$dirWithVideo/$filename")
}