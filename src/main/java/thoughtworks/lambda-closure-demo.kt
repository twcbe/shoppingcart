package thoughtworks

val square = { number: Int -> number * number }
val nine = square(3)  //returns an Integer

val that: (Int) -> Int = { three -> three }
val more: (String, Int) -> String = { str, int -> str + int }
val noReturn: (Int) -> Unit = { num -> println(num) }

fun myFunction(): () -> Unit {
    var a = 1
    var lambda = { println(a); a += 1 }
    return lambda
}

fun main() {
    val mylambda1 = myFunction()
    mylambda1() //prints 1
    mylambda1() //prints 2 -- lambda captures the value of ‘a’

}



