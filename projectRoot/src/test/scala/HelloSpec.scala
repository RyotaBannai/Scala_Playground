import org.scalatest.diagrams.Diagrams
import org.scalatest.flatspec.AnyFlatSpec

class HelloSpec extends AnyFlatSpec with Diagrams {
  // assert(List(1, 2, 3).contains(4))
  assertResult(2) {
    3
  }
}
