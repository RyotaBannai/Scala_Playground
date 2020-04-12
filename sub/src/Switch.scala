object Switch {
  def main(args: Array[String]): Unit = {
    asia_or_not(countryList)
  }
  val _name: String = "Jack"
  def your_sex (name: String): String = {
    name match {
      case "Taro" => "Male"
      case "Jira" => "Male"
      case "Cassie" => "Female"
      case "Jack" | "Ryan" => "Male" // 複数まとめる
      case _ => "Other" // default value matches all. ワイルドカードパターン
    }
  }
  val wineList: List[String] = List("Chardonnay", "Merlot", "Tempranillo")
  val oldwineList: List[String] = List("Chateau Ausone", "Chateau Cheval Blanc", "Chateau D'Yquem")
  val nestWineList: List[List[String]] = List(wineList, oldwineList)
  def wine_or_not (somelist: List[List[String]]): Unit = {
    somelist match {
      case List(x, a@List("Chateau Ausone", b, c)) =>
        println(b)
        println(c)
        println("Just famous wine list:"+x)
      case _ =>
        println("Not wine list.") // default value matches all. ワイルドカードパターン
    }
  }
  val countryList: List[String] = List("Japan", "Korea", "China", "Thai")
  def asia_or_not (someList: List[String]) = List {
    someList match {
      case "Japan" :: b :: _ => // リストが可変長の場合は中置パターンを使う
        println("Asia list")
        println(b)
      case "US" :: _ =>
        println("Not Asia list ")
    }
  }
}
