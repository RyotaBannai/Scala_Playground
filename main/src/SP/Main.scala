import ChecksumAccumulator._
import Rational._
import FP99._
import FileMatcher._

// Entry point.
// when single object doesn't have ..., it's called standalone object.(SP85)
object Main extends App {
  // val acc = new ChecksumAccumulator // NOTE: instance variable はそれぞれ保有
  // println(ChecksumAccumulator.calculate("hello"))

  // val r = new Rational(1, 2)
  // println()

  // println(FP99.multiTable())

  // print all file ends with '.scala'
  FileMatcher.filesEnding(".scala").foreach(println)
}
