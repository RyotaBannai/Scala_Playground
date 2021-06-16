class ForExp {}
object ForExp {
  case class Person(name: String, isMale: Boolean, children: Person*)
  val lara = Person("Lara", false)
  val bob = Person("Bob", true)
  val julie = Person("Julie", false, lara, bob)
  val persons = List(lara, bob, julie)

  val momAndChildPairs = persons filter (p => !p.isMale) flatMap { p =>
    (p.children map (c => (p.name, c.name)))
  }

  // withFilter を使うと、女性データを集めた中間データ構造の生成が回避される(SP440)
  val momAndChildPairs2 = persons withFilter (p => !p.isMale) flatMap { p =>
    p.children map (c => (p.name, c.name))
  }

  val momAndChildPairFor =
    for {
      p <- persons
      if !p.isMale
      c <- p.children
    } yield (p.name, c.name)

  // yield => map, flatMap, withFiler
  // without yield => withFilter, foreach
}
