package untad.aldochristopherleo.sispenboulder.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import untad.aldochristopherleo.sispenboulder.EventActivity
import untad.aldochristopherleo.sispenboulder.data.Event
import untad.aldochristopherleo.sispenboulder.databinding.ItemEventListBinding
import untad.aldochristopherleo.sispenboulder.util.DateConverter

class ListEventsAdapter(options: FirebaseRecyclerOptions<Event>) :
    FirebaseRecyclerAdapter<Event, ListEventsAdapter.ListViewHolder>(options) {

//    private var listEvents = ArrayList<Event>()

//    internal fun setEvent(event: List<Event>?){
//        if (event == null) return
//        this.listEvents.clear()
//        this.listEvents.addAll(event)
//    }

    class ListViewHolder(private val binding: ItemEventListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) {
            with(binding){
                val converter = DateConverter(event.date)
                itemContestName.text = event.name
                itemContestDate.text = converter.getHomeDate()
                itemContestTime.text = converter.getHomeTime()
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view = ItemEventListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(view)
    }

//    override fun onBindViewHolder(holder: ListEventsAdapter.ListViewHolder, position: Int) {
//        val event = listEvents[position]
//        holder.bind(event)
//    }
//    override fun getItemCount(): Int = listEvents.size
    override fun onBindViewHolder(holder: ListViewHolder, position: Int, model: Event) {

        holder.bind(model)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, EventActivity::class.java)
            intent.putExtra(EventActivity.EXTRA_EVENT, model)
            holder.itemView.context.startActivity(intent)
        }
    }

}