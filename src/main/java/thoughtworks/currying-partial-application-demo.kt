package thoughtworks

import org.funktionale.currying.*
import kotlin.test.assertEquals

val sum2ints = {x: Int, y: Int -> x + y }
val curried:(Int) -> (Int) -> Int = sum2ints.curried()

fun main() {
    assertEquals(curried(2)(4), 6)
    val add5 = curried(5)
    assertEquals(add5(7), 12)    
}

