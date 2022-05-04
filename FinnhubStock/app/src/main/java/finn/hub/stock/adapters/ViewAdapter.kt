package finn.hub.stock.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import finn.hub.stock.R
import finn.hub.stock.StockItem
import finn.hub.stock.databinding.StockItemBinding

class ViewAdapter(var mList: ArrayList<StockItem>, val layoutManager: LinearLayoutManager):
    RecyclerView.Adapter<ViewAdapter.StockViewHolder>() {
    private val TAG = this.javaClass.name

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stock_item, parent, false)
        return StockViewHolder(view)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(mList.get(position))
        val first = getFirst()
        val last = getLast()
        val size = last - first
        Log.i(TAG, "onBindViewHolder: $first\t$last\t$size")
    }

    override fun getItemCount(): Int = mList.size

    fun getFirst(): Int = if(layoutManager.findFirstVisibleItemPosition() < 0) 0 else layoutManager.findFirstVisibleItemPosition()
    fun getLast(): Int = if(layoutManager.findLastVisibleItemPosition() < 0) 0 else layoutManager.findLastVisibleItemPosition()
    fun getVisibleArray(): ArrayList<StockItem>?{
        val outList = ArrayList<StockItem>()
        val first = getFirst()
        val last = getLast()
        for(i:Int in 0 until (last - first)){
            outList.add(i, mList[first + i])
        }
        return outList
    }

    inner class StockViewHolder(item: View): RecyclerView.ViewHolder(item){
        val binding = StockItemBinding.bind(item)
        fun bind(stockItem: StockItem){
            binding.stockCompName.text = stockItem.description
            binding.stockTick.text = stockItem.tick
            binding.currentQuote.text = stockItem.getCurrQuote().toString()
            binding.quoteDif.text = stockItem.getQuoteDiff().toString()
            binding.quoteDifPercent.text = stockItem.getQuoteDiffPercent().toString()
            binding.stockCompName.isSelected = true
        }
    }
}