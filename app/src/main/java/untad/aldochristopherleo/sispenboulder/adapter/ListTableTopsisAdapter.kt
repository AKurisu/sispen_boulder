package untad.aldochristopherleo.sispenboulder.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import untad.aldochristopherleo.sispenboulder.R
import untad.aldochristopherleo.sispenboulder.data.SortedResult
import java.math.BigDecimal
import java.math.RoundingMode

class ListTableTopsisAdapter(private val list: ArrayList<SortedResult>? = null):
    RecyclerView.Adapter<ListTableTopsisAdapter.ListViewHolder>() {
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var headRow: TableRow = itemView.findViewById(R.id.headrow)

        var headRow2: TextView = itemView.findViewById(R.id.headrow2)
        private var headRow3: TextView = itemView.findViewById(R.id.headrow3)
        private var headRow4: TextView = itemView.findViewById(R.id.headrow4)
        private var headRow5: TextView = itemView.findViewById(R.id.headrow5)
        var listHeadRow = arrayListOf(headRow2, headRow3, headRow4, headRow5)

        private var row1: TextView = itemView.findViewById(R.id.row1)
        private var row2: TextView = itemView.findViewById(R.id.row2)
        private var row3: TextView = itemView.findViewById(R.id.row3)
        private var row4: TextView = itemView.findViewById(R.id.row4)
        private var row5: TextView = itemView.findViewById(R.id.row5)
        var listRow = arrayListOf(row1, row2, row3, row4, row5)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_table_topsis, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        if (!list.isNullOrEmpty()){
            if (position > 0){
                holder.headRow.visibility = View.GONE
            } else if (list[position].preferenceValue != null && list[position].result?.top == null){
                holder.listHeadRow.forEach { item ->
                    item.visibility = View.GONE
                }
                holder.headRow2.visibility = View.VISIBLE
            }
            if (list[position].result != null){
                holder.listRow.indices.forEach { index ->
                    if (index == 0){
                        holder.listRow[index].text = (list[position].name)!!.take(5)
                    } else if (index == 1){
                        holder.listRow[index].text = BigDecimal(list[position].result!!.top)
                            .setScale(2, RoundingMode.HALF_EVEN).toPlainString()
                    } else if (index == 2){
                        holder.listRow[index].text = BigDecimal(list[position].result!!.at)
                            .setScale(2, RoundingMode.HALF_EVEN).toPlainString()
                    } else if (index == 3){
                        holder.listRow[index].text = BigDecimal(list[position].result!!.bonus)
                            .setScale(2, RoundingMode.HALF_EVEN).toPlainString()
                    } else if (index == 4){
                        holder.listRow[index].text = BigDecimal(list[position].result!!.ab)
                            .setScale(2, RoundingMode.HALF_EVEN).toPlainString()
                    }
                }
            } else {
                holder.listRow.indices.forEach { index ->
                    if (index > 1) {
                        holder.listRow[index].visibility = View.GONE
                    } else if (index == 0){
                        holder.listRow[index].text = (list[position].name)!!.take(5)
                    } else if (index == 1){
                        holder.listRow[index].text = BigDecimal(list[position].preferenceValue!!)
                            .setScale(2, RoundingMode.HALF_EVEN).toPlainString()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (!list.isNullOrEmpty()){
            list.size
        } else 0
    }
}