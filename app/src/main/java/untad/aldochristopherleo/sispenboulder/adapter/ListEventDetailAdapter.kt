package untad.aldochristopherleo.sispenboulder.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import untad.aldochristopherleo.sispenboulder.EventActivity
import untad.aldochristopherleo.sispenboulder.GradingActivity
import untad.aldochristopherleo.sispenboulder.R
import untad.aldochristopherleo.sispenboulder.data.Event
import untad.aldochristopherleo.sispenboulder.util.DateConverter

class ListEventDetailAdapter(private val event: ArrayList<Event>, private val eventKeys: ArrayList<String>,
private val userName: String, private val userType: String):
    RecyclerView.Adapter<ListEventDetailAdapter.ListViewHolder>() {
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var name : TextView = itemView.findViewById(R.id.item_contest_name_detail)
        var date : TextView = itemView.findViewById(R.id.item_contest_date_detail)
        var time : TextView = itemView.findViewById(R.id.item_contest_time_detail)
        var location : TextView = itemView.findViewById(R.id.item_contest_location_detail)
        var cardView : CardView = itemView.findViewById(R.id.cardview_detail_event)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event_detail_list, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val name = event[position].name
        holder.name.text = name
        val date = DateConverter(event[position].date)
        holder.date.text = date.getHomeDate()
        holder.time.text = date.getHomeTime()
        holder.location.text = event[position].location

        if (event[position].status == "PERSIAPAN"){
            holder.cardView.setCardBackgroundColor(Color.YELLOW)
        } else if (event[position].status == "LOMBA"){
            holder.cardView.setCardBackgroundColor(Color.GREEN)
        } else holder.cardView.setCardBackgroundColor(Color.RED)

        holder.itemView.setOnClickListener {

            if (userType == "Juri Lapangan"){
                if (event[position].status == "PERSIAPAN"){
                    Toast.makeText(holder.cardView.context, "Lomba Belum Dimulai", Toast.LENGTH_SHORT).show()
                } else if (event[position].status == "LOMBA"){
                    var judging = false
                    if (event[position].judges != null){
                        event[position].judges?.forEach { _, judge ->
                            if (judge.name == userName){
                                judging = true
                            }
                        }
                    }
                    if (judging){
                        val intent = Intent(holder.itemView.context, GradingActivity::class.java)
                        intent.putExtra(GradingActivity.EXTRA_EVENT_KEY, eventKeys[position])
                        intent.putExtra(GradingActivity.EXTRA_EVENT, event[position])
                        intent.putExtra(GradingActivity.EXTRA_NAME, userName)
                        intent.putExtra(GradingActivity.EXTRA_ACTIVITY_FROM, "EVENT")
                        holder.cardView.context.startActivity(intent)
                    } else {
                        Toast.makeText(holder.cardView.context, "Anda Tidak Menilai Pada Perlombaan Ini", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                if (userType == "Presiden Juri"){
                    if (event[position].president != userName){
                        Toast.makeText(holder.cardView.context, "Anda Tidak Memiliki Hak Untuk Mengakses Perlombaan Ini", Toast.LENGTH_SHORT).show()
                    } else {
                        val intent = Intent(holder.itemView.context, EventActivity::class.java)
                        intent.putExtra(EventActivity.EXTRA_EVENT_KEY, eventKeys[position])
                        intent.putExtra(EventActivity.EXTRA_EVENT, event[position])
                        holder.itemView.context.startActivity(intent)
                    }
                } else {
                    val intent = Intent(holder.itemView.context, EventActivity::class.java)
                    intent.putExtra(EventActivity.EXTRA_EVENT_KEY, eventKeys[position])
                    intent.putExtra(EventActivity.EXTRA_EVENT, event[position])
                    holder.itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int = event.size
}