/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   LoginActivity.kt
 * =  LAST MODIFIED BY PASSIONPENGUIN [8/14/20 1:40 AM]
 * ==================================================
 * Copyright 2020 PassionPenguin. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("BlockingMethodInNonBlockingContext")

package com.ditiezu.android

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.passionpenguin.HttpHeader
import com.passionpenguin.LoadingButton
import com.passionpenguin.NetUtils
import com.passionpenguin.SPHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.net.URLEncoder

class LoginActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        val spHelper = SPHelper(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        Glide.with(this).load(R.mipmap.noavatar_middle).apply(RequestOptions.bitmapTransform(RoundedCorners(8))).into(findViewById(R.id.avatar))

        val codeWrap = findViewById<LinearLayout>(R.id.codeWrap)
        val codeInput = findViewById<EditText>(R.id.codeInput)
        val codeImage = findViewById<ImageView>(R.id.codeImage)
        val usrName = findViewById<EditText>(R.id.userNameInput)
        val usrPwd = findViewById<EditText>(R.id.userPasswordInput)
        val usrQuestionWrap = findViewById<LinearLayout>(R.id.userQuestionWrap)
        val usrQuestionSelect = findViewById<Spinner>(R.id.userQuestionSelect)
        val usrQuestionInput = findViewById<EditText>(R.id.userQuestionInput)
        val submit = findViewById<LinearLayout>(R.id.submitLogin)
        val tips = findViewById<TextView>(R.id.tips)
        val loadingButton = LoadingButton(submit, this)

        with(ArrayAdapter(applicationContext, R.layout.textview_dropdown, resources.getStringArray(R.array.question_list))) {
            this.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            usrQuestionSelect.adapter = this
        }

        fun loadCode(hash: String) {
            /**
             * [Function] loadCode(hash: String)
             * @param hash: String, the code hash (or sechash)
             * @return null
             * @purpose show security code -> ImageView
             */
            GlobalScope.launch {
                with(NetUtils(this@LoginActivity).retrievePage("http://www.ditiezu.com/misc.php?mod=seccode&action=update&idhash=$hash&inajax=1&ajaxtarget=seccode_")) {
                    val doc = Jsoup.parse(this.substring(53, this.length - 1))
                    val bitmap = NetUtils(this@LoginActivity).retrieveBitmap("http://www.ditiezu.com/" + doc.select("img").attr("src"), retrieveAsDesktopPage = false)
                    try {
                        runOnUiThread {
                            Glide.with(codeImage)
                                .load(bitmap)
                                .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
                                .into(codeImage)
                        }
                    } catch (e: Exception) {
                        println(e)
                    }
                }
            }
        }

        fun loadLoginData() {
            /**
             * [Function] loadLoginData()
             * @purpose load Login Data
             */
            try {
                runOnUiThread {
                    submit.setOnClickListener {
                        loadingButton.onLoading()
                        GlobalScope.launch {
                            val res = NetUtils(this@LoginActivity).postPage(
                                "http://www.ditiezu.com/member.php?mod=logging&action=login&loginsubmit=yes&infloat=yes&lssubmit=yes&inajax=1",
                                "fastloginfield=username&username=${URLEncoder.encode(usrName.text.toString(), "GBK")}" +
                                        "&password=${URLEncoder.encode(usrPwd.text.toString(), "GBK")}&quickforward=yes&handlekey=ls",
                                customHeader = arrayOf(
                                    HttpHeader("Host", "www.ditiezu.com"),
                                    HttpHeader("Cache-Control", "max-age=0"),
                                    HttpHeader("Origin", "http://www.ditiezu.com"),
                                    HttpHeader("Upgrade-Insecure-Requests", "1"),
                                    HttpHeader("Content-Type", "application/x-www-form-urlencoded"),
                                    HttpHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"),
                                    HttpHeader("Referer", "http://www.ditiezu.com/member.php?mod=logging&action=login&mobile=yes"),
                                    HttpHeader("Connection", "keep-alive")
                                )
                            )
                            runOnUiThread {
                                loadingButton.onLoaded()
                            }
                            if (res.contains("欢迎您回来") || res.contains("succeedhandle_ls")) {
                                spHelper.edit("login", "true")
                                println("Login" + spHelper.getString("login"))
                                spHelper.edit("user_name", res.substring(res.indexOf("'username':") + 11, res.indexOf("'", res.indexOf("'username':") + 11)))
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            } else {
                                val data = res.substring(53, res.length - 10)
                                try {
                                    if (res.contains("location")) {
                                        // 需要输入验证码
                                        val location = data.substring(data.indexOf("', '") + 4, data.indexOf("'", data.indexOf("', '") + 4))
                                        GlobalScope.launch {
                                            try {
                                                val source = NetUtils(this@LoginActivity).retrievePage("http://www.ditiezu.com/$location&infloat=yes&handlekey=login&inajax=1&ajaxtarget=fwin_content_login")
                                                if (source.contains("验证码")) {

                                                    var requireQuestion = false

                                                    if (source.contains("安全提问")) {
                                                        requireQuestion = true
                                                        usrQuestionWrap.visibility = View.VISIBLE
                                                    }

                                                    val codeParser = Jsoup.parse(source.substring(53, source.length - 10))
                                                    runOnUiThread {
                                                        codeWrap.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                                        loadCode(codeParser.select("[name='sechash']").attr("value"))
                                                    }

                                                    findViewById<LinearLayout>(R.id.submitLogin).setOnClickListener {
                                                        loadingButton.onLoading()
                                                        GlobalScope.launch {
                                                            val status = NetUtils(this@LoginActivity).retrievePage(
                                                                "http://www.ditiezu.com/misc.php?mod=seccode&action=check&inajax=1" +
                                                                        "&idhash=${codeParser.select("[name='sechash']").attr("value")}" +
                                                                        "&secverify=${URLEncoder.encode(codeInput.text.toString(), "GBK")}"
                                                            )
                                                            if (status.contains("succeed")) {
                                                                // 验证码正确
                                                                val postResult = NetUtils(this@LoginActivity).postPage(
                                                                    "http://www.ditiezu.com/${codeParser.select("form").attr("action")}&inajax=1",
                                                                    "formhash=${codeParser.select("[name='formhash']").attr("value")}&referer=http%3A%2F%2Fwww.ditiezu.com%2Fmember.php%3Fmod%3Dregditiezu.php" +
                                                                            "&auth=${codeParser.select("[name='auth']").attr("value")}" +
                                                                            "&sechash=${URLEncoder.encode(codeParser.select("[name='sechash']").attr("value"), "GBK")}" +
                                                                            "&seccodeverify=${URLEncoder.encode(codeInput.text.toString(), "GBK")}&cookietime=2592000" +
                                                                            if (requireQuestion) "&questionid=${usrQuestionSelect.selectedItemPosition}&answer=${usrQuestionInput.text}" else "",
                                                                    customHeader = arrayOf(
                                                                        HttpHeader("Host", "www.ditiezu.com"),
                                                                        HttpHeader("Cache-Control", "max-age=0"),
                                                                        HttpHeader("Origin", "http://www.ditiezu.com"),
                                                                        HttpHeader("Upgrade-Insecure-Requests", "1"),
                                                                        HttpHeader("Content-Type", "application/x-www-form-urlencoded"),
                                                                        HttpHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"),
                                                                        HttpHeader("Referer", "http://www.ditiezu.com/member.php?mod=logging&action=login&mobile=yes"),
                                                                        HttpHeader("Connection", "keep-alive")
                                                                    )
                                                                )
                                                                if (postResult.contains("抱歉，密码空或包含非法字符")) {
                                                                    runOnUiThread {
                                                                        codeWrap.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
                                                                        loadLoginData()
                                                                    }
                                                                } else runOnUiThread {
                                                                    tips.text = postResult.substring(postResult.indexOf("CDATA[") + 6, postResult.indexOf("<", postResult.indexOf("CDATA[") + 6))
                                                                }
                                                                if (postResult.contains("succeedhandle_") || postResult.contains("'欢迎您回来，")) {
                                                                    // 登录成功进行写入及跳转
                                                                    spHelper.edit("login", "true")
                                                                    spHelper.edit("user_name", postResult.substring(postResult.indexOf("'username':'") + 12, postResult.indexOf("'", postResult.indexOf("'username':'") + 12)))
                                                                    spHelper.edit("user_uid", postResult.substring(postResult.indexOf("'uid':'") + 12, postResult.indexOf("'", postResult.indexOf("'uid':'") + 12)))
                                                                    runOnUiThread {
                                                                        tips.text = resources.getString(R.string.tip_login_successfully)
                                                                    }
                                                                    val i = Intent(this@LoginActivity, MainActivity::class.java)
                                                                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                                                    startActivity(i)
                                                                }
                                                                runOnUiThread {
                                                                    loadingButton.onLoaded()
                                                                }
                                                            } else runOnUiThread {
                                                                tips.text = resources.getString(R.string.tip_incorrect_code)
                                                                loadingButton.onLoaded()
                                                            }
                                                        }
                                                    }
                                                }

                                            } catch (e: Exception) {
                                                println(e)
                                            }
                                        }
                                    } else runOnUiThread {
                                        // 出现错误，如密码空、密码错误等
                                        tips.text = data.substring(0, data.indexOf("<"))
                                    }
                                } catch (e: Exception) {
                                    println(e)
                                }

                                runOnUiThread {
                                    when {
                                        res.contains("location_login") -> tips.text = resources.getString(R.string.tip_enter_code)
                                        res.contains("Error") -> tips.text = res.substring(res.indexOf("CDATA[") + 6, res.indexOf("<", res.indexOf("CDATA[") + 6))
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println(e)
            }
        }
        loadLoginData()
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