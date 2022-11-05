package untad.aldochristopherleo.sispenboulder.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import untad.aldochristopherleo.sispenboulder.R
import untad.aldochristopherleo.sispenboulder.data.SortedResult

class ListParticipantAdapter(
    private var list: ArrayList<SortedResult>): RecyclerView.Adapter<ListParticipantAdapter.ListViewHolder>() {
    private var refreshCount = 0
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var numberPosition : TextView = itemView.findViewById(R.id.txt_position)
        var layoutName : LinearLayout = itemView.findViewById(R.id.layout_name)
        var nameNull : TextView = itemView.findViewById(R.id.txt_nama_null)
        var posNull : TextView = itemView.findViewById(R.id.txt_position_null)
        var name : TextView = itemView.findViewById(R.id.txt_nama)
        var totalAttempt : TextView = itemView.findViewById(R.id.txt_total)
        var total : TextView = itemView.findViewById(R.id.txt_attempt_total)
        var top : TextView = itemView.findViewById(R.id.txt_topResult)
        var at : TextView = itemView.findViewById(R.id.txt_atResult)
        var bonus : TextView = itemView.findViewById(R.id.txt_bonusResult)
        var ab : TextView = itemView.findViewById(R.id.txt_abResult)
        var tableLayout : TableLayout = itemView.findViewById(R.id.table_result)
        var tableLayoutFive :TableLayout = itemView.findViewById(R.id.table_result_five_wall)

        var numberPositionFive : TextView = itemView.findViewById(R.id.txt_position_five)
        var nameFive : TextView = itemView.findViewById(R.id.txt_nama_five)

        var totalFive : TextView = itemView.findViewById(R.id.txt_attempt_total_five)
        var totalAttemptFive : TextView = itemView.findViewById(R.id.txt_total_five)
        var wall1Five : TextView = itemView.findViewById(R.id.txt_wall1_five)
        var wall2Five : TextView = itemView.findViewById(R.id.txt_wall2_five)
        var wall3Five : TextView = itemView.findViewById(R.id.txt_wall3_five)
        var wall4Five : TextView = itemView.findViewById(R.id.txt_wall4_five)
        var wall5Five : TextView = itemView.findViewById(R.id.txt_wall5_five)
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
        val wall5 = result.wall5

        val name = result.name.toString()
        var textWall5 = ""

        val textWall1 =  if (wall1.at != 0.0 || wall1.ab != 0.0 || wall1.top != 0.0 || wall1.bonus != 0.0){
            "t" + wall1.at.toInt().toString() + " " + "z" + wall1.ab.toInt().toString()
        } else "-"
        val textWall2 =  if (wall2.at != 0.0 || wall2.ab != 0.0 || wall2.top != 0.0 || wall2.bonus != 0.0){
            "t" + wall2.at.toInt().toString() + " " + "z" + wall2.ab.toInt().toString()
        } else "-"
        val textWall3 =  if (wall3.at != 0.0 || wall3.ab != 0.0 || wall3.top != 0.0 || wall3.bonus != 0.0){
            "t" + wall3.at.toInt().toString() + " " + "z" + wall3.ab.toInt().toString()
        } else "-"
        val textWall4 =  if (wall4.at != 0.0 || wall4.ab != 0.0 || wall4.top != 0.0 || wall4.bonus != 0.0){
            "t" + wall4.at.toInt().toString() + " " + "z" + wall4.ab.toInt().toString()
        } else "-"
        if (wall5 != null)  textWall5 = inputText(wall5.at, wall5.ab)

        val positionResult = result.position.toString()

        val top = result.result!!.top.toInt().toString()
        val at = result.result.at.toInt().toString()
        val bonus = result.result.bonus.toInt().toString()
        val ab = result.result.ab.toInt().toString()

        val textTotal = "$top + $bonus"
        val textTotalAttempt = "t$at z$ab"
        Log.d("onBindViewHolder: ", textTotal)


        if (top == "0" && at == "0" && bonus == "0" && ab == "0"){
            holder.posNull.text = positionResult
            holder.nameNull.text = name

            holder.layoutName.visibility = View.VISIBLE
            holder.tableLayout.visibility = View.GONE
            holder.tableLayoutFive.visibility = View.GONE
        } else if (wall5 == null) {
            holder.layoutName.visibility = View.GONE
            holder.tableLayoutFive.visibility = View.GONE

            holder.numberPosition.text = positionResult
            holder.name.text = name

            holder.top.text = textWall1
            holder.at.text = textWall2
            holder.bonus.text = textWall3
            holder.ab.text = textWall4

            holder.totalAttempt.text = textTotalAttempt
            holder.total.text = textTotal
        } else {
            holder.layoutName.visibility = View.GONE
            holder.tableLayout.visibility = View.GONE

            holder.numberPositionFive.text = positionResult
            holder.nameFive.text = name

            holder.wall1Five.text = textWall1
            holder.wall2Five.text = textWall2
            holder.wall3Five.text = textWall3
            holder.wall4Five.text = textWall4
            holder.wall5Five.text = textWall5


            holder.totalAttemptFive.text = textTotalAttempt
            holder.totalFive.text = textTotal
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

    private fun inputText (at: Double, ab: Double) : String {
        return if (at == 0.0 && ab == 0.0){
            "-"
        } else if (at == 0.0){
            "z$ab"
        } else if (ab == 0.0){
            "t$at"
        } else "t$at z$ab"
    }
}