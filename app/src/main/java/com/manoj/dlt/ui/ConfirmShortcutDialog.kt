package com.manoj.dlt.ui

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.manoj.dlt.R
import com.manoj.dlt.utils.Utilities

class ConfirmShortcutDialog : DialogFragment() {

    private var _deepLink: String? = null
    private var _defaultLabel: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(DialogFragment.STYLE_NO_TITLE, 0)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        extractData()
        val view = inflater!!.inflate(R.layout.confirm_shortcut_dialog, container, false)
        initView(view)
        return view
    }

    private fun extractData() {
        _deepLink = arguments?.getString(KEY_DEEP_LINK)
        _defaultLabel = arguments?.getString(KEY_LABEL, "")
    }

    private fun initView(view: View) {
        val labelEditText = view.findViewById(R.id.shortcut_label) as EditText
        if (!TextUtils.isEmpty(_defaultLabel)) {
            labelEditText.setText(_defaultLabel)
            labelEditText.setSelection(_defaultLabel!!.length)
        }
        view.findViewById<View>(R.id.confirm_shortcut_negative).setOnClickListener { dismiss() }
        view.findViewById<View>(R.id.confirm_shortcut_positive).setOnClickListener {
            val shortcutAdded = Utilities.addShortcut(_deepLink!!, activity!!, labelEditText.text.toString())
            if (shortcutAdded) {
                Toast.makeText(activity, "shortcut added", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(activity, "could not add shortcut", Toast.LENGTH_LONG).show()
            }
            dismiss()
        }
    }

    companion object {
        private val KEY_DEEP_LINK = "key_deep_link"
        private val KEY_LABEL = "key_label"

        fun newInstance(deepLinkUri: String?, defaultLabel: String?): ConfirmShortcutDialog {
            val dialog = ConfirmShortcutDialog()

            val args = Bundle()
            args.putString(KEY_DEEP_LINK, deepLinkUri)
            args.putString(KEY_LABEL, defaultLabel)
            dialog.arguments = args

            return dialog
        }
    }
}
