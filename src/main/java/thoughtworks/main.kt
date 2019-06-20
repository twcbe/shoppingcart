import org.funktionale.currying.*

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

data class Item(
    val product: Product,
    val quantity: Int,
    val price: Double,
    val tax: Double,
    val priceWithTax: Double
)

data class BookInfo(val bookType: String, val format: String)

data class CartItem(val product: Product, val quantity: Int, val bookInfo: BookInfo? = null)

data class Order(val items: List<Item>, val grossAmount: Double, val greenTax: Double, val netAmount: Double)

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

    val userSelectedProducts = getCart(catalog, emptyArray())

    val getItemCurried = ::getItem.curried()

    val bookPriceCalculator = getBookPriceCalculator(bookPrices)

    val taxCalculator = { category: String, price: Double ->
        calculatePrice(price, getTax(category))
    }

    val mapItem: (CartItem) -> Item =
        getItemCurried(bookPriceCalculator)(taxCalculator)

    val items = userSelectedProducts.map(mapItem)

}

tailrec fun getCart(catalog: Collection<Product>, userProducts: Array<CartItem>):
        Array<CartItem> {

    val updatedUserProducts = arrayOf(
        *(userProducts),
        getCartItem(catalog)
    )
    return if (isUserRequireMoreProducts()) getCart(catalog, updatedUserProducts) else updatedUserProducts
}

fun getCartItem(catalog: Collection<Product>): CartItem {
    val product = getProduct(catalog, promptUser("Enter the product id wish for", catalog.map {
        it.id
    }))
    return CartItem(product, 1, if (product.isBook()) getBookInfo() else null)
}

fun Product.isBook() = this.category == "Book"

fun getProduct(catalog: Collection<Product>, productId: String) = catalog.first { it.id == productId }

fun isUserRequireMoreProducts(): Boolean =
    promptUser("Enter C to add more products, X to Complete", listOf("C", "X")) == "C"

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

tailrec fun promptUser(prompt: String = "", validValues: List<String> = emptyList()): String {
    println(prompt)
    val value = readLine() ?: ""
    return if (validValues.isEmpty() || validValues.contains(value)) value else promptUser(prompt, validValues)
}

fun getItem(
    bookPriceCalculator: (BookInfo) -> Double,
    taxCalculator: (String, Double) -> Pair<Double, Double>,
    userSelectedProduct: CartItem
): Item {

    val unitPrice = if (userSelectedProduct.product.isBook())
        userSelectedProduct.product.price + bookPriceCalculator(userSelectedProduct.bookInfo!!)
    else userSelectedProduct.product.price

    val price = getTotalPrice(
        unitPrice,
        userSelectedProduct.quantity
    )

    val (priceWithTax, tax) = taxCalculator(userSelectedProduct.product.category, price)

    return Item(userSelectedProduct.product, userSelectedProduct.quantity, price, tax, priceWithTax)

}

fun getTotalPrice(unitPrice: Double, quantity: Int) = unitPrice * quantity

fun getTax(category: String): Double = when (category) {
    Constants.Electronics -> 2.5
    Constants.Book -> 0.5
    else -> 0.0
}

fun calculatePrice(price: Double, percentage: Double): Pair<Double, Double> {
    val percent = price * (percentage / 100)
    val total = price + percent
    return Pair(total, percent)
}


fun getAdditionalPriceForBook(bookPrices: Map<BookInfo, Double>, bookInfo: BookInfo) =
    bookPrices.entries.first { it.key == bookInfo }.value

fun getBookPriceCalculator(bookPrices: Map<BookInfo, Double>): (BookInfo) -> Double =
    ::getAdditionalPriceForBook.curried()(bookPrices)
