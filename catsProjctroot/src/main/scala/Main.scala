// Define a very simple JSON AST
sealed trait Json
final case class JsObject(get: Map[String, Json]) extends Json
final case class JsString(get: String) extends Json
final case class JsNumber(get: Number) extends Json
final case object JsNull extends Json

// Type class
trait JsonWriter[A] {
  def write(value: A): Json
}

final case class Person(name: String, email: String)

// Instance
object JsonWriterInstances {
  // These are known as implicit values.
  implicit val stringWriter: JsonWriter[String] =
    new JsonWriter[String] {
      def write(value: String): Json = JsString(value)
    }

  implicit val personWriter: JsonWriter[Person] =
    new JsonWriter[Person] {
      def write(value: Person): Json = JsObject(
        Map(
          "name" -> JsString(value.name),
          "email" -> JsString(value.email)
        )
      )
    }

  // Recursive Implicit Resolution to create Type Class instance
  // @example Json.toJson(Option("A String")) => Json = JsString(A String)
  implicit def optionWriter[A](implicit
      writer: JsonWriter[A]
  ): JsonWriter[Option[A]] = new JsonWriter[Option[A]] {
    def write(option: Option[A]): Json = option match {
      case Some(aValue) => writer.write(aValue)
      case None         => JsNull
    }
  }
}

// Use
object Json {
  def toJson[A](value: A)(implicit w: JsonWriter[A]): Json = w.write(value)
}

// @example Person("Dave", "dave@example.com").toJson
object JsonSyntax {
  implicit class JsonWriterOps[A](value: A) {
    def toJson(implicit w: JsonWriter[A]): Json = w.write(value)
  }
}

object MyApp extends App {
  import JsonWriterInstances._
  import JsonSyntax._

  val r = Json.toJson(Person("Dave", "dave@example.com"))
  println(r)

  val r2 = Person("Dave", "dave@example.com").toJson;
  println(r2)

}
