package video.dto

import spray.json.DefaultJsonProtocol.{StringJsonFormat, jsonFormat2}

case class VideoNames(name: String, tag: String)

trait VideoNamesProtocol {
  import spray.json.{DefaultJsonProtocol, RootJsonFormat}
  implicit val videoJsonFormat: RootJsonFormat[VideoNames] = jsonFormat2(VideoNames)
}
