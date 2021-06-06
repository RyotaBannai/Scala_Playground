package my_traits

import Rectangular._
import Point._

class Rectangle extends Rectangular {
  def topLeft = new Point(0, 0)
  def bottomRight = new Point(2, 2)
}

// $ sbt $ run 5
object Main extends App {
  val r = new Rectangle()
  println(r.width)
}
