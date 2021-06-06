package my_traits

import my_traits.Point

trait Rectangular {
  def topLeft: Point
  def bottomRight: Point
  def left = topLeft.x
  def right = bottomRight.x
  def width = right - left
  // ...
}

object Rectangular {}
