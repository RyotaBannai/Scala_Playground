abstract class XY{
  val x: Int
  val y: Int
  def print: Unit
}
class P(val x:Int, val y:Int) extends XY {
  def print(): Unit = {
    println("a")
  }
}
// A extends B : A is creating a default (no-argument) constructor for class
// https://stackoverflow.com/questions/53564697/scala-unspecified-value-parameters
class Abs_Inherit(override val x:Int, override val y:Int) extends P(x,y){
  override def print(): Unit = {
    println(x, y)
  }
}
