package classes

import Element._

// use parametric field val valName: Type
class ArrayElement(val contents: Array[String]) extends Element {
  // def contents: Array[String] = conts
  // you can override method with field.
}
object ArrayElement {}
