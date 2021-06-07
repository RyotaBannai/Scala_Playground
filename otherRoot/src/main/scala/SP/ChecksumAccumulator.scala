import scala.collection.mutable;
// class doesn't have static members.
// when class has a companion object, the class is called companion class.
class ChecksumAccumulator {
  var sum = 0
  private var sum_priv = 0

  // NOTE: parameter is val not var.
  def add(b: Byte): Unit =
    sum_priv += b; // statement doesn't return, so the return is Unit
  def checksum(): Int = ~(sum & 0xff) + 1
}

// Singleton object
// when singleton object has the same name as its class the object is called companion object.
object ChecksumAccumulator {
  private val cache = mutable.Map.empty[String, Int];
  def calculate(s: String): Int = {
    if (cache.contains(s))
      cache(s)
    else {
      val acc = new ChecksumAccumulator
      for (c <- s)
        acc.add(c.toByte)
      val cs = acc.checksum()
      cache += (s -> cs)
      cs
    }
  }
}
