package thoughtworks

import org.funktionale.composition.*
import kotlin.test.assertEquals

val add5 = { i: Int -> i + 5 }
val multiplyBy2 = { i: Int -> i * 2 }

fun compose() {
    val add5andMultiplyBy2 = add5 andThen multiplyBy2 
    assertEquals(add5andMultiplyBy2(2), 14) //(2 + 5) * 2
}

