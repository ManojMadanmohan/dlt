package com.manoj.dlt.ui

import android.app.Activity
import android.view.View
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.manoj.dlt.R

object TutorialUtil{

    @JvmStatic
    fun showTutorial(activity: Activity, onboardingInput: View, onboardingLaunch: View, onboardingHistory: View, listener: TapTargetSequence.Listener)
    {
        TapTargetSequence(activity)
                .targets(TapTarget.forView(onboardingInput, activity.getString(R.string.onboarding_input_title))
                        .dimColor(android.R.color.black)
                        .outerCircleColor(R.color.SlateGray)
                        .targetCircleColor(R.color.fabColorNormal)
                        .tintTarget(false),
                        TapTarget.forView(onboardingLaunch, activity.getString(R.string.onboarding_launch_title))
                                .dimColor(android.R.color.black)
                                .outerCircleColor(R.color.SlateGray)
                                .targetCircleColor(R.color.fabColorNormal)
                                .tintTarget(false),
                        TapTarget.forView(onboardingHistory, activity.getString(R.string.onboarding_history_title))
                                .dimColor(android.R.color.black)
                                .outerCircleColor(R.color.SlateGray)
                                .targetCircleColor(R.color.fabColorNormal)
                                .tintTarget(false))
                .listener(listener)
                .start()
    }
}
