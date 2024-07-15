package video

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import org.slf4j.LoggerFactory

import java.net.NetworkInterface
import scala.jdk.CollectionConverters.*

object Runner {
  private val logger = LoggerFactory.getLogger(getClass)
  private val port : Int = 8080


  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "akka-video-stream")

    val localhost: String = getLocalIP.head
    logger.info(f"running service: $localhost:$port")
    Http().newServerAt(localhost, port).bind(Streamer.route)
  }

  private def getLocalIP: List[String] = {
    NetworkInterface.getNetworkInterfaces.asScala.toList
      .flatMap(_.getInetAddresses.asScala)
      .filter(addr => !addr.isLoopbackAddress && addr.isSiteLocalAddress)
      .map(_.getHostAddress)
      .filter(addr => addr.contains("192"))
  }
}