package com.manoj.dlt.interfaces

import com.manoj.dlt.models.DeepLinkInfo

/**
 * Created by baldor on 17/6/18.
 */
interface DeepLinkHistoryUpdateListener
{
    abstract fun onUpdate(deeplinkInfoList: ArrayList<DeepLinkInfo>)
}