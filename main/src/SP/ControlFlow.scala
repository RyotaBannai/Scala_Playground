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
}
