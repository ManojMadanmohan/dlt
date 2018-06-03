package com.manoj.dlt.ui.adapters

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable

import java.util.ArrayList

abstract class FilterableListAdapter<T:Any>(protected var _originalList: ArrayList<T>, defaultListEmpty: Boolean) : BaseAdapter(), Filterable {
    protected var _resultList: MutableList<T>
    protected var _searchString: String

    init {
        _resultList = ArrayList()
        if (!defaultListEmpty) {
            _resultList.addAll(_originalList)
        }
        _searchString = ""
    }

    override fun getCount(): Int {
        Log.d("deep", "size = " + _resultList.size)
        return _resultList.size
    }

    override fun getItem(i: Int): Any {
        Log.d("deep", "call for item " + i)
        return _resultList[i]
    }

    fun updateBaseData(baseData: ArrayList<T>) {
        _originalList = baseData
        updateResults(_searchString)
    }

    fun updateResults(searchString: CharSequence) {
        filter.filter(searchString)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val results = Filter.FilterResults()
                val resultList = getMatchingResults(charSequence)
                results.values = resultList
                results.count = resultList.size
                Log.d("deep", "filtering, cnt  = " + resultList.size)
                return results
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                _searchString = charSequence.toString()
                _resultList = filterResults.values as ArrayList<T>
                notifyDataSetChanged()
            }
        }
    }

    protected abstract fun getMatchingResults(constraint: CharSequence): ArrayList<T>
}
