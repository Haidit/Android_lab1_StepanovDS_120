package com.haidit.intersvyaztestovoe.ui.list

import androidx.lifecycle.ViewModel
import com.haidit.intersvyaztestovoe.domain.models.ListElement
import org.json.JSONArray
import org.json.JSONObject

class ListViewModel : ViewModel() {

    val list = ArrayList<ListElement>()

    var ids = ArrayList<String>()

    fun parseData(result: String) {
        val mainObjectsList = JSONArray(result)

        for (i in 0 until mainObjectsList.length()) {
            val itemData = mainObjectsList[i] as JSONObject
            val item = ListElement(
                itemData.getString("name"),
                itemData.getString("desc"),
                itemData.getString("picture"),
                itemData.getString("id").toInt()
            )
            list.add(item)
        }
    }

}