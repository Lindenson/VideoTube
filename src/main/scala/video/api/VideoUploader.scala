package video.rest

import akka.http.scaladsl.server.Route

trait VideoUploader {
  def upload: Route
}

object VideoUploader {
  def getUploader: VideoUploader = FileUploader() 
}