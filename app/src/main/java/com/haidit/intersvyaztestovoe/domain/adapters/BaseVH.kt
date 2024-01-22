package com.haidit.intersvyaztestovoe.domain.adapters

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.haidit.intersvyaztestovoe.utils.inflate

abstract class BaseVH<I>(parent: ViewGroup, @LayoutRes id: Int) : androidx.recyclerview.widget.RecyclerView.ViewHolder(parent.inflate(id)) {

    abstract fun bind(item: I)

}