package finn.hub.stock.finnhub

import finn.hub.stock.StockItem

interface FinnhubCallback {
    fun ListOfStock(stockItems: ArrayList<StockItem>)
    fun ListError()
    fun QuoteUpdate(num: Int, stockItem: StockItem)
    fun getFirst(): Int
}