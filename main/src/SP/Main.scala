import ChecksumAccumulator._
import Rational._
import FP99._
import FileMatcher._
import ControlFlow._
import classes.Element._
import classes.Spiral._
import stackable_traits._

// Entry point.
// when single object doesn't have ..., it's called standalone object.(SP85)
object Main extends App {
  // val acc = new ChecksumAccumulator // NOTE: instance variable はそれぞれ保有
  // println(ChecksumAccumulator.calculate("hello"))

  val r1 = new Rational(1, 2)
  val r2 = new Rational(3, 2)
  // Thank to Ordered trait.
  println(r1 < r2)

  // println(FP99.multiTable())

  // print all file ends with '.scala'
  // FileMatcher.filesEnding(".scala").foreach(println)
  // ControlFlow.use("log.txt")
  // ControlFlow.useAssert()

  // val a = elem(Array("string"))
  // val b = elem(Array("world"))
  // val c = a above b;
  // val d = c beside c;
  // println(d.toString)

  // var nSides = args(0).toInt
  // println(spiral(nSides, 0))

  val a = new BasicIntQueue with DoublingTrait with FilteringTrait // trait を追加
  a.put(0)
  a.put(-1)
  a.put(2)
  println(a.get())
  println(a.get())
}
