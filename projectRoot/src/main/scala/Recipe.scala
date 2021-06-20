package recipe

// Food や Recipe クラスはデータベースに永続化されるエンティティ(Entity)を表現
abstract class Food(val name: String) {
  override def toString = name
}

class Recipe(
    val name: String,
    val ingredients: List[Food],
    val institutions: String
) {
  override def toString = name
}

object Apple extends Food("Apple")
object Orange extends Food("Orange")
object Cream extends Food("Cream")
object Sugar extends Food("Sugar")
object FruitSalad
    extends Recipe(
      "Fruit Salad",
      List(Apple, Orange, Cream, Sugar),
      "Stir it all together."
    )

// モジュールは１つのファイルに納めるには大きすぎることが多いため、
// トレイトを使って１つのモジュールを複数のファイルに分割すると良い(Mixin する)(SP573)
trait FoodCategories {
  case class FoodCategory(name: String, foods: List[Food])
  def allCategories: List[FoodCategory]
}

trait SimpleFoods {
  this: FoodCategories =>
  def allCategories: List[FoodCategory] = Nil
  object Pear extends Food("Pear")
  def allFoods = List(Apple, Pear)
}

// self-type(SP574)
trait SimpleRecipes {
  // この trait がミックスインされる具象クラスに関する要求を指定
  // 他のトレイトとともにミックスインされた時にだけ使われるトレイトでは、
  // 他のトレイトのミックスインを前提とするように指定できるため(SP574)
  this: SimpleFoods =>
  object FruitSalad
      extends Recipe(
        "Fruit Salad",
        List(Apple, Pear),
        "Mix it all together."
      )
  def allRecipes = List(FruitSalad)
}

abstract class Database extends FoodCategories {
  def allFoods: List[Food]
  def allRecipes: List[Recipe]
  def foodNamed(name: String): Option[Food] = allFoods.find(_.name == name)

}

// データベースごとにブラウザを切り替えられるようにしたい
abstract class Browser {
  val database: Database
  def recipesUsing(food: Food) =
    database.allRecipes.filter(recipe => recipe.ingredients.contains(food))
  def displayCategory(category: database.FoodCategory) = {
    println(category)
  }
}

object SimpleDatabase extends Database with SimpleFoods with SimpleRecipes {
  private var categories = List(
    FoodCategory(
      "fruits",
      List(Apple, Orange)
    ),
    FoodCategory("misc", List(Cream, Sugar))
  )
  override def allCategories = categories
}

object SimpleBrowser extends Browser {
  val database: Database = SimpleDatabase
}
