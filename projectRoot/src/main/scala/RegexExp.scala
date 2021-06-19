class RegexExp {}

object RegexExp {
  val Decimal = """(-)?(\d+)(\.\d*)?""".r
  val input = "for -1.0 to 99 by 3 "
  val allDecimals = for (s <- Decimal findAllIn input) yield s

  // use Regex as Extractor, マッチするパターンはグループ化されている箇所(SP544)
  val Decimal(sign, integerpart, decipalpart) = "-1.23"
}
