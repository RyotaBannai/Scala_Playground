package classes

import Element._

class LineElement(val s: String) extends Element {
  val contents = Array(s)
  override def width: Int = s.length
  override def height: Int = 1
}
object LineElement {}
