class FP99() {}

object FP99 {
  def makeRowSeq(row: Int) =
    for (col <- 1 to 10) yield {
      val prod = (row * col).toString
      val padding = " " * (4 - prod.length)
      padding + prod
    }

  // シーケンス(generator)内の文字列に対して mkString を直接呼び出して連結できる.(SP149)
  def makeRow(row: Int) = makeRowSeq(row).mkString

  def multiTable() = {
    val tableSeq =
      for (row <- 1 to 10)
        yield makeRow(row)
    // 同手法で更に連結.
    tableSeq.mkString("\n")
  }
}
