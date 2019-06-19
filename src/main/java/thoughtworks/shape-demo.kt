package thoughtworks

sealed class Shape {

    data class Rectangle(val length: Double, val breadth: Double) : Shape()

    data class Square(val side: Double) : Shape()

    companion object {

        fun match(
            square: (Square) -> Double,
            rectangle: (Rectangle) -> Double,
            shape: Shape
        ) {
            when (shape) {
                is Square -> square
                is Rectangle -> rectangle
            }(shape)
        }
    }

}

fun calculateArea(shape: Shape) {

    fun calculateAreaOfRectangle(rec: Shape.Rectangle): Double =
        rec.length * rec.breadth

    fun calculateAreaOfSquare(square: Shape.Square): Double =
        square.side * square.side

    Shape.match(
        ::calculateAreaOfSquare,
        ::calculateAreaOfRectangle,
        shape
    )
}

fun calculatePerimeter(shape: Shape) =
    Shape.match(
        { it.side * 4 },
        { (it.length * 2) + (it.breadth * 2) },
        shape
    )

fun main() {
    val area = calculateArea(Shape.Square(10.0))
    val perimeter = calculatePerimeter(Shape.Square(10.0))
    println("Area $area perimeter $perimeter")
}

