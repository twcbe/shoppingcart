package thoughtworks

import org.funktionale.currying.*
import org.funktionale.composition.*


val sum2ints = { x: Int, y: Int -> x + y }

val sum2intsCurried: (Int) ->
    (Int) -> Int = sum2ints.curried()

fun sum2intsCurriedOwn(): (Int) -> (Int) -> Int {

    return { p1 -> { p2 -> sum2ints(p1, p2) } }
}


val testFunction = { x: Int, y: Int,
                     x1: Int, y1: Int,
                     x2: Int, y2: Int
    ->
    sum2ints(
        sum2ints(
            sum2ints(x, y),
            sum2ints(x1, y1)
        ),
        sum2ints(x2, y2)
    )

}

val testCurried = testFunction.curried()

fun main() {

    //5+5
    val result1 = sum2ints(5, 5)
    val result2 = sum2intsCurried(5)(5)

    val add5 = sum2intsCurried(5)

    add5(10)

    val x1: (Int -> Int -> Int -> Double)

    val x = testCurried(10)

    val result3 = testCurried(10)(5)(10)(5)(10)(5)
    val result4 = testCurried(10)(5)(10)

    val fn1and5 = testCurried(10)// 6 parms, () -> (5params....
    val fn1and4 = fn1and5(5)
    val fn1and3 = fn1and4(10)
    val fn1and2 = fn1and3(5)
    val fn1and1 = fn1and2(10)
    val result = fn1and1(5)

    val appleJuiceMaker = getAppleJuiceFunction()
    val juice = appleJuiceMaker("apple")
}


fun appleToSlices(apple: String) = "Slices"

fun slicesToMash(slices: String) = "Mash"

fun mashToJuice(mash: String) = "Juice"

fun juiceToPack(juice: String, container: String) = "Packed - Juice"

fun juiceToPack2(container: String, juice: String) = "Packed - Juice"


fun prepareAppleJuice(apple: String): String /* Juice */ =

    mashToJuice(
        slicesToMash(
            appleToSlices(apple)
        )
    )

fun prepareAppleJuice1(apple: String): String /* Juice */ =
    (::appleToSlices andThen
            ::slicesToMash andThen
            ::mashToJuice)(apple)


fun getAppleJuiceFunction(): (String) -> String /* Juice */ =
    (::appleToSlices andThen
            ::slicesToMash andThen
            ::mashToJuice)

fun getAppleJuiceFunction1(
    appleToSlicesFn: (String) -> String,
    slicesToMashFn: (String) -> String,
    mashToJuiceFn: (String) -> String
) =
    (appleToSlicesFn andThen
            slicesToMashFn andThen
            mashToJuiceFn)


fun preparePackedJuice(apple: String, container: String): String /* Juice */ {
//    (getAppleJuiceFunction() andThen
//            ::juiceToPack)(apple)

    val appleJuiceFn = getAppleJuiceFunction()
    val juice = appleJuiceFn(apple)
    return juiceToPack(juice, container)
}

fun preparePackedJuice1(apple: String, container: String): String /* Juice */ {

    val appleJuiceFn = getAppleJuiceFunction()
    val juiceToPackCurried = ::juiceToPack.curried()
    val juice = appleJuiceFn(apple)
    val packer = juiceToPackCurried(juice)
    return packer(container)
//    return juiceToPack(juice, container)
}

fun preparePackedJuiceAndThen(apple: String, container: String): String /* Juice */ {

    val appleJuiceFn = getAppleJuiceFunction()
    val juiceToPackCurried = ::juiceToPack2.curried()
    val packedJuiceFn = appleJuiceFn andThen juiceToPackCurried(container)

    return packedJuiceFn(apple)

}

fun preparePackedJuiceAndThenOneLiner(apple: String, container: String): String /* Juice */ =
    (getAppleJuiceFunction()
            andThen ::juiceToPack2.curried()
        (container))(apple)


fun some(p1: String, p2: Int): String {
    return p1 + p2
}

fun invertParameter(fn: (String, Int) -> String): (Int, String) -> String {
    return { i, s -> fn(s, i) }
}

fun client() {

    val someInverted = invertParameter(::some)
    useSome(someInverted)
}

fun useSome(some: (Int, String) -> String) {}