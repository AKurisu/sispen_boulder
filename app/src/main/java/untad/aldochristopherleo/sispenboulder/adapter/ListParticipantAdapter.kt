package untad.aldochristopherleo.sispenboulder.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import untad.aldochristopherleo.sispenboulder.R
import untad.aldochristopherleo.sispenboulder.data.SortedResult
import untad.aldochristopherleo.sispenboulder.data.Result
import untad.aldochristopherleo.sispenboulder.util.ResultDiffCallback

class ListParticipantAdapter(
    private var list: ArrayList<SortedResult>): RecyclerView.Adapter<ListParticipantAdapter.ListViewHolder>() {
    private var refreshCount = 0
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var numberPosition : TextView = itemView.findViewById(R.id.txt_position)
        var layoutName : LinearLayout = itemView.findViewById(R.id.layout_name)
        var nameNull : TextView = itemView.findViewById(R.id.txt_nama_null)
        var posNull : TextView = itemView.findViewById(R.id.txt_position_null)
        var name : TextView = itemView.findViewById(R.id.txt_nama)
        var total : TextView = itemView.findViewById(R.id.txt_total)
        var attemptTotal : TextView = itemView.findViewById(R.id.txt_attempt_total)
        var top : TextView = itemView.findViewById(R.id.txt_topResult)
        var at : TextView = itemView.findViewById(R.id.txt_atResult)
        var bonus : TextView = itemView.findViewById(R.id.txt_bonusResult)
        var ab : TextView = itemView.findViewById(R.id.txt_abResult)
        var tableLayout : TableLayout = itemView.findViewById(R.id.table_result)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        Log.d("onCreateViewHolder: ", list.size.toString())
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_participant_list, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {

        Log.d("onBindViewHolder: ", list.size.toString())
        val result = list[position]
        val wall1 = result.wall1
        val wall2 = result.wall2
        val wall3 = result.wall3
        val wall4 = result.wall4

        val name = result.name.toString()

        val textWall1 =  if (wall1.at != 0.0 || wall1.ab != 0.0 || wall1.top != 0.0 || wall1.bonus != 0.0){
            "t" + wall1.top.toInt().toString() + " " + "z" + wall1.bonus.toInt().toString()
        } else ""
        val textWall2 =  if (wall2.at != 0.0 || wall2.ab != 0.0 || wall2.top != 0.0 || wall2.bonus != 0.0){
            "t" + wall2.top.toInt().toString() + " " + "z" + wall2.bonus.toInt().toString()
        } else ""
        val textWall3 =  if (wall3.at != 0.0 || wall3.ab != 0.0 || wall3.top != 0.0 || wall3.bonus != 0.0){
            "t" + wall3.top.toInt().toString() + " " + "z" + wall3.bonus.toInt().toString()
        } else ""
        val textWall4 =  if (wall4.at != 0.0 || wall4.ab != 0.0 || wall4.top != 0.0 || wall4.bonus != 0.0){
            "t" + wall4.top.toInt().toString() + " " + "z" + wall4.bonus.toInt().toString()
        } else ""


        val top = result.result!!.top.toInt().toString()
        val at = result.result.at.toInt().toString()
        val bonus = result.result.bonus.toInt().toString()
        val ab = result.result.ab.toInt().toString()

        val textTotal = "$top $bonus"
        val textTotalAttempt = "$at $ab"
        Log.d("onBindViewHolder: ", textTotal)

        holder.numberPosition.text = (position + 1).toString()
        holder.name.text = name
        holder.posNull.text = (position + 1).toString()
        holder.nameNull.text = name

        if (top == "0" && at == "0" && bonus == "0" && ab == "0"){
            holder.layoutName.visibility = View.VISIBLE
            holder.tableLayout.visibility = View.GONE
        } else {
            holder.layoutName.visibility = View.GONE

            holder.top.text = textWall1
            holder.at.text = textWall2
            holder.bonus.text = textWall3
            holder.ab.text = textWall4

            holder.total.text = textTotal
            holder.attemptTotal.text = textTotalAttempt
        }
    }

    override fun getItemCount(): Int = list.size

//    fun refreshData(newList: ArrayList<SortedResult>){
//        val diffCallback = ResultDiffCallback(list, newList)
//        val diffResult = DiffUtil.calculateDiff(diffCallback)
//
//        list.clear()
//        list.addAll(newList)
//
//        diffResult.dispatchUpdatesTo(this)
//    }

    fun addAll(
        newList: ArrayList<SortedResult>
    ){
        Log.d("onBindViewHolder: ", "refreshCount: $refreshCount")
        if (refreshCount == 0){
            list = newList
            refreshCount++
            notifyDataSetChanged()
        } else {
            refreshCount++
            if (list != newList){
                Log.d("onBindViewHolder: ", "list != newlist")
                notifyDataSetChanged()
//                list = newList

            }
        }

    }
}