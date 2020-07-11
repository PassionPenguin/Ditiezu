@file:Suppress("BlockingMethodInNonBlockingContext")

package com.passionpenguin.ditiezu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.passionpenguin.ditiezu.helper.HttpExt
import com.passionpenguin.ditiezu.helper.LoadingButton
import com.passionpenguin.ditiezu.helper.Preference
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.net.URLEncoder

class LoginActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        CrashReport.initCrashReport(applicationContext, "8555ad868a", false)
        AppCenter.start(
            application, "84a9bc99-3f3b-4a6c-a8f5-2007ce79a16c",
            Analytics::class.java, Crashes::class.java
        )

        val pref = Preference(this)
        val isRedirected = intent.extras?.getBoolean("redirected") ?: false // 如果是由个人页面跳转过来的则不进行跳转
        if ((pref.getBoolean("login_state")!! || pref.getBoolean("skip_login")!!) && !isRedirected)
            startActivity(Intent(this@LoginActivity, Splash::class.java))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loadingButton = LoadingButton(findViewById(R.id.submitLogin), this)

        skip_login.setOnClickListener {
            pref.edit("skip_login", true) // 跳过登录，存入设置
            startActivity(Intent(this@LoginActivity, Splash::class.java))
        }

        Glide.with(this).load(R.mipmap.noavatar_middle).apply(RequestOptions.bitmapTransform(RoundedCorners(8))).into(avatar)

        with(ArrayAdapter(applicationContext, R.layout.textview_dropdown, resources.getStringArray(R.array.question_list))) {
            this.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            userQuestionSelect.adapter = this
        }

        fun loadCode(hash: String) {
            /**
             * [Function] loadCode(hash: String)
             * @param hash: String, the code hash (or sechash)
             * @return null
             * @purpose show security code -> ImageView
             */
            GlobalScope.launch {
                with(HttpExt.retrievePage("http://www.ditiezu.com/misc.php?mod=seccode&action=update&idhash=$hash&inajax=1&ajaxtarget=seccode_")) {
                    val doc =
                        Jsoup.parse(this.substring(53, this.length - 1))
                    val bitmap = HttpExt.retrieveBitmap(
                        "http://www.ditiezu.com/" + doc.select("img").attr("src"),
                        retrieveAsDesktopPage = false
                    )
                    try {
                        runOnUiThread {
                            Glide.with(codeImage)
                                .load(
                                    bitmap
                                )
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
                    submitLogin.setOnClickListener {
                        loadingButton.onLoading()
                        GlobalScope.launch {
                            val res = HttpExt.postPage(
                                "http://www.ditiezu.com/member.php?mod=logging&action=login&loginsubmit=yes&infloat=yes&lssubmit=yes&inajax=1",
                                "fastloginfield=username&username=${URLEncoder.encode(userNameInput.text.toString(), "GBK")}" +
                                        "&password=${URLEncoder.encode(userPasswordInput.text.toString(), "GBK")}&quickforward=yes&handlekey=ls",
                                customHeader = arrayOf(
                                    HttpExt.HttpHeader("Host", "www.ditiezu.com"),
                                    HttpExt.HttpHeader("Cache-Control", "max-age=0"),
                                    HttpExt.HttpHeader("Origin", "http://www.ditiezu.com"),
                                    HttpExt.HttpHeader("Upgrade-Insecure-Requests", "1"),
                                    HttpExt.HttpHeader("Content-Type", "application/x-www-form-urlencoded"),
                                    HttpExt.HttpHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"),
                                    HttpExt.HttpHeader("Referer", "http://www.ditiezu.com/member.php?mod=logging&action=login&mobile=yes"),
                                    HttpExt.HttpHeader("Connection", "keep-alive")
                                )
                            )
                            runOnUiThread {
                                loadingButton.onLoaded()
                            }
                            if (res.contains("欢迎您回来") || res.contains("succeedhandle_ls")) {
                                pref.edit("login_state", true)
                                pref.edit("user_name", res.substring(res.indexOf("'username':") + 11, res.indexOf("'", res.indexOf("'username':") + 11)))
                                startActivity(Intent(this@LoginActivity, Splash::class.java))
                            } else {
                                val data = res.substring(53, res.length - 10)
                                try {
                                    if (res.contains("location")) {
                                        // 需要输入验证码
                                        val location = data.substring(data.indexOf("', '") + 4, data.indexOf("'", data.indexOf("', '") + 4))
                                        GlobalScope.launch {
                                            try {
                                                val source = HttpExt.retrievePage("http://www.ditiezu.com/$location&infloat=yes&handlekey=login&inajax=1&ajaxtarget=fwin_content_login")
                                                if (source.contains("验证码")) {
                                                    val codeParser = Jsoup.parse(source.substring(53, source.length - 10))
                                                    runOnUiThread {
                                                        codeWrap.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                                        loadCode(codeParser.select("[name='sechash']").attr("value"))
                                                    }

                                                    submitLogin.setOnClickListener {
                                                        loadingButton.onLoading()
                                                        GlobalScope.launch {
                                                            val status = HttpExt.retrievePage("http://www.ditiezu.com/misc.php?mod=seccode&action=check&inajax=1" +
                                                                    "&idhash=${codeParser.select("[name='sechash']").attr("value")}" +
                                                                    "&secverify=${URLEncoder.encode(codeInput.text.toString(), "GBK")}"
                                                            )
                                                            if (status.contains("succeed")) {
                                                                // 验证码正确
                                                                val postResult = HttpExt.postPage(
                                                                    "http://www.ditiezu.com/${codeParser.select("form").attr("action")}&inajax=1",
                                                                    "formhash=${codeParser.select("[name='formhash']").attr("value")}&referer=http%3A%2F%2Fwww.ditiezu.com%2Fmember.php%3Fmod%3Dregditiezu.php" +
                                                                            "&auth=${codeParser.select("[name='auth']").attr("value")}" +
                                                                            "&sechash=${URLEncoder.encode(codeParser.select("[name='sechash']").attr("value"), "GBK")}" +
                                                                            "&seccodeverify=${URLEncoder.encode(codeInput.text.toString(), "GBK")}&cookietime=2592000",
                                                                    customHeader = arrayOf(
                                                                        HttpExt.HttpHeader("Host", "www.ditiezu.com"),
                                                                        HttpExt.HttpHeader("Cache-Control", "max-age=0"),
                                                                        HttpExt.HttpHeader("Origin", "http://www.ditiezu.com"),
                                                                        HttpExt.HttpHeader("Upgrade-Insecure-Requests", "1"),
                                                                        HttpExt.HttpHeader("Content-Type", "application/x-www-form-urlencoded"),
                                                                        HttpExt.HttpHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"),
                                                                        HttpExt.HttpHeader("Referer", "http://www.ditiezu.com/member.php?mod=logging&action=login&mobile=yes"),
                                                                        HttpExt.HttpHeader("Connection", "keep-alive")
                                                                    )
                                                                )
                                                                println(postResult)
                                                                if (postResult.contains("抱歉，密码空或包含非法字符")) {
                                                                    runOnUiThread {
                                                                        codeWrap.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
                                                                        loadLoginData()
                                                                    }
                                                                }
                                                                runOnUiThread {
                                                                    tips.text = postResult.substring(postResult.indexOf("CDATA[") + 6, postResult.indexOf("<", postResult.indexOf("CDATA[") + 6))
                                                                }
                                                                if (postResult.contains("succeedhandle_") || postResult.contains("'欢迎您回来，")) {
                                                                    // 登录成功进行写入及跳转
                                                                    pref.edit("login_state", true)
                                                                    pref.edit("user_name", postResult.substring(postResult.indexOf("'username':'") + 12, postResult.indexOf("'", postResult.indexOf("'username':'") + 12)))
                                                                    pref.edit("user_uid", postResult.substring(postResult.indexOf("'uid':'") + 12, postResult.indexOf("'", postResult.indexOf("'uid':'") + 12)))
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