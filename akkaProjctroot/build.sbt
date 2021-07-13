ThisBuild / scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.2.9",
  "org.scalatest" %% "scalatest" % "3.2.9" % "test"
)

val AkkaVersion = "2.6.15"

libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed"         % AkkaVersion
libraryDependencies += "ch.qos.logback"     % "logback-classic"          % "1.2.3"
libraryDependencies += "com.typesafe.akka" %% "akka-actor-testkit-typed" % "2.5.32" % Test

libraryDependencies ++= Seq("com.lihaoyi" %% "pprint" % "0.5.6")

scalacOptions ++= Seq(
  "-Xfatal-warnings"
)
