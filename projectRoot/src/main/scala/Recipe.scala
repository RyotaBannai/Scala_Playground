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

object SimpleDataBase {
  def allFoods = List(Apple, Orange, Cream, Sugar)
  def foodNamed(name: String): Option[Food] = allFoods.find(_.name == name)
  def allRecipes: List[Recipe] = List(FruitSalad)

  case class FoodCategory(name: String, foods: List[Food])
  private var categories = List(
    FoodCategory(
      "fruits",
      List(Apple, Orange)
    ),
    FoodCategory("misc", List(Cream, Sugar))
  )
  def allCategories = categories
}

object SimpleBrowser {
  def recipesUsing(food: Food) = SimpleDataBase.allRecipes.filter(recipe =>
    recipe.ingredients.contains(food)
  )
  def displayCategory(category: SimpleDataBase.FoodCategory) = {
    println(category)
  }
}
