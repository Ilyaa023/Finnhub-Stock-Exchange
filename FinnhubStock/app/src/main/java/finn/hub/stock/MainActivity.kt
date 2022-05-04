package finn.hub.stock

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import finn.hub.stock.adapters.ViewAdapter
import finn.hub.stock.databinding.ActivityMainBinding
import finn.hub.stock.finnhub.FinnhubCallback
import finn.hub.stock.finnhub.FinnhubReceiver

class MainActivity : AppCompatActivity(), FinnhubCallback {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewAdapter: ViewAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val activity: Activity = this
    private var stockItems = ArrayList<StockItem>()
    private var visibleLast: Int? = 0

    private val TAG = this.javaClass.name
    //private val receiver = FinnhubReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        linearLayoutManager = LinearLayoutManager(this)
        viewAdapter = ViewAdapter(stockItems, linearLayoutManager)
        binding.refreshBtn.setOnClickListener(View.OnClickListener {
            binding.refreshBtn.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            FinnhubReceiver.Start(this)
        })
        FinnhubReceiver.Start(this)
        Thread(Runnable {
            while (true){
                val last = viewAdapter.getLast()
                Log.i(TAG, "onCreate: $last")
                if (visibleLast != last){
                    FinnhubReceiver.setList(viewAdapter.getVisibleArray()!!)
                    visibleLast = last
                }
                Thread.sleep(500)
            }
        }).start()
    }

    override fun ListOfStock(stockItems: ArrayList<StockItem>) {
        activity.runOnUiThread(Runnable {
            this.stockItems = stockItems
            binding.recycleView.setHasFixedSize(false)
            binding.recycleView.layoutManager = linearLayoutManager
            viewAdapter = ViewAdapter(stockItems, linearLayoutManager)
            binding.recycleView.adapter = viewAdapter
            binding.progressLayout.visibility = View.GONE
        })
    }

    override fun ListError() {
        activity.runOnUiThread(Runnable {
            Toast.makeText(applicationContext, "Error"/*e.message*/, Toast.LENGTH_LONG).show()
            binding.refreshBtn.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        })
    }

    override fun QuoteUpdate(num: Int, stockItem: StockItem) {
        activity.runOnUiThread(Runnable {
            try {
                if (stockItem != stockItems[num]){
                    stockItems[num] = stockItem
                    viewAdapter.notifyItemChanged(num)
                }
            }catch (e: Exception){
                Log.e(TAG, "QuoteUpdate: ${e.message}", )
            }
        })
    }

    override fun getFirst(): Int = viewAdapter.getFirst()
}