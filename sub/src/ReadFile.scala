import scala.io.Source
import java.io.File

class ReadFile {

}

object Readfile {
  def main(args: Array[String]): Unit={
    val fineName = new File(".").getCanonicalPath+"/src/Fn.scala"
    // val fineName = System.getProperty("user.dir")+"/src/Fn.scala"
    around(
      () => println("ファイルを開く"),
      printFile(fineName),
      () => println("ファイルを閉じる")
    )
  }
  def printFile(filename: String): Unit = {
    for (line <- Source.fromFile(filename).getLines()){
      println(line)
    }
  }
  def around(init: () => Unit, body: Unit, fin: () => Unit): Any = {
    init()
    try {
      body
    } finally {
      fin()
    }
  }
}