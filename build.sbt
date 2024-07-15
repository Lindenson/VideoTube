import sbtassembly.AssemblyPlugin.autoImport.*

Compile / mainClass := Some("video.Runner")

name := "MTube"

version := "0.1"

scalaVersion := "3.4.2"

javacOptions ++= Seq("-source", "11", "-target", "11")

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

libraryDependencies ++= Seq(
  "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "8.0.0",
  "org.postgresql" % "postgresql" % "42.7.3",
  "com.typesafe.akka" %% "akka-actor-typed" % "2.9.3",
  "com.typesafe.akka" %% "akka-stream" % "2.9.3",
  "com.typesafe.akka" %% "akka-http" % "10.6.3",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.6.3",
  "com.github.jwt-scala" %% "jwt-core" % "10.0.1",
  "com.github.jwt-scala" %% "jwt-circe" % "10.0.1",
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
      case "reference.conf" => MergeStrategy.concat
      case _ => MergeStrategy.first
    }
  )




