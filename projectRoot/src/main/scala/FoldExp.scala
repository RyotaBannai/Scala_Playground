class FoldExp {}

class Foo(val name: String, val age: Int, val sex: Symbol)

object Foo {
  def apply(name: String, age: Int, sex: Symbol) = new Foo(name, age, sex)
}

object FoldExp {
  // https://coderwall.com/p/4l73-a/scala-fold-foldleft-and-foldright
  val fooList = Foo("Hugh Jass", 25, 'male) ::
    Foo("Biggus Dickus", 43, 'male) ::
    Foo("Incontinentia Buttocks", 37, 'female) ::
    Nil

  val stringList = fooList.foldLeft(List[String]()) { (z, foo) =>
    val title = foo.sex match {
      case 'male   => "Mr."
      case 'female => "Ms."
    }
    z :+ s"$title ${foo.name}, ${foo.age}"
  }

  val stringListRight = fooList.foldRight(List[String]()) { (foo, z) =>
    val title = foo.sex match {
      case 'male   => "Mr."
      case 'female => "Ms."
    }
    z :+ s"$title ${foo.name}, ${foo.age}"
  }

  "abcd".foldRight(Nil: List[Int]) { (x, acc) => x :: acc }
}
