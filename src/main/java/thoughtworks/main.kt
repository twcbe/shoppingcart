import org.funktionale.currying.*
import org.funktionale.composition.*

data class Product(val id: String, val name: String, val price: Double, val category: String)

data class Item(
    val product: Product,
    val quantity: Int,
    val price: Double,
    val tax: Double,
    val priceWithTax: Double
)

data class BookInfo(val bookType: String, val format: String)

data class UserSelectedProduct(val product: Product, val quantity: Int, val bookInfo: BookInfo? = null)

data class Order(val items: List<Item>, val grossAmount: Double, val greenTax: Double, val netAmount: Double)

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

fun getItem(
    bookPriceCalculator: (BookInfo) -> Double,
    taxCalculator: (String, Double) -> Pair<Double, Double>,
    userSelectedProduct: UserSelectedProduct
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


fun getBookPriceCalculator(bookPrices: Map<BookInfo, Double>): (BookInfo) -> Double {

    fun getAdditionalPriceForBook(bookPrices: Map<BookInfo, Double>, bookInfo: BookInfo) =
        bookPrices.entries.first { it.key == bookInfo }.value

    return ::getAdditionalPriceForBook.curried()(bookPrices)
}

fun createOrder(items: List<Item>): Order {
    val grossAmount = items.sumByDouble { it.priceWithTax }

    val (greenTax, _) = calculatePrice(grossAmount, 2.5)

    val netAmount = grossAmount + greenTax

    return Order(items, grossAmount, greenTax, netAmount)
}

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

    val userSelectedProducts = getProductsFromUser(catalog, emptyArray())

    val getCartItemCurried = ::getItem.curried()

    val bookPriceCalculator = getBookPriceCalculator(bookPrices)


    val taxCalculator =
        { category: String, price: Double -> (::getTax andThen ::calculatePrice.curried()(price))(category) }

    val mapItem: (UserSelectedProduct) -> Item = getCartItemCurried(bookPriceCalculator)(taxCalculator)

    val items = userSelectedProducts.map(mapItem)

    val order = createOrder(items)

    println(order.netAmount)

}
