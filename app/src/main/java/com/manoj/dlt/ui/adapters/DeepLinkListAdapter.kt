package com.manoj.dlt.ui.adapters

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.manoj.dlt.R
import com.manoj.dlt.features.DeepLinkHistoryFeature
import com.manoj.dlt.models.DeepLinkInfo
import com.manoj.dlt.utils.Utilities

import java.util.ArrayList

class DeepLinkListAdapter(originalList: ArrayList<DeepLinkInfo>, private val _context: Context) : FilterableListAdapter<DeepLinkInfo>(originalList, false) {

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(i: Int, convertView: View?, viewGroup: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(_context).inflate(R.layout.deep_link_info_layout, viewGroup, false)
        }

        val deepLinkInfo = getItem(i) as DeepLinkInfo
        return createView(i, convertView!!, deepLinkInfo)
    }

    fun createView(position: Int, view: View, deepLinkInfo: DeepLinkInfo): View {
        val deepLink = deepLinkInfo.deepLink
        val deepLinkTitle = Utilities.colorPartialString(deepLink, deepLink.indexOf(_searchString), _searchString.length, _context.resources.getColor(R.color.Blue))
        Utilities.setTextViewText(view, R.id.deep_link_title, deepLinkTitle)
        Utilities.setTextViewText(view, R.id.deep_link_package_name, deepLinkInfo.packageName!!)
        Utilities.setTextViewText(view, R.id.deep_link_activity_name, deepLinkInfo.activityLabel!!)
        try {
            val icon = _context.packageManager.getApplicationIcon(deepLinkInfo.packageName)
            (view.findViewById(R.id.deep_link_icon) as ImageView).setImageDrawable(icon)
        } catch (exception: PackageManager.NameNotFoundException) {
            (view.findViewById(R.id.deep_link_icon) as ImageView).setImageDrawable(_context.resources.getDrawable(R.drawable.ic_warning_red_24_px))
        }

        view.findViewById(R.id.deep_link_remove).setOnClickListener {
            _originalList.removeAt(position)
            updateResults(_searchString)
            DeepLinkHistoryFeature.getInstance(_context).removeLinkFromHistory(deepLinkInfo.id)
        }
        return view
    }

    override fun getMatchingResults(constraint: CharSequence): ArrayList<DeepLinkInfo> {
        val prefixList = ArrayList<DeepLinkInfo>()
        val suffixList = ArrayList<DeepLinkInfo>()
        for (info in _originalList) {
            if (info.deepLink.startsWith(constraint.toString())) {
                prefixList.add(info)
            } else if (info.deepLink.contains(constraint)) {
                suffixList.add(info)
            }
        }
        prefixList.addAll(suffixList)
        return prefixList
    }
}
