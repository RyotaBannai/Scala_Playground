import scala.io.Source
import java.io.File

class ReadFile {

}

object Readfile {
  def main(args: Array[String]): Unit={
    val fileName = new File(".").getCanonicalPath+"/src/Fn.scala"
    // val fileName = System.getProperty("user.dir")+"/src/Fn.scala"
    around(
      () => {
        println("ファイルを開く")
        Source.fromFile(fileName)
      },
      printFile,
      () => println("ファイルを閉じる")
    )
  }
  def printFile(buffs: Source): Unit = {
    for (line <- buffs.getLines()){
      println(line)
    }
  }
  def around(init: () => Source, body: Source => Unit, fin: () => Unit): Unit = {
    val buffs: Source = init()
    try {
      body(buffs)
    } finally {
      fin()
      buffs.close
      // sudo lsof -u ryota | grep $(pwd)'/src/Fn.scala'
      // intellij は親切にファイル閉じてくれるのか。。
    }
  }
}