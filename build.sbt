import sbtassembly.AssemblyPlugin.autoImport.*

Compile / mainClass := Some("video.Runner")

name := "MTube"

version := "0.1"

scalaVersion := "3.4.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % "2.8.5",
  "com.typesafe.akka" %% "akka-stream" % "2.8.5",
  "com.typesafe.akka" %% "akka-http" % "10.5.3",
  "org.slf4j" % "slf4j-simple" % "1.7.36"
)

lazy val app = (project in file("."))
  .settings(
    assembly / assemblyJarName := "MTube.jar",
    assembly / mainClass := Some("video.Runner"),
    assembly / assemblyOption ~= {
      _.withIncludeScala(true)
        .withIncludeDependency(true)
    },
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", _*) => MergeStrategy.discard
      case "reference.conf"     => MergeStrategy.concat
      case _                    => MergeStrategy.first
    }
  )




