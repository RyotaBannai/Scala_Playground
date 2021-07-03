import cats.data.Reader
import cats.syntax.applicative._ // for pure

object ReaderExpr {
  final case class Cat(name: String, favoriteFood: String)
  val catName: Reader[Cat, String] = Reader(cat => cat.name)
  val greetKitty: Reader[Cat, String] = catName.map(name => s"Hello ${name}")
  val feedKitty: Reader[Cat, String] =
    Reader(cat => s"Have a nice bowl of ${cat.favoriteFood}")

  val greetAndFeed: Reader[Cat, String] = for {
    greet <- greetKitty
    feed <- feedKitty
  } yield s"$greet. $feed."

  /* To retrieve Cat's name
  catName.run(Cat("Garfield", "lasagne"))
  => cats.Id[String] = Garfield

  greetKitty.run(Cat("Garfield", "lasagne"))
  greetAndFeed.run(Cat("Garfield", "lasagne"))
   */
}

object ReaderForConf {
  final case class Db(
      usernames: Map[Int, String],
      passwords: Map[String, String]
  )

  // Our type alias fixes the Db type but leaves the result type flexible:
  type DbReader[A] = Reader[Db, A]

  def findUsername(userId: Int): DbReader[Option[String]] =
    Reader(db => db.usernames.get(userId))
  def checkPassword(username: String, password: String): DbReader[Boolean] =
    Reader(db => db.passwords.get(username).contains(password))

  // We write steps of our program as instances of Reader,
  // chain them together with map and flatMap,
  // and build a function that accepts the dependency as input.
  def checkLogin(userId: Int, password: String): DbReader[Boolean] =
    for {
      username <- findUsername(userId)
      passwordOk <- username
        .map { username =>
          checkPassword(username, password)
        }
        .getOrElse {
          false.pure[DbReader]
        }
    } yield passwordOk

  /*
  val users = Map(
  1 -> "dade",
  2 -> "kate",
  3 -> "margo"
  )

  val passwords = Map(
    "dade"  -> "zerocool",
    "kate"  -> "acidburn",
    "margo" -> "secret"
  )

  val db = Db(users, passwords)

  checkLogin(1, "zerocool").run(db)
  // res7: cats.package.Id[Boolean] = true
  checkLogin(4, "davinci").run(db)
  // res8: cats.package.Id[Boolean] = false

   */
}
