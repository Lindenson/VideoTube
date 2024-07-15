package video.api

import akka.http.scaladsl.model.HttpResponse
import video.files.FileStreamer

trait VideoStreamer {
  def stream(rangeHeader: String, orderId: Int): HttpResponse
}

object VideoStreamer {
  def getStreamer: FileStreamer = FileStreamer()
}
