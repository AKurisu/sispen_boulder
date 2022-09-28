package untad.aldochristopherleo.sispenboulder.util

import androidx.recyclerview.widget.DiffUtil
import untad.aldochristopherleo.sispenboulder.data.SortedResult

class ResultDiffCallback(
    private val oldList: ArrayList<SortedResult>,
    private val newList: ArrayList<SortedResult>
    ) : DiffUtil.Callback(){

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return oldItem.name == newItem.name && oldItem.result == newItem.result
    }
}