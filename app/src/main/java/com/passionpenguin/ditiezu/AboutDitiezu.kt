package com.passionpenguin.ditiezu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.salomonbrys.kotson.obj
import com.google.gson.JsonParser
import com.passionpenguin.ditiezu.helper.Dialog
import com.passionpenguin.ditiezu.helper.HttpExt
import com.passionpenguin.ditiezu.helper.PrefListItem
import com.passionpenguin.ditiezu.helper.prefView
import kotlinx.android.synthetic.main.activity_about_ditiezu.*

class AboutDitiezu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_ditiezu)

        version.text = "${BuildConfig.VERSION_NAME} [${BuildConfig.BUILD_TYPE}]"
        BackButton.setOnClickListener { onBackPressed() }

        val appPref = mutableListOf<PrefListItem>()
        appPref.add(
            PrefListItem(
                resources.getString(R.string.version),
                BuildConfig.VERSION_NAME,
                ""
            ) {})

        val value =
            HttpExt().asyncRetrieveNonForumPage("https://gitee.com/PassionPenguin/Ditiezu/raw/v2/CUR_VERSION.json")
        if (value != "Failed Retrieved") {
            val latestVersion = JsonParser().parse(value).obj
            if (latestVersion.get("versionCode").asInt > BuildConfig.VERSION_CODE)
                appPref.add(
                    PrefListItem(
                        resources.getString(R.string.new_version_detected),
                        latestVersion.get("versionLog").asString,
                        latestVersion.get("versionCode").asString,
                        true
                    ) {
                        Dialog().create(
                            this,
                            AboutDitiezu,
                            applicationContext,
                            resources.getString(R.string.confirmUpdating),
                            resources.getString(R.string.confirmUpdating_description)
                        ) {
                            HttpExt().downloadUtils(
                                applicationContext,
                                "https://passionpenguin.coding.net/api/share/download/0fa9eb8c-6255-4a97-b7cb-41c64e5b1699",
                                "dtz_${latestVersion.get("versionCode").asString}.apk"
                            )
                        }
                    })
        }

        appPref.forEach { item ->
            application_pref_list.addView(
                prefView(
                    applicationContext,
                    item.name,
                    item.description,
                    item.value,
                    item.toggle,
                    item.execFunc
                )
            )
        }

    }
}