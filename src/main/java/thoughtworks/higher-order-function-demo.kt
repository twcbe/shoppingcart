package thoughtworks


// redundant code
fun sum(till: Int): Int {
    var sum = 0
    (1..till).forEach { i -> sum += i }
    return sum
}

fun product(till: Int): Int {
    var product = 1
    (1..till).forEach { i -> product *= i }
    return product
}

fun onRange(till: Int, init: Int, action: (Int, Int) -> Int): Int {
    return (1..till).fold(init, action) //acc,item
}


fun functionReferencesExample(str: String, expression: (String) -> Unit) {
    print("Welcome To Kotlin Series @")
    expression(str)
}

fun printFunction(str: String) {
    println(str)
}


fun <T, R> logAndCall(function: (T) -> R, t: T): R {
    println("log start")
    val r = function(t)
    println("log end")
    return r

}

//decorate
fun <T, R> withLogger(function: (T) -> R): (T) -> R = { t ->
    println("log start")
    val r = function(t)
    println("log end")
    r
}


/* Use compose 
fun workflow(validator, transformer, persister, data)
{
   val validated =  validator(data)
   val transformed = transformer(transformer)
   val result = persister(transformed)
    
}

//dependancy
fun checkAlreadyExists(getExistingItems, item){
 val items = getExistingItems()
 return items.contains(item)
}

//lazy
fun checkAlreadyExistsOnlyIfNew(getExistingItems, item){
  return if(item.status == new)  getExistingItems().contains(item) else true
}


*/
fun main() {
    functionReferencesExample("JournalDev.com", ::printFunction)
}
