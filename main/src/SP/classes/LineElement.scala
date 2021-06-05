package classes

import ArrayElement._

class LineElement(val s: String) extends ArrayElement(Array(s)) {
  override def width: Int = s.length
  override def height: Int = 1
}
object LineElement {}
