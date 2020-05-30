package com.passionpenguin.ditiezu

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.passionpenguin.ditiezu.helper.HttpExt
import com.passionpenguin.ditiezu.helper.SearchListAdapter
import com.passionpenguin.ditiezu.helper.SearchListItem

import org.jsoup.Jsoup
import java.net.URLEncoder

class SearchResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        val i = intent.extras
        if (i?.getString("kw", null) == null) finish()
        var kw = i?.getString("kw", "霜羽")

        val formhash =
            Jsoup.parse(HttpExt().asyncRetrievePage("http://www.ditiezu.com/search.php?mod=forum"))
                .select("[name=\"formhash\"]").attr("value")

        fun processResult(result: String) {
            val parser = Jsoup.parse(result)
            val threadListContent = mutableListOf<SearchListItem>()
            parser.select("#threadlist .pbw").forEach {
                val title = it.select("h3 a")
                val author = it.select("p")[2].select("span a")[0]
                var targetTid = ""
                with(title.attr("href")) {
                    try {
                        targetTid = if (this.contains("highlight"))
                            this.substring(
                                this.indexOf("tid=") + 4,
                                this.indexOf("&", this.indexOf("tid=") + 4)
                            )
                        else
                            this.substring(this.indexOf("tid=") + 4)
                    } catch (E: Exception) {
                        Log.e("", E.toString())
                        Log.e("", this)
                    }
                }
                threadListContent.add(
                    SearchListItem(
                        author.attr("href").substring(
                            author.attr("href").indexOf("uid-") + 4,
                            author.attr("href").indexOf(".html")
                        ).toInt(),
                        title.text().trim(),
                        it.select("p:nth-child(3)").text().trim(),
                        author.text(),
                        it.select("p:nth-child(4) span:first-child").text(),
                        "[" + it.select("p:nth-child(4) span:last-child")
                            .text() + "]" + it.select("p.xg1").text(),
                        targetTid.toInt()
                    )
                )
            }

            runOnUiThread {
                val list = findViewById<ListView>(R.id.threadItemList)
                list?.adapter =
                    SearchListAdapter(
                        applicationContext,
                        0,
                        threadListContent
                    )

                findViewById<ListView>(R.id.threadItemList)
                    ?.setOnItemClickListener { _, _, position, _ ->
                        val intent = Intent(this@SearchResultActivity, ViewThread::class.java)
                        intent.putExtra("tid", threadListContent[position].target)
                        startActivity(intent)
                    }
                findViewById<LinearLayout>(R.id.LoadingMaskContainer)?.visibility = View.GONE
            }
        }

        fun search() {
            findViewById<LinearLayout>(R.id.LoadingMaskContainer)?.visibility = View.VISIBLE
            val s = HttpExt().asyncPostPage(
                "http://ditiezu.com/search.php?mod=forum",
                "formhash=$formhash&srchtxt=" + URLEncoder.encode(
                    kw.toString(),
                    "GBK"
                ) + "&searchsubmit=yes"
            )
            if (s == "Failed Retrieved") {
                // Failed Retrieved
                Log.i("HTTPEXT", "FAILED RETRIEVED")
            } else if (s.contains("用户登录"))
                Snackbar.make(
                    findViewById<ConstraintLayout>(R.id.SearchResultActivity),
                    resources.getString(R.string.login_description),
                    Snackbar.LENGTH_LONG
                ).setAction("登录") {
                    startActivity(
                        Intent(
                            this@SearchResultActivity,
                            LoginActivity::class.java
                        )
                    )
                }.show()
            else if (s.contains("只能进行一次搜索"))
                Snackbar.make(
                    findViewById<ConstraintLayout>(R.id.SearchResultActivity),
                    resources.getString(R.string.search_15s),
                    Snackbar.LENGTH_LONG
                ).show()
            else if (s.contains("站点设置每分钟系统最多"))
                Snackbar.make(
                    findViewById<ConstraintLayout>(R.id.SearchResultActivity),
                    resources.getString(R.string.search_system),
                    Snackbar.LENGTH_LONG
                ).show()
            else
                processResult(s)
        }
        search()

        val input = findViewById<EditText>(R.id.app_search_input)
        input.setText(kw)

        input.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && input.text.toString()
                        .trim().isNotEmpty()
                ) {
                    kw = input.text.toString()
                    search()
                    return true
                }
                return false
            }
        })
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