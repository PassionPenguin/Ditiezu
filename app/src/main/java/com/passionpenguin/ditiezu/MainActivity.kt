package com.passionpenguin.ditiezu

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.github.salomonbrys.kotson.obj
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.JsonParser
import com.passionpenguin.ditiezu.helper.Dialog
import com.passionpenguin.ditiezu.helper.HttpExt
import com.passionpenguin.ditiezu.helper.Preference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        actionBar?.hide()

        val navView = nav_view

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        if (Preference(this).getBoolean("preview_mode")!!) GlobalScope.launch {
            val value = HttpExt.retrievePage("https://gitee.com/PassionPenguin/Ditiezu/raw/v2/PREVIEW_VERSION.json", customHeader = arrayOf(HttpExt.HttpHeader("Referer", "https://gitee.com/PassionPenguin/Ditiezu?from=app")))
            if (value != "Failed Retrieved") {
                val latestVersion = JsonParser().parse(value).obj
                if (latestVersion.get("debugCode").asInt > BuildConfig.VERSION_CODE)
                    MainActivity.postDelayed({
                        Dialog.create(
                            this@MainActivity,
                            MainActivity,
                            resources.getString(R.string.confirmUpdating),
                            resources.getString(R.string.new_version_detected),
                            latestVersion.get("debugLog").asString
                        ) { _, w ->
                            HttpExt.downloadUtils(
                                applicationContext,
                                "https://passionpenguin.coding.net/api/share/download/0fa9eb8c-6255-4a97-b7cb-41c64e5b1699",
                                "dtz_${latestVersion.get("debugCode").asString}.apk"
                            )
                            w.dismiss()
                        }
                    }, 0)
            }
        }
        else GlobalScope.launch {
            val value = HttpExt.retrievePage("https://gitee.com/PassionPenguin/Ditiezu/raw/v2/STABLE_VERSION.json", customHeader = arrayOf(HttpExt.HttpHeader("Referer", "https://gitee.com/PassionPenguin/Ditiezu?from=app")))
            if (value != "Failed Retrieved") {
                val latestVersion = JsonParser().parse(value).obj
                if (latestVersion.get("versionCode").asInt > BuildConfig.VERSION_CODE)
                    MainActivity.postDelayed({
                        Dialog.create(
                            this@MainActivity,
                            MainActivity,
                            resources.getString(R.string.confirmUpdating),
                            resources.getString(R.string.new_version_detected),
                            latestVersion.get("versionLog").asString
                        ) { _, w ->
                            HttpExt.downloadUtils(
                                applicationContext,
                                "https://passionpenguin.coding.net/api/share/download/0fa9eb8c-6255-4a97-b7cb-41c64e5b1699",
                                "dtz_${latestVersion.get("versionCode").asString}.apk"
                            )
                            w.dismiss()
                        }
                    }, 0)
            }
        }

        with(Preference(this@MainActivity)) {
            if (this.getBoolean("login_state")!!)
                MainActivity.postDelayed({
                    Dialog.tip(resources.getString(R.string.welcome_user, this.getString("user_name")),
                        R.drawable.ic_baseline_check_24,
                        R.color.success,
                        this@MainActivity,
                        MainActivity,
                        Dialog.TIME_SHORT)
                }, 0)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}