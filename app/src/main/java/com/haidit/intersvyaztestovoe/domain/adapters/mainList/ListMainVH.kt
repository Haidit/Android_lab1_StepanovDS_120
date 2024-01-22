package com.haidit.intersvyaztestovoe.domain.adapters.mainList

import android.content.Context
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.haidit.intersvyaztestovoe.R
import com.haidit.intersvyaztestovoe.databinding.ListElementPodBinding
import com.haidit.intersvyaztestovoe.domain.adapters.BaseVH
import com.haidit.intersvyaztestovoe.domain.models.ListElement

class ListMainVH(
    private val context: Context,
    parent: ViewGroup,
    private val onClickListener: ListItemOnClickListener,
    private val starOnClickListener: ListItemOnClickListener
) : BaseVH<ListElement>(parent, R.layout.list_element_pod) {

    private lateinit var binding: ListElementPodBinding

    override fun bind(item: ListElement) {
        binding = ListElementPodBinding.bind(itemView)
        with(binding) {
            itemName.text = item.name
            itemDesc.text = item.description
            favouritesButton.setOnClickListener { starOnClickListener.onClicked(item) }
            val url = item.picture
            Glide.with(context).load(url).override(512, 512).into(binding.itemImage)
            itemView.setOnClickListener { onClickListener.onClicked(item) }
            if (item.id.toString() in getArrayListFromSharedPreferences("ids")) {
                favouritesButton.setImageResource(R.drawable.baseline_star_24)
            } else {
                favouritesButton.setImageResource(R.drawable.baseline_star_border_purple500_24)
            }
        }
    }

    private fun getArrayListFromSharedPreferences(key: String): ArrayList<String> {
        val sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

        val mySet = sharedPreferences.getStringSet(key, emptySet())

        val myArrayList = arrayListOf<String>()
        myArrayList.addAll(mySet!!)

        return myArrayList
    }

}