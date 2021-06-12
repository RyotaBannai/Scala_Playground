class Thermometer {
  var celsius: Float = _ // default value depends on its type. in this case 0
  // use default getter and setter for celsius
  def fahrenheit = celsius * 9 / 5 + 32
  def fahrenheit_=(f: Float) = {
    celsius = (f - 32) * 5 / 9
  }
  override def toString = fahrenheit + "F/" + celsius + "C"
}
