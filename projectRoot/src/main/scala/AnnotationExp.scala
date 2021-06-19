class AnnotationExp {}

object AnnotationExp {
  @deprecated("use new ShinyMethod() instead") def bigMistake() {
    println("should have...");
  }
}
