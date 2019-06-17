data class Product(val id: String, val name: String, val price: Double, val category: String)

fun main(args: Array<String>) {

    val productsCatalog = listOf(
        Product("P5", "Agile", 100.00, "Book"),
        Product("P6", "FP", 350.00, "Book"),
        Product("P7", "Kotlin", 250.00, "Book")
    )

    val userItems = getCartItems(productsCatalog, arrayOf()).map { "${it.unitPrice}, ${it.productId}" }
    userItems.map { println(it) }
}

data class CartItem(val productId: String, val quantity: Int, val unitPrice: Double)

tailrec fun promptUser(prompt: String = "", validValues: List<String> = emptyList()): String {
    println(prompt)
    val value = readLine() ?: ""
    return if (validValues.isEmpty() || validValues.contains(value)) value else promptUser(prompt, validValues)
}

fun getProduct(catalog: Collection<Product>): Product {
    val productId = promptUser("Enter the product id wish for", catalog.map {
        it.id
    })
    return catalog.first { it.id == productId }
}

tailrec fun getCartItems(catalog: Collection<Product>, cartItems: Array<CartItem>): Array<CartItem> {
    val product = getProduct(catalog);
    val newCart = arrayOf(
        *(cartItems),
        CartItem(product.id, 1, getUnitPrice(product, ::getBookTypes))
    )
    val canContinue = promptUser("Enter C to add more products, X to Complete", listOf("C", "X")) == "C"
    return if (canContinue) getCartItems(catalog, newCart) else newCart
}

tailrec fun getBookTypes(): Pair<String, String> =
    when (promptUser("Enter E for EBook, P for Paperback", listOf("E", "P"))) {
        "E" -> Pair("E", promptUser("Enter K for kindle, P for Pdf", listOf("K", "P")))
        "P" -> Pair("P", promptUser("Enter H for Hardcover, S for Softcover", listOf("H", "S")))
        else -> getBookTypes()
    }

fun getUnitPrice(product: Product, bookTypesGetter: () -> Pair<String, String>) =
    product.price + (if (product.category == "Book") getAdditionalPriceOfBook(bookTypesGetter()) else 0.0)

fun getAdditionalPriceOfBook(bookTypes: Pair<String, String>): Double =
    when (bookTypes) {
        Pair("E", "K") -> 100.0
        Pair("E", "P") -> 150.00
        Pair("P", "H") -> 150.00
        Pair("P", "S") -> 100.00
        else -> 0.0
    }

