package com.manoj.dlt.utils

import android.widget.EditText

fun EditText.hasText() : Boolean {
    return this.text != null &&
            this.text.length > 0;
}

fun EditText.getTextStr(): String {
    return this.text.toString();
}

fun EditText.setTextWithSelection(text: CharSequence) {
    this.setText(text);
    this.setSelection(text.length)
}