package com.manoj.dlt.models

import android.net.Uri
import android.util.Log
import org.json.JSONException
import org.json.JSONObject

data class DeepLinkInfo(val deepLink: String, val activityLabel: String,
                        val packageName: String, val updatedTime: Long) : Comparable<DeepLinkInfo>
{
    val id = generateId()

    //unique id for each deep link entry. similar deep links, varying in query or fragments are combined
    private fun generateId(): String {
        val uri = Uri.parse(deepLink)
        var id = uri.toString()
        if (uri.fragment != null) {
            id = id.replace(uri.fragment, "").replace("#", "")
        }
        if (uri.query != null) {
            id = id.replace(uri.query, "").replace("?", "")
        }
        id = id.replace("/", "")
        //replace '.' since firebase does not support them in paths
        id = id.replace(".", "-dot-")
        return id
    }

    override fun compareTo(other: DeepLinkInfo): Int {
        if(other.updatedTime < updatedTime) {
            return 1
        } else {
            return -1
        }
    }

    companion object {

        @JvmStatic
        fun toJson(deepLinkInfo: DeepLinkInfo): String {
            try {
                val jsonObject = JSONObject()
                jsonObject.put(JSON_KEYS.KEY_DEEP_LINK, deepLinkInfo.deepLink)
                jsonObject.put(JSON_KEYS.KEY_ACTIVITY_LABEL, deepLinkInfo.activityLabel)
                jsonObject.put(JSON_KEYS.KEY_PACKAGE_NAME, deepLinkInfo.packageName)
                jsonObject.put(JSON_KEYS.KEY_UPDATED_TIME, deepLinkInfo.updatedTime)
                return jsonObject.toString()
            } catch (jsonException: JSONException) {
                return deepLinkInfo.id
            }

        }

        @JvmStatic
        fun fromJson(deepLinkJson: String): DeepLinkInfo? {
            Log.d("deeplink", "json string = " + deepLinkJson)
            try {
                val jsonObject = JSONObject(deepLinkJson)
                val deepLink = jsonObject.getString(JSON_KEYS.KEY_DEEP_LINK)
                val activityLable = jsonObject.getString(JSON_KEYS.KEY_ACTIVITY_LABEL)
                val packageName = jsonObject.getString(JSON_KEYS.KEY_PACKAGE_NAME)
                val updatedTime = jsonObject.getLong(JSON_KEYS.KEY_UPDATED_TIME)
                return DeepLinkInfo(deepLink, activityLable, packageName, updatedTime)
            } catch (jsonException: JSONException) {
                Log.d("deeplink", "returning null for deep lnk info, exception = " + jsonException)
                return null
            }

        }

    }

    private object JSON_KEYS {
        var KEY_DEEP_LINK = "deep_link"
        var KEY_PACKAGE_NAME = "pacakage_name"
        var KEY_ACTIVITY_LABEL = "label"
        var KEY_UPDATED_TIME = "update_time"
    }

}