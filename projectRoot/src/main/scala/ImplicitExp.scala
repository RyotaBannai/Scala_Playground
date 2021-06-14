class ImplicitExp {}
object ImplicitExp {

  /** @example val i: Int = 3.5 // Not Error thank to implicit conversion
    */
  implicit def doubleToInt(x: Double) = x.toInt

  /** @example val myRectangle = 10 x 10
    */
  case class Rectangle(width: Int, height: Int)
  implicit class RectangleMarker(width: Int) {
    def x(height: Int) = Rectangle(width, height)
  }
  // implicit def RectangleMarker(width: Int) = new RectangleMarker(width) が自動生成する
}
