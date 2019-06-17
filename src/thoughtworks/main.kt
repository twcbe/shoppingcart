fun main(args: Array<String>) {


}

data class Category(val name: String);

sealed class Book {
    object EBook : Book()

    object PaperBack : Book()

    companion object {
        fun <T> match(book: Book, eBook: (EBook) -> T, paperBack: (PaperBack) -> T): T =
            when (book) {
                is EBook -> eBook
                is PaperBack -> paperBack
            }(book)

        fun getAdditionalPrice(book: Book) = match(book, { 0 } , { 100 });

    }

}

data class Product(val id: String, val name: String, val price: Double, val category: Category);