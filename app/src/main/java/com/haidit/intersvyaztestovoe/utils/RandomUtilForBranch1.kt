package com.haidit.intersvyaztestovoe.utils

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.notRandomUtil(text: String) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()

}

