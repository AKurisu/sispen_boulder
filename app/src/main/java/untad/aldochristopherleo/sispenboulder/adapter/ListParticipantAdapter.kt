package untad.aldochristopherleo.sispenboulder.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import untad.aldochristopherleo.sispenboulder.GradingActivity
import untad.aldochristopherleo.sispenboulder.R
import untad.aldochristopherleo.sispenboulder.data.Event
import untad.aldochristopherleo.sispenboulder.data.Result
import untad.aldochristopherleo.sispenboulder.data.SortedResult

class ListParticipantAdapter(
    private var list: ArrayList<SortedResult>,
    private var eventKey: String,
    private var event: Event
): RecyclerView.Adapter<ListParticipantAdapter.ListViewHolder>() {
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

        var btnEdit : ImageView = itemView.findViewById(R.id.btn_edit_result)
        var btnEditFive : ImageView = itemView.findViewById(R.id.btn_edit_result_five)

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
        var wall1IsAvailable = false
        var wall2IsAvailable = false
        var wall3IsAvailable = false
        var wall4IsAvailable = false
        var wall5IsAvailable = false

        for (item in result.listOfWall){
            if (item == 1) wall1IsAvailable = true
            else if (item == 2) wall2IsAvailable = true
            else if (item == 3) wall3IsAvailable = true
            else if (item == 4) wall4IsAvailable = true
            else if (item == 5) wall5IsAvailable = true
        }
        val wall1 = result.wall1
        val wall2 = result.wall2
        val wall3 = result.wall3
        val wall4 = result.wall4
        val wall5 = result.wall5
        val wallList = ArrayList<String>()

        val name = result.name.toString()
        var textWall5 = ""

        val textWall1 =  if (wall1IsAvailable){
            wallList.add("Dinding 1")
            inputText(wall1.at, wall1.ab)
        } else "-"
        val textWall2 =  if (wall2IsAvailable){
            wallList.add("Dinding 2")
            inputText(wall2.at, wall2.ab)
        } else "-"
        val textWall3 =  if (wall3IsAvailable){
            wallList.add("Dinding 3")
            inputText(wall3.at, wall3.ab)
        } else "-"
        val textWall4 =  if (wall4IsAvailable){
            wallList.add("Dinding 4")
            inputText(wall4.at, wall4.ab)
        } else "-"
        if (wall5IsAvailable) {
            wallList.add("Dinding 5")
            if (wall5 != null) {
                textWall5 = inputText(wall5.at, wall5.ab)
            }
        }

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
            holder.btnEdit.setOnClickListener {
                val list = wallList.toTypedArray()
                var choosenWall = ""
                MaterialAlertDialogBuilder(holder.tableLayout.context)
                    .setTitle("Silahkan Pilih Dinding Yang Akan Diubah")
                    .setSingleChoiceItems(list,-1) { _, which ->
                        choosenWall = list[which]
                    }
                    .setPositiveButton("OK"){_,_ ->

                    }
                    .show()
            }
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

            holder.btnEditFive.setOnClickListener {
                val thisList = wallList.toTypedArray()
                var choosenWall = ""
                MaterialAlertDialogBuilder(holder.tableLayout.context)
                    .setTitle("Silahkan Pilih Dinding Yang Akan Diubah")
                    .setSingleChoiceItems(thisList,-1) { _, which ->
                        choosenWall = thisList[which]
                    }
                    .setPositiveButton("OK"){_,_ ->
                        val intent = Intent(holder.itemView.context, GradingActivity::class.java)
                        var wallSend = Result()
                        when (choosenWall) {
                            "Dinding 1" -> wallSend = wall1
                            "Dinding 2" -> wallSend = wall2
                            "Dinding 3" -> wallSend = wall3
                            "Dinding 4" -> wallSend = wall4
                            "Dinding 5" -> wallSend = wall5
                        }

                        intent.putExtra(GradingActivity.EXTRA_EVENT_KEY, eventKey)
                        intent.putExtra(GradingActivity.EXTRA_EVENT, event)
                        intent.putExtra(GradingActivity.EXTRA_NAME, "PRESIDEN")
                        intent.putExtra(GradingActivity.EXTRA_ACTIVITY_FROM, "ADAPTER")
                        intent.putExtra("EXTRA_EDIT_PARTICIPANT_KEY", list[position].key)
                        intent.putExtra("EXTRA_EDIT_WALL_PICKED", choosenWall)
                        intent.putExtra("EXTRA_EDIT_RESULT", wallSend)
                        intent.putExtra("EXTRA_EDIT_NAME", result.name.toString())

                        holder.tableLayout.context.startActivity(intent)
                    }
                    .show()
            }

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
        }
        refreshCount++
        notifyDataSetChanged()
    }

    private fun inputText (at: Double, ab: Double) : String {
        return if (at == 0.0 && ab == 0.0){
            "-"
        } else if (at == 0.0){
            "z${ab.toInt()}"
        } else if (ab == 0.0){
            "t${at.toInt()}"
        } else "t${at.toInt()} z${ab.toInt()}"
    }
}