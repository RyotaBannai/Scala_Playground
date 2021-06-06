package stackable_traits

import scala.collection.mutable.ArrayBuffer
import stackable_traits.{IntQueue, DoublingTrait}

class BasicIntQueue extends IntQueue {
  private val buf = new ArrayBuffer[Int]
  def get() = buf.remove(0)
  def put(x: Int) = { buf += x }
}

// $ sbt $ run 5
// object Main extends App {
//   val a = new BasicIntQueue with DoublingTrait // trait を追加
//   a.put(0)
//   a.put(1)
//   println(a.get())
//   println(a.get())
// }
