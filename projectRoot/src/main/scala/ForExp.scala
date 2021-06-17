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

  // n-queens
  def queens(n: Int): List[List[(Int, Int)]] = {
    def isSafe(queen: (Int, Int), queens: List[(Int, Int)]) =
      queens forall (q => !isCheck(queen, q))

    def isCheck(q1: (Int, Int), q2: (Int, Int)) =
      q1._1 == q2._1 || q1._2 == q2._2 || (q1._1 - q2._1).abs == (q1._2 - q2._2).abs

    def placeQueens(k: Int): List[List[(Int, Int)]] =
      if (k == 0)
        List(List())
      else
        for {
          queens <- placeQueens(k - 1)
          column <- 1 to n
          queen = (k, column)
          if isSafe(queen, queens)
        } yield queen :: queens

    placeQueens(n)
  }
}

object DBQuery {
  case class Book(title: String, authors: String*)
  val books: List[Book] = List(
    Book("Structure and interpretation of Computer Programs"),
    Book("Principles of Compiler and Design", "Aho, Alfred", "Ullman, Jeffrey"),
    Book("Programming in Modula-2", "Wirth, Niklaus"),
    Book("Elements of ML Programming", "Ullman, Jeffrey"),
    Book("The Java Language Specification", "Goslin, James", "Joy, Bill")
  )

  val goslinsBooks =
    for (book <- books; a <- book.authors if a startsWith "Goslin")
      yield book.title;

  val javaBooks =
    for (book <- books; if (book.title indexOf "Java") >= 0)
      yield book.title

  val authoredMoreThanTwo =
    for {
      b1 <- books; b2 <- books
      if b1 != b2
      a1 <- b1.authors; a2 <- b2.authors
      if a1 == a2
    } yield a1

  // この再帰処理は上位レイアーから結果値を決めていくケース
  def removeDuplicates[A](xs: List[A]): List[A] = {
    if (xs.isEmpty) xs
    else xs.head :: removeDuplicates(xs.tail filter (x => x != xs.head))
  }
}
