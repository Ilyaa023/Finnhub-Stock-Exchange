package finn.hub.stock.finnhub

import android.util.Log
import finn.hub.stock.StockItem
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ApiClient
import java.lang.Exception

object FinnhubReceiver {
    private val TAG = this.javaClass.name
    private var stockItems = ArrayList<StockItem>()
    private var bufferItem: StockItem? = null
    private var currItemNum = 0
    private var isGetting = false

    fun Start(finnhubCallback: FinnhubCallback){
        ApiClient.apiKey["token"] = "sandbox_c8qqrr2ad3ienapjs1ag"
        val apiClient = DefaultApi()
        Thread(Runnable {
            try {
                val symbolsList = apiClient.stockSymbols("US", "", "", "")
                val listSize = symbolsList.size
                val itemList = ArrayList<StockItem>()
                for (i: Int in 0 until listSize) {
                    itemList.add(i, StockItem(symbolsList[i]))
                }
                finnhubCallback.ListOfStock(itemList)
            }
            catch (e: Exception){
                finnhubCallback.ListError()
            }
        }).start()

        Thread(Runnable {
            while(true){
                isGetting = true
                Log.i(TAG, "Start: request $currItemNum,\t${stockItems.size}")
                val first = finnhubCallback.getFirst()
                if (stockItems.size > currItemNum)
                    if (stockItems[currItemNum] != null){
                        try {
                            val quote = apiClient.quote(stockItems[currItemNum].tick!!)
                            Log.i(TAG, "Start: $quote, ${first + currItemNum}")
                            finnhubCallback.QuoteUpdate(first + currItemNum, StockItem(stockItems[currItemNum], quote))
                        }
                        catch (e: Exception){
                            Log.e(TAG, "StartQuoteUpdate: ${e.message}")
                        }
                    }
                isGetting = false
                if (bufferItem != null){
                    stockItems.set(currItemNum, bufferItem!!)
                    bufferItem = null
                    currItemNum = 0
                }else if (currItemNum < stockItems.size)
                    currItemNum++
                else
                    currItemNum = 0
                Thread.sleep(100)
            }
        }).start()
    }

    fun setList(list: ArrayList<StockItem>){
        try {
            Log.i(TAG, "setList: call!")
            if (!isGetting) {
                stockItems = list
                //currItemNum = 0
            } else {
                val size = if (stockItems.size > list.size) stockItems.size else list.size
                for (i: Int in 0..size)
                    if (i == currItemNum)
                        bufferItem = list[i]
                    else
                        stockItems.set(i, list[i])
            }
        }catch (e: Exception){
            Log.e(TAG, "setList: ${e.message}")
        }
    }

    public fun getStoskItems(): ArrayList<StockItem> = stockItems
}