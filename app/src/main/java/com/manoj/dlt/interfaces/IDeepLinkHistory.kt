package com.manoj.dlt.interfaces

import com.manoj.dlt.models.DeepLinkInfo

interface IDeepLinkHistory {
    abstract fun addLinkToHistory(deepLinkInfo: DeepLinkInfo)
    abstract fun removeLinkFromHistory(deepLink: String)
    abstract fun clearAllHistory()
    abstract fun getLinkHistoryFromFileSystem() : List<DeepLinkInfo>
}