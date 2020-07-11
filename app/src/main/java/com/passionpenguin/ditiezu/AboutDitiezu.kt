package com.passionpenguin.ditiezu

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.salomonbrys.kotson.obj
import com.google.gson.JsonParser
import com.passionpenguin.ditiezu.helper.*
import kotlinx.android.synthetic.main.activity_about_ditiezu.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@SuppressLint("SetTextI18n")
class AboutDitiezu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_ditiezu)

        version.text = "${BuildConfig.VERSION_NAME} [${BuildConfig.BUILD_TYPE}]"
        BackButton.setOnClickListener { onBackPressed() }

        val pref = Preference(this)
        val appPref = mutableListOf<PrefListItem>()
        appPref.add(PrefListItem(resources.getString(R.string.version), BuildConfig.VERSION_NAME + " (${BuildConfig.VERSION_CODE})", "") {})
        appPref.add(PrefListItem(resources.getString(R.string.preview_on), "", "", true) {
            Dialog.create(
                this@AboutDitiezu,
                AboutDitiezu,
                resources.getString(R.string.preview_title),
                resources.getString(R.string.preview_on),
                resources.getString(R.string.preview_description),
                { v, _ ->
                    pref.edit("preview_mode", v.findViewById<CheckBox>(R.id.settingCheckbox).isChecked)
                    if (pref.getBoolean("preview_mode")!!)
                        GlobalScope.launch {
                            val value = HttpExt.retrievePage("https://gitee.com/PassionPenguin/Ditiezu/raw/v2/PREVIEW_VERSION.json", customHeader = arrayOf(HttpExt.HttpHeader("Referer", "https://gitee.com/PassionPenguin/Ditiezu?from=app")))
                            if (value != "Failed Retrieved") {
                                val latestVersion = JsonParser().parse(value).obj
                                if (latestVersion.get("debugCode").asInt > BuildConfig.VERSION_CODE)
                                    application_pref_list.addView(prefView(applicationContext, resources.getString(R.string.new_version_detected), latestVersion.get("debugLog").asString, latestVersion.get("debugCode").asString, true) {
                                        Dialog.create(this@AboutDitiezu, AboutDitiezu, resources.getString(R.string.confirm), resources.getString(R.string.confirmUpdating), resources.getString(R.string.confirmUpdating_description)) { _, w ->
                                            HttpExt.downloadUtils(applicationContext, "https://passionpenguin.coding.net/api/share/download/ebf77d48-2984-4a8b-b8da-1b53f4a8de8e", "dtz_${latestVersion.get("debugCode").asString}.apk")
                                            w.dismiss()
                                        }
                                    })
                            }
                        }
                    else recreate()
                }) { v, w ->
                v.addView(LayoutInflater.from(this@AboutDitiezu).inflate(R.layout.fragment_boolean_settting, v, false))
                v.findViewById<CheckBox>(R.id.settingCheckbox).isChecked = pref.getBoolean("preview_mode")!!
                v.findViewById<TextView>(R.id.settingName).text = resources.getString(R.string.preview_on)
                w.update()
            }
        })

        if (pref.getBoolean("preview_mode")!!) GlobalScope.launch {
            val value = HttpExt.retrievePage("https://gitee.com/PassionPenguin/Ditiezu/raw/v2/PREVIEW_VERSION.json", customHeader = arrayOf(HttpExt.HttpHeader("Referer", "https://gitee.com/PassionPenguin/Ditiezu?from=app")))
            if (value != "Failed Retrieved") {
                val latestVersion = JsonParser().parse(value).obj
                if (latestVersion.get("debugCode").asInt > BuildConfig.VERSION_CODE)
                    appPref.add(PrefListItem(resources.getString(R.string.new_version_detected), latestVersion.get("debugLog").asString, latestVersion.get("debugCode").asString, true) {
                        Dialog.create(this@AboutDitiezu, AboutDitiezu, resources.getString(R.string.confirm), resources.getString(R.string.confirmUpdating), resources.getString(R.string.confirmUpdating_description)) { _, w ->
                            HttpExt.downloadUtils(applicationContext, "https://passionpenguin.coding.net/api/share/download/ebf77d48-2984-4a8b-b8da-1b53f4a8de8e", "dtz_${latestVersion.get("debugCode").asString}.apk")
                            w.dismiss()
                        }
                    })
            }
        }
        else GlobalScope.launch {
            val value = HttpExt.retrievePage("https://gitee.com/PassionPenguin/Ditiezu/raw/v2/STABLE_VERSION.json", customHeader = arrayOf(HttpExt.HttpHeader("Referer", "https://gitee.com/PassionPenguin/Ditiezu?from=app")))
            if (value != "Failed Retrieved") {
                val latestVersion = JsonParser().parse(value).obj
                if (latestVersion.get("versionCode").asInt > BuildConfig.VERSION_CODE)
                    appPref.add(PrefListItem(resources.getString(R.string.new_version_detected), latestVersion.get("versionLog").asString, latestVersion.get("versionCode").asString, true) {
                        Dialog.create(this@AboutDitiezu, AboutDitiezu, resources.getString(R.string.confirm), resources.getString(R.string.confirmUpdating), resources.getString(R.string.confirmUpdating_description)) { _, w ->
                            HttpExt.downloadUtils(applicationContext, "https://passionpenguin.coding.net/api/share/download/0fa9eb8c-6255-4a97-b7cb-41c64e5b1699", "dtz_${latestVersion.get("versionCode").asString}.apk")
                            w.dismiss()
                        }
                    })
            }
        }

        appPref.forEach { item ->
            application_pref_list.addView(prefView(applicationContext, item.name, item.description, item.value, item.toggle, item.execFunc))
        }

    }
}