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

// ignore staled implementations:
unmanagedSources / excludeFilter := HiddenFileFilter || "PrefixMapV2.12.scala" || "RNA2.12.scala"

// Adds a `src/main/scala-2.13+` source directory for Scala 2.13 and newer
// and a `src/main/scala-2.13-` source directory for Scala version older than 2.13
// scala/ dir has to be split into to like below.

// unmanagedSourceDirectories in Compile += {
//   val sourceDir = (sourceDirectory in Compile).value
//   CrossVersion.partialVersion(scalaVersion.value) match {
//     case Some((2, n)) if n >= 13 => sourceDir / "scala-2.13+"
//     case _                       => sourceDir / "scala-2.13-"
//   }
// }
