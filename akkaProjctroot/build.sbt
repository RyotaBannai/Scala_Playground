ThisBuild / scalaVersion := "2.13.6"

val AkkaVersion       = "2.6.15"
val ScalaTest: String = "3.2.9"

libraryDependencies ++= Seq(
  "org.scalactic"     %% "scalactic"                % ScalaTest,
  "org.scalatest"     %% "scalatest-wordspec"       % ScalaTest   % "test",
  "org.scalatest"     %% "scalatest-diagrams"       % ScalaTest   % "test",
  "org.scalatest"     %% "scalatest-shouldmatchers" % ScalaTest   % "test",
  "com.typesafe.akka" %% "akka-actor-typed"         % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
  "ch.qos.logback"     % "logback-classic"          % "1.2.3",
  "com.lihaoyi"       %% "pprint"                   % "0.5.6"
)

scalacOptions ++= Seq(
  "-Xfatal-warnings"
)
