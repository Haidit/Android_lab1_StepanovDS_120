package com.haidit.intersvyaztestovoe.domain.adapters.mainList

import com.haidit.intersvyaztestovoe.domain.models.ListElement

interface ListItemOnClickListener {

    fun onClicked(listElement: ListElement)

}