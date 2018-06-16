package com.manoj.dlt.utils

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView

interface OnTextViewForward : TextView.OnEditorActionListener {
    override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        return if(isForwardAction(p1)) onForward() else false
    }

    abstract fun onForward() : Boolean

    private fun isForwardAction(actionId: Int): Boolean {
        return if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_NEXT) {
            true
        } else false
    }
}