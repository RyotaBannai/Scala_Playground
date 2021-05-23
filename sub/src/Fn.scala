class Fn {}

object Fn {
  def main(args: Array[String]): Unit = {
    around(
      () => println("ファイルを開く"),
      () => throw new Exception("例外発生！"),
      () => println("ファイルを閉じる")
    )
  }
  val add = (x: Int, y: Int) => x + y
  val addCurried = (x: Int) => ((y: Int) => x + y) // 無名関数をネスト
  //
  def double(i: Int, f: Int => Int): Int = {
    f(f(i))
  }

  def around(init: () => Unit, body: () => Any, fin: () => Unit): Any = {
    init()
    try {
      body()
    } finally {
      fin()
    }
  }

}
