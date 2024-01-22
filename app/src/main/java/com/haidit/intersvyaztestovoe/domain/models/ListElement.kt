package com.haidit.intersvyaztestovoe.domain.models

data class ListElement(
    var name: String,
    var description: String,
    val picture: String,
    var id: Int = 0
)

fun makeLine(listElement: ListElement): String {
    listElement.description = listElement.description.replace("\n", "")
    return listElement.id.toString() + ";" + listElement.name + ";" + listElement.description + ";" + listElement.picture + "\n"
}
