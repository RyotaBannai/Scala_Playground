import scala.collection.mutable
class CollectionExp {}

object CollectionExp {
  def countWords(text: String) = {
    val counts = mutable.Map.empty[String, Int]

    for (rawWord <- text.split("[ ,!.]+")) {
      val word = rawWord.toLowerCase
      val oldCount = if (counts.contains(word)) counts(word) else 0
      counts += (word -> (oldCount + 1))
    }
    counts
  }

  def heavyCalc(x: String) = {
    println("taking my time."); Thread.sleep(100)
    x.reverse
  }
  val myCache = collection.mutable.Map[String, String]()
  def cachedHeavyCalc(s: String) = myCache.getOrElseUpdate(s, heavyCalc(s))
}
