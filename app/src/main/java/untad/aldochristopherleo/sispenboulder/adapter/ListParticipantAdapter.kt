package untad.aldochristopherleo.sispenboulder.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import untad.aldochristopherleo.sispenboulder.R
import untad.aldochristopherleo.sispenboulder.data.Alternative
import untad.aldochristopherleo.sispenboulder.data.Event
import untad.aldochristopherleo.sispenboulder.data.Result
import untad.aldochristopherleo.sispenboulder.data.SortedResult
import untad.aldochristopherleo.sispenboulder.databinding.ItemParticipantListBinding

class ListParticipantAdapter(private val list: ArrayList<SortedResult>): RecyclerView.Adapter<ListParticipantAdapter.ListViewHolder>() {
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name : TextView = itemView.findViewById(R.id.txt_nama)
        var top : TextView = itemView.findViewById(R.id.txt_topResult)
        var at : TextView = itemView.findViewById(R.id.txt_atResult)
        var bonus : TextView = itemView.findViewById(R.id.txt_bonusResult)
        var ab : TextView = itemView.findViewById(R.id.txt_abResult)
        var tableLayout : TableLayout = itemView.findViewById(R.id.table_result)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_participant_list, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var name = ""
        val result = list[position]
        name = result.name.toString()
        val top = result.result!!.top.toString()
        val at = result.result.at.toString()
        val bonus = result.result.bonus.toString()
        val ab = result.result.ab.toString()

        holder.name.text = "${position + 1}. $name"

        if (top == "0.0" && at == "0.0" && bonus == "0.0" && ab == "0.0"){
            holder.tableLayout.visibility = View.GONE
        } else {
            holder.top.text = top
            holder.at.text = at
            holder.bonus.text = bonus
            holder.ab.text = ab
        }
    }

    override fun getItemCount(): Int = list.size
}