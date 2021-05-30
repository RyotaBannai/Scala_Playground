import ChecksumAccumulator._

// Entry point.
// when single object doesn't have ..., it's called standalone object.(SP85)
object Main {
  def main(args: Array[String]): Unit = {
    // val acc = new ChecksumAccumulator // NOTE: instance variable はそれぞれ保有
    println(ChecksumAccumulator.calculate("hello"))
  }
}
