fun main(args: Array<String>) {


    val productsCatlog = listOf(
        Product("P5", "Agile", 100.00, "Book"),
        Product("P6", "FP", 350.00, "Book"),
        Product("P7", "Kotlin", 250.00, "Book")
    )

    val userItems = getProductFromUser(productsCatlog, arrayOf()).map { "${it.itemPrice}, ${it.productId}" }
    userItems.map { println(it) }
}

data class CartItem(val productId: String, val quantity: Int, val itemPrice: Double);


fun getProductFromUser(catalog: Collection<Product>, cartItems: Array<CartItem>): Array<CartItem> {
    val productId = promptUser("Enter the prdouct to wish for")
    val product = catalog.first { it.id == productId }
    val cartItem = if (product.category == "Book") getBookFromUser(product) else CartItem(product.id, 1, product.price)
    val continues = promptUser("Enter c to add more products")
    val newCart = arrayOf(*(cartItems), cartItem)
    return if (continues == "c") getProductFromUser(catalog, newCart) else newCart
}

fun getBookFromUser(book: Product): CartItem {
    val bookType = promptUser("Enter E for Ebook, P for Paperback")
    val bookSubType = when (bookType) {
        "E" -> promptUser("Enter K for kindle, P for Pdf")
        "P" -> promptUser("Enter H for Hardcover, S for Softcover")
        else -> ""
    }
    return CartItem(book.id, 1, book.price + getAdditionalPrice(bookType, bookSubType))
}

fun getAdditionalPrice(bookType: String, bookSubType: String): Double =
    when (Pair(bookType, bookSubType)) {
        Pair("E", "K") -> 100.0
        Pair("E", "P") -> 150.00
        Pair("P", "H") -> 150.00
        Pair("P", "S") -> 100.00
        else -> 0.0;
    }


fun promptUser(prompt: String = ""): String {
    println(prompt)
    return readLine() ?: ""
}

data class Product(val id: String, val name: String, val price: Double, val category: String);






