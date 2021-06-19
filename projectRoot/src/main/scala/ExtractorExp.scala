class ExtractorExp {}

object ExtractorExp {
  object Email extends ((String, String) => String) {
    // Function2 のオブジェクト宣言を入れておくと,
    // Function2 を要求するメソッドに Email を渡せるようになる
    def apply(user: String, domain: String) = user + "@" + domain

    // None を返すと pattern match は他の alternative を探すようになる.
    def unapply(str: String): Option[(String, String)] = {
      val parts = str split "@"
      if (parts.length == 2) Some(parts(0), parts(1)) else None
    }
  }

  object Twice {
    def apply(s: String): String = s + s
    def unapply(s: String): Option[String] = {
      val length = s.length / 2
      val half = s.substring(0, length)
      if (half == s.substring(length)) Some(half) else None
    }
  }

  object UpperCase {
    def unapply(s: String): Boolean = s.toUpperCase == s
  }

  def userTwiceUpper(s: String) = s match {
    case Email(Twice(x @ UpperCase()), domain) =>
      "match: " + x + " in domain " + domain
    case _ => "no match"
  }

  // 可変個の引数をとる抽出子
  object Domain {
    def apply(parts: String*): String =
      parts.reverse.mkString(".")

    def unapplySeq(whole: String): Option[Seq[String]] = Some(
      whole.split("\\.").reverse
    )
  }

  def isTomInDotCom(s: String): Boolean = s match {
    case Email("tom", Domain("com", _*)) => true
    case _                               => false
  }

  object ExpandedEmail {
    def unapplySeq(email: String): Option[(String, Seq[String])] = {
      val parts = email split "@"
      if (parts.length == 2) Some(parts(0), parts(1).split("\\.").reverse)
      else None
    }
  }
}

/*
userTwiceUpper("DIDI@hotmail.com")
// String = match: DI in domain hotmail.com

isTomInDotCom("tom@sun.com")

// ExpandedEmail
val s = "tom@support.epfl.ch"
val ExpandedEmail(name, topdom, subdoms @ _*) = s
>> name: String = tom
>> topdom: String = ch
>> subdoms: Seq[String] = WrappedArray(epfl, support)
 */
