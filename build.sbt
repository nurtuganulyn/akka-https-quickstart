import com.typesafe.sbt.packager.docker.ExecCmd

enablePlugins(JavaAppPackaging, AshScriptPlugin)

dockerBaseImage := "openjdk:8-jre-alpine"
packageName in Docker := "akkahttp-quickstart"

name := "akka-http-sample"

version := "0.3"

scalaVersion := "2.13.3"

val AkkaVersion = "2.6.10"
val AkkaHttpVersion = "10.2.1"
val circeVersion = "0.13.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json"     % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit"        % AkkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion     % Test,
  "org.scalatest"     %% "scalatest"                % "3.0.8"         % Test,

  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,

  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "de.heikoseeberger" %% "akka-http-circe" % "1.31.0"

)

dockerCommands := dockerCommands.value.map {
  case ExecCmd("CMD", _ @ _*) =>
    ExecCmd("CMD", "/opt/docker/bin/akkahttp-sample")
  case other =>
    other
}





//import com.typesafe.sbt.packager.docker.ExecCmd
//
//enablePlugins(JavaAppPackaging, AshScriptPlugin)
//
//dockerBaseImage := "openjdk:8-jre-alpine"
//packageName in Docker := "akkahttp-quickstart"
//
//lazy val akkaHttpVersion = "10.2.1"
//lazy val akkaVersion    = "2.6.10"
//
//lazy val root = (project in file(".")).
//  settings(
//    inThisBuild(List(
//      organization    := "com.example",
//      scalaVersion    := "2.13.3"
//    )),
//    name := "akka-http-quickstart-scala",
//    libraryDependencies ++= Seq(
//      "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
//      "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
//      "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
//      "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
//      "ch.qos.logback"    % "logback-classic"           % "1.2.3",
//
//      "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion % Test,
//      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion     % Test,
//      "org.scalatest"     %% "scalatest"                % "3.0.8"         % Test
//    )
//  )
