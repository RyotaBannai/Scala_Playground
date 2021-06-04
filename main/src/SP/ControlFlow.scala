import java.io.File
import java.io.PrintWriter

class ControlFlow {}
object ControlFlow {
  def withPrintWriter(file: File, op: PrintWriter => Unit) = {
    val writer = new PrintWriter(file)
    try {
      op(writer)
    } finally {
      writer.close()
    }
  }

  def use(fileName: String): Unit = {
    // new control flow.
    withPrintWriter(
      new File(fileName),
      writer => writer.println(new java.util.Date)
    )
  }

  def newWithPrintWriter(file: File)(op: PrintWriter => Unit) = {
    val writer = new PrintWriter(file)
    try {
      op(writer)
    } finally {
      writer.close()
    }
  }

  // use braces when there is only one arg
  def useNewOne(fileName: String): Unit = {
    val file = new File(fileName)
    newWithPrintWriter(file) { writer =>
      writer.println(new java.util.Date)
    }
  }

  private val assertionsEnabled: Boolean = true
  def byNameAssert(pred: => Boolean) =
    if (assertionsEnabled && !pred) throw new AssertionError

  def useAssert(): Unit = {
    byNameAssert(1 > 3)
  }
}
