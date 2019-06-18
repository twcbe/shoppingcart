data class Product(val id: String, val name: String, val price: Double, val category: String)

data class CartItem(val product: Product, val quantity: Int, val price: Double)

data class BookInfo(val bookType: String, val format: String)

data class UserSelectedProduct(val product: Product, val quantity: Int, val bookInfo: BookInfo? = null)

object Constants {
    const val EBook = "EBook"
    const val Paperback = "Paperback"
    const val Kindle = "Kindle"
    const val Pdf = "Pdf"
    const val Hardcover = "Pdf"
    const val Softcover = "Softcover"
}

tailrec fun promptUser(prompt: String = "", validValues: List<String> = emptyList()): String {
    println(prompt)
    val value = readLine() ?: ""
    return if (validValues.isEmpty() || validValues.contains(value)) value else promptUser(prompt, validValues)
}


fun getProduct(catalog: Collection<Product>, productId: String) = catalog.first { it.id == productId }

fun Product.isBook() = this.category == "Book"

fun getUserSelectedProduct(catalog: Collection<Product>): UserSelectedProduct {
    val product = getProduct(catalog, promptUser("Enter the product id wish for", catalog.map {
        it.id
    }))
    return UserSelectedProduct(product, 1, if (product.isBook()) getBookInfo() else null)
}

fun isUserRequireMoreProducts(): Boolean =
    promptUser("Enter C to add more products, X to Complete", listOf("C", "X")) == "C"

tailrec fun getProductsFromUser(catalog: Collection<Product>, userProducts: Array<UserSelectedProduct>):
        Array<UserSelectedProduct> {

    val updatedUserProducts = arrayOf(
        *(userProducts),
        getUserSelectedProduct(catalog)
    )
    return if (isUserRequireMoreProducts()) getProductsFromUser(catalog, updatedUserProducts) else updatedUserProducts
}


tailrec fun getBookInfo(): BookInfo =
    when (promptUser("Enter E for EBook, P for Paperback", listOf("E", "P"))) {
        "E" -> BookInfo(
            Constants.EBook, when (promptUser("Enter K for kindle, P for Pdf", listOf("K", "P"))) {
                "K" -> Constants.Kindle
                "P" -> Constants.Pdf
                else -> ""
            }
        )

        "P" -> BookInfo(
            Constants.Paperback, when (promptUser("Enter H for Hardcover, S for Softcover", listOf("H", "S"))) {
                "H" -> Constants.Hardcover
                "S" -> Constants.Softcover
                else -> ""
            }
        )
        else -> getBookInfo()
    }

fun getCartItem(bookPriceGetter: (BookInfo) -> Double, userSelectedProduct: UserSelectedProduct): CartItem {

    val price = getPrice(
        if (userSelectedProduct.product.isBook()) bookPriceGetter(userSelectedProduct.bookInfo!!) else userSelectedProduct.product.price,
        userSelectedProduct.quantity
    )
    return CartItem(userSelectedProduct.product, userSelectedProduct.quantity, price)

}

fun getPrice(unitPrice: Double, quantity: Int) = unitPrice * quantity

fun main() {

    val catalog = listOf(
        Product("P5", "Agile", 100.00, "Book"),
        Product("P6", "FP", 350.00, "Book"),
        Product("P7", "Kotlin", 250.00, "Book")
    )

    var bookPrices = mapOf(
        BookInfo(Constants.EBook, Constants.Kindle) to 100.00,
        BookInfo(Constants.EBook, Constants.Pdf) to 150.00,
        BookInfo(Constants.Paperback, Constants.Hardcover) to 150.00,
        BookInfo(Constants.Paperback, Constants.Softcover) to 100.00
    )

    val userSelectedProducts = getProductsFromUser(catalog, emptyArray())

    val getCartItemCurried = { bookPriceGetter: (BookInfo) -> Double ->
        { userSelectedProduct: UserSelectedProduct ->
            getCartItem(
                bookPriceGetter,
                userSelectedProduct
            )
        }
    }

    fun getAdditionalPriceForBook(bookInfo: BookInfo): Double = bookPrices.entries.first { it.key == bookInfo }.value

    val mapCartItem = getCartItemCurried(::getAdditionalPriceForBook)

    val cartItems = userSelectedProducts.map(mapCartItem)

    cartItems.map { it.product.name }

}
