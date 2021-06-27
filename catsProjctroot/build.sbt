ThisBuild / scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.2.9",
  "org.scalatest" %% "scalatest" % "3.2.9" % "test"
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.1.0"
)

scalacOptions ++= Seq(
  "-Xfatal-warnings"
)
