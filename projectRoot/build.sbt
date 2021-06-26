ThisBuild / scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.2.9",
  "org.scalatest" %% "scalatest" % "3.2.9" % "test"
)

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "2.0.0",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.0.0",
  "org.scala-lang.modules" %% "scala-swing" % "3.0.0"
)
