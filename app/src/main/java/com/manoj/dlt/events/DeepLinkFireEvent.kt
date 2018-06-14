package com.manoj.dlt.events

import com.manoj.dlt.models.DeepLinkInfo
import com.manoj.dlt.models.ResultType

class DeepLinkFireEvent {

    var resultType: ResultType? = null
        private set
    var deepLinkInfo: DeepLinkInfo? = null
        private set
    var failureReason: FAILURE_REASON? = null
        private set

    enum class FAILURE_REASON {
        NO_ACTIVITY_FOUND, IMPROPER_URI
    }

    constructor(resultType: ResultType, info: DeepLinkInfo) {
        deepLinkInfo = info
        this.resultType = resultType
    }

    constructor(resultType: ResultType, deepLinkInfo: DeepLinkInfo, failureReason: FAILURE_REASON) {
        this.deepLinkInfo = deepLinkInfo
        this.failureReason = failureReason
        this.resultType = resultType
    }

}
