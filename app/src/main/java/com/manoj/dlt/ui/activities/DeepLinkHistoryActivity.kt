package com.manoj.dlt.ui.activities

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.manoj.dlt.Constants
import com.manoj.dlt.DbConstants
import com.manoj.dlt.R
import com.manoj.dlt.events.DeepLinkFireEvent
import com.manoj.dlt.features.DeepLinkHistoryFeature
import com.manoj.dlt.features.ProfileFeature
import com.manoj.dlt.interfaces.DeepLinkHistoryUpdateListener
import com.manoj.dlt.models.DeepLinkInfo
import com.manoj.dlt.models.ResultType
import com.manoj.dlt.ui.ConfirmShortcutDialog
import com.manoj.dlt.ui.TutorialUtil
import com.manoj.dlt.ui.adapters.DeepLinkListAdapter
import com.manoj.dlt.utils.*
import hotchemi.android.rate.AppRate
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList

class DeepLinkHistoryActivity: AppCompatActivity() {

    val TAG_DIALOG = "dialog"
    private var _listView: ListView? = null
    private var _fabMenu: FloatingActionsMenu? = null
    private var _deepLinkInput: EditText? = null
    private var _privacyPolicy: TextView? = null
    private var _adapter: DeepLinkListAdapter? = null

    private var _presenter: DeepLinkHistoryPresenter = getPresenter();

    fun getPresenter(): DeepLinkHistoryPresenter {
        return DeepLinkHistoryPresenter(getHistoryUpdateListener())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deep_link_history)
        initView()
    }

    private fun initView() {
        _deepLinkInput = findViewById(R.id.deep_link_input) as EditText
        _listView = findViewById(R.id.deep_link_list_view) as ListView
        _privacyPolicy = findViewById(R.id.privacy_policy) as TextView
        _adapter = DeepLinkListAdapter(ArrayList(), this)
        configureDeepLinkInput()
        findViewById(R.id.deep_link_fire).setOnClickListener { extractAndFireLink() }
        setFabMenuActions()
        setAppropriateLayout()
        configureListView()
        configurePrivacyPolicy()
    }


    private fun setFabMenuActions() {
        setFabMenuOrientation()
        setFabListeners()
    }

    private fun setFabListeners() {
        _fabMenu!!.findViewById(R.id.fab_web).setOnClickListener {
            if (Constants.isFirebaseAvailable(this@DeepLinkHistoryActivity)) {
                val userId = ProfileFeature.getInstance(this@DeepLinkHistoryActivity).getUserId()
                Utilities.showAlert("Fire from your PC", "go to " + Constants.WEB_APP_LINK + userId, this@DeepLinkHistoryActivity)
            } else {
                Utilities.raiseError(getString(R.string.play_services_error), this@DeepLinkHistoryActivity)
            }
        }
        _fabMenu!!.findViewById(R.id.fab_share).setOnClickListener { Utilities.shareApp(this@DeepLinkHistoryActivity) }
        _fabMenu!!.findViewById(R.id.fab_rate).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.GOOGLE_PLAY_URI)))
            //Do not show app rate dialog anymore
            AppRate.with(this@DeepLinkHistoryActivity).setAgreeShowDialog(false)
        }
        _fabMenu!!.setOnFloatingActionsMenuUpdateListener(object : FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
            override fun onMenuExpanded() {
                setContentInFocus(true)
            }

            override fun onMenuCollapsed() {
                setContentInFocus(false)
            }
        })
    }

    private fun setFabMenuOrientation() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            _fabMenu = findViewById(R.id.fab_menu_vertical) as FloatingActionsMenu
        } else {
            _fabMenu = findViewById(R.id.fab_menu_horizontal) as FloatingActionsMenu
        }
        _fabMenu!!.setVisibility(View.VISIBLE)
    }

    private fun configureListView() {
        _listView!!.setAdapter(_adapter)
        _listView!!.setOnItemClickListener( { adapterView, view, position, l ->
            val (deepLink) = _adapter!!.getItem(position) as DeepLinkInfo
            _deepLinkInput!!.setTextWithSelection(deepLink)
        })
        _listView!!.setOnItemLongClickListener( { parent, view, position, id ->
            showConfirmShortcutDialog(_adapter!!.getItem(position) as DeepLinkInfo)
            true
        })
    }

    private fun configurePrivacyPolicy() {
        _privacyPolicy!!.setOnClickListener(object: View.OnClickListener {
            override fun onClick(p0: View?) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://manojmadanmohan.github.io/dlt/privacy_policy"))
                startActivity(browserIntent)
            }
        })
    }

    private fun showConfirmShortcutDialog(info: DeepLinkInfo) {
        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        // Create and show the dialog.
        ConfirmShortcutDialog.newInstance(info.deepLink, info.activityLabel).show(ft, TAG_DIALOG)
    }

    private fun configureDeepLinkInput() {
        _deepLinkInput!!.requestFocus()
        _deepLinkInput!!.setOnEditorActionListener(object: OnTextViewForward {
            override fun onForward(): Boolean {
                extractAndFireLink()
                return true
            }
        });
        _deepLinkInput!!.addTextChangedListener(object : TextChangedListener() {
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                _fabMenu!!.collapse()
                _adapter!!.updateResults(charSequence)
            }
        })
        _deepLinkInput!!.setOnClickListener(View.OnClickListener { _fabMenu!!.collapse() })
    }

    private fun pasteFromClipboard() {
        var copy = _presenter.getInputString(this, _deepLinkInput!!.getTextStr())
        _deepLinkInput!!.setTextWithSelection(copy)
    }

    private fun setAppropriateLayout() {
        showDeepLinkRootView()

        if (Utilities.isAppTutorialSeen(this)) {
            AppRate.showRateDialogIfMeetsConditions(this)
        } else {
            launchTutorial()
            Utilities.setAppTutorialSeen(this@DeepLinkHistoryActivity)
        }
    }

    private fun showDeepLinkRootView() {
        findViewById(R.id.deep_link_history_root).visibility = View.VISIBLE
        _deepLinkInput!!.requestFocus()
        Utilities.showKeyboard(this)
    }

    fun extractAndFireLink() {
        val deepLinkUri = _deepLinkInput!!.getTextStr()
        Utilities.checkAndFireDeepLink(deepLinkUri, this)
    }

    override fun onStart() {
        super.onStart()
        initListViewData()
        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()
        pasteFromClipboard()
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        _presenter.removeFirebaseListener(this)
        super.onStop()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onEvent(deepLinkFireEvent: DeepLinkFireEvent) {
        val deepLinkString = deepLinkFireEvent.deepLinkInfo!!.deepLink
        _deepLinkInput!!.setTextWithSelection(deepLinkString)
        if (deepLinkFireEvent.resultType == ResultType.SUCCESS) {
            _adapter!!.updateResults(deepLinkString)
        } else {
            if (DeepLinkFireEvent.FAILURE_REASON.NO_ACTIVITY_FOUND == deepLinkFireEvent.failureReason) {
                Utilities.raiseError(getString(R.string.error_no_activity_resolved) + ": " + deepLinkString, this)
            } else if (DeepLinkFireEvent.FAILURE_REASON.IMPROPER_URI == deepLinkFireEvent.failureReason) {
                Utilities.raiseError(getString(R.string.error_improper_uri) + ": " + deepLinkString, this)
            }
        }
        EventBus.getDefault().removeStickyEvent(deepLinkFireEvent)
    }

    override fun onBackPressed() {
        if (_fabMenu!!.isExpanded()) {
            _fabMenu!!.collapse()
        } else {
            super.onBackPressed()
        }
    }

    private fun initListViewData() {
        if (Constants.isFirebaseAvailable(this)) {
            //Attach callback to init adapter from data in firebase
            _presenter.attachFirebaseListener(this);
        } else {
            val deepLinkInfoList = DeepLinkHistoryFeature.getInstance(this).getLinkHistoryFromFileSystem()
            if (deepLinkInfoList.size > 0) {
                showShortcutBannerIfNeeded()
            }
            _adapter!!.updateBaseData(deepLinkInfoList)
            findViewById(R.id.progress_wheel).visibility = View.GONE
        }
        _adapter!!.updateResults(_deepLinkInput!!.getText().toString())
    }

    private fun launchTutorial() {
        val deepLinkInfo = DeepLinkInfo("deeplinktester://example", "Deep Link Tester", packageName, Date().time)

        val demoHeaderView = _adapter!!.createView(0, layoutInflater.inflate(R.layout.deep_link_info_layout, null, false), deepLinkInfo)
        demoHeaderView.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.White, theme))
        _listView!!.addHeaderView(demoHeaderView)

        TutorialUtil.showTutorial(this, findViewById(R.id.deep_link_input),
                findViewById(R.id.deep_link_fire), demoHeaderView,
                object : TapTargetSequence.Listener {
                    override fun onSequenceFinish() {
                        _listView!!.removeHeaderView(demoHeaderView)
                    }

                    override fun onSequenceCanceled(lastTarget: TapTarget) {
                        _listView!!.removeHeaderView(demoHeaderView)
                    }
                });
    }

    private fun getHistoryUpdateListener(): DeepLinkHistoryUpdateListener {
        return object: DeepLinkHistoryUpdateListener {
            override fun onUpdate(deepLinkInfos: ArrayList<DeepLinkInfo>) {
                _adapter!!.updateBaseData(deepLinkInfos)
                if (_deepLinkInput!!.hasText()) {
                    _adapter!!.updateResults(_deepLinkInput!!.getTextStr())
                }
                if (deepLinkInfos.size > 0) {
                    showShortcutBannerIfNeeded()
                }
            }
        }
    }

    private fun showShortcutBannerIfNeeded() {
        if (!Utilities.isShortcutHintSeen(this)) {
            findViewById(R.id.shortcut_hint_banner).visibility = View.VISIBLE
            findViewById(R.id.shortcut_hint_banner_cancel).setOnClickListener {
                Utilities.setShortcutBannerSeen(this@DeepLinkHistoryActivity)
                findViewById(R.id.shortcut_hint_banner).visibility = View.GONE
            }
        }
    }

    private fun setContentInFocus(hideFocus: Boolean) {
        val overlay = findViewById(R.id.list_focus_overlay)
        if (hideFocus) {
            overlay.visibility = View.VISIBLE
        } else {
            overlay.visibility = View.GONE
        }
        overlay.setOnClickListener { _fabMenu!!.collapse() }
    }

}