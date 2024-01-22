package com.haidit.intersvyaztestovoe.ui.favourites

import androidx.lifecycle.ViewModel
import com.haidit.intersvyaztestovoe.domain.models.ListElement

class FavouritesViewModel : ViewModel() {

    var ids = ArrayList<String>()

    val list = ArrayList<ListElement>()
}