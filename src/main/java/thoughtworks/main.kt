object Constants {
    const val Book = "Book"
    const val Electronics = "Electronics"
    const val EBook = "EBook"
    const val Paperback = "Paperback"
    const val Kindle = "Kindle"
    const val Pdf = "Pdf"
    const val Hardcover = "Pdf"
    const val Softcover = "Softcover"
}

data class Product(val id: String, val name: String, val price: Double, val category: String)

data class BookInfo(val bookType: String, val format: String)

data class CartItem(val product: Product, val quantity: Int)

fun main() {

    val catalog = listOf(

        Product("P1", "TV", 10000.00, Constants.Electronics),
        Product("P2", "Mobile", 7500.00, Constants.Electronics),
        Product("P3", "Headset", 2500.00, Constants.Electronics),
        Product("P4", "Alexa", 1500.00, Constants.Electronics),

        Product("P5", "Agile", 100.00, Constants.Book),
        Product("P6", "FP", 350.00, Constants.Book),
        Product("P7", "Kotlin", 250.00, Constants.Book)
    )

    val bookPrices = mapOf(
        BookInfo(Constants.EBook, Constants.Kindle) to 100.00,
        BookInfo(Constants.EBook, Constants.Pdf) to 150.00,
        BookInfo(Constants.Paperback, Constants.Hardcover) to 150.00,
        BookInfo(Constants.Paperback, Constants.Softcover) to 100.00
    )

    val cart = getCart(catalog, emptyArray())

}

tailrec fun getCart(catalog: Collection<Product>, userProducts: Array<CartItem>):
        Array<CartItem> {

    val updatedCart = arrayOf(
        *(userProducts),
        getCartItem(catalog)
    )
    return if (isUserRequireMoreProducts()) getCart(catalog, updatedCart) else updatedCart
}

fun getCartItem(catalog: Collection<Product>): CartItem {
    val product = getProduct(catalog, promptUser("Enter the product id wish for", catalog.map {
        it.id
    }))
    return CartItem(product, 1)
}

fun getProduct(catalog: Collection<Product>, productId: String) = catalog.first { it.id == productId }

fun isUserRequireMoreProducts(): Boolean =
    promptUser("Enter C to add more products, X to Complete", listOf("C", "X")) == "C"

tailrec fun promptUser(prompt: String = "", validValues: List<String> = emptyList()): String {
    println(prompt)
    val value = readLine() ?: ""
    return if (validValues.isEmpty() || validValues.contains(value)) value else promptUser(prompt, validValues)
}
