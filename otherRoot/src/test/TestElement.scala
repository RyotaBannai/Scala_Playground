import org.scalatest.funsuite.AnyFunSuite
import Element.elem

class ElementTest extends AnyFunSuite {
  test("elem result should have passed width") {
    val ele = elem('x', 2, 3)
    assert(ele.width == 3)
  }
}
