import scala.io.Source
import java.io.File

class ReadFile {

}

object Readfile {
  def main(args: Array[String]): Unit={
    val fileName = new File(".").getCanonicalPath+"/src/Fn.scala"
    // val fileName = System.getProperty("user.dir")+"/src/Fn.scala"
    val buffs = Source.fromFile(fileName)
    around(
      () => println("ファイルを開く"),
      printFile(buffs),
      () => {
        println("ファイルを閉じる")
        buffs.close
      }
    )
  }
  def printFile(buff: Source): Unit = {
    for (line <- buff.getLines()){
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