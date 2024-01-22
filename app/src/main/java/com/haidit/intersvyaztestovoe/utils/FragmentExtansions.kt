package com.haidit.intersvyaztestovoe.utils

import android.content.Context
import androidx.fragment.app.Fragment

fun Fragment.saveArrayListToSharedPreferences(key: String, arrayList: ArrayList<String>) {
    val mySet = arrayList.toSet()

    val sharedPreferences =
        requireActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

    val editor = sharedPreferences.edit()

    editor.putStringSet(key, mySet)

    editor.apply()
}
fun Fragment.getArrayListFromSharedPreferences(key: String): ArrayList<String> {
    val sharedPreferences =
        requireActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

    val mySet = sharedPreferences.getStringSet(key, emptySet())

    val myArrayList = arrayListOf<String>()
    myArrayList.addAll(mySet!!)

    return myArrayList
}