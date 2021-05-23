object Return {
  def main(args: Array[String]): Unit = {
    println(indexOf(Array("a", "b", "c"), "c"))
  }
  def indexOf(array: Array[String], target: String): Int = {
    var i = 0
    while (i < array.length) {
      if (array(i) == target) return i
      i += 1
    }
    -1
  }

  for (x <- 1 to 5; y <- 1 until 5) {
    println("x=" + x + ", y=" + y)
  }
  for (x <- List("A", "B", "C")) println(x)

  var myList: List[String] =
    for (x <- List("A", "B", "C")) yield { // for-comprehension
      x + "s"
    }
  var result =
    for (x <- myList if x == "Bs")
      yield "here->" + x

  for (
    a <- 1 to 1000; b <- 1 to 1000; c <- 1 to 1000; if a * a == b * b + c * c
  ) yield { // ピタゴラス
    // println((a,b,c))
  }
}
