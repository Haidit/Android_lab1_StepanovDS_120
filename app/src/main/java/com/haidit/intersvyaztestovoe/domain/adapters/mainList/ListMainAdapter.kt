package com.haidit.intersvyaztestovoe.domain.adapters.mainList

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.haidit.intersvyaztestovoe.domain.models.ListElement


class ListMainAdapter(
    private val context: Context,
    private var listElements: ArrayList<ListElement>,
    private val onClickListener: ListItemOnClickListener,
    private val starOnClickListener: ListItemOnClickListener

) : RecyclerView.Adapter<ListMainVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListMainVH {
        return ListMainVH(context, parent, onClickListener, starOnClickListener)
    }

    fun filterList(filterList: ArrayList<ListElement>) {
        listElements = filterList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ListMainVH, position: Int) =
        holder.bind(listElements[position])

    override fun getItemCount(): Int = listElements.size

}