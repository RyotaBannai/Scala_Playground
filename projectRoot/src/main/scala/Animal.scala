package animal

class Food
abstract class Animal {
  type SuitableFood <: Food // upper bound
  def eat(food: SuitableFood)
}
class Grass extends Food
class Fish extends Food
class Cow extends Animal {
  type SuitableFood = Grass // パス依存型(path-dependent type)
  override def eat(food: SuitableFood) = {}
}
