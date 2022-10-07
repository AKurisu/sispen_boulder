package untad.aldochristopherleo.sispenboulder.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import untad.aldochristopherleo.sispenboulder.R
import untad.aldochristopherleo.sispenboulder.data.Participant

class ListUserParticipant(private val list: ArrayList<Participant>? = null, private  val key: ArrayList<String>):
    RecyclerView.Adapter<ListUserParticipant.ListViewHolder>() {

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textHolder: TextView = itemView.findViewById(R.id.txt_name)
        var buttonHolder: ImageView = itemView.findViewById(R.id.btn_hapus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_participant, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        if (!list.isNullOrEmpty()){
            val text = (position + 1).toString() + ". "  + list[position].name
            val context = holder.buttonHolder.context

            holder.textHolder.text = text

            holder.buttonHolder.setOnClickListener{
                MaterialAlertDialogBuilder(context)
                    .setTitle("Apakah Anda Yakin Ingin Menghapus Data?")
                    .setMessage(list[position].name)
                    .setPositiveButton("Ya"){_,_ ->
                        Firebase.database.reference.child("participant/"+key[position]).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton("Tidak"){_,_ ->

                    }
                    .show()
            }
        }
    }
    override fun getItemCount(): Int {
        return if (!list.isNullOrEmpty()){
            list.size
        } else 0
    }

}