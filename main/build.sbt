scalaVersion := "2.12.10"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint")
// -deprecation 今後廃止予定
// -feature  明示的に使用を宣言しないといけない事件的な昨日や注意しなければならない機能を利用している
// -uncheck 型消去などでパターンマッチが有効に機能しない場合
// -Xlint その他、望ましい書き方や落とし穴についての情報

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "1.0.0-MF"
)
