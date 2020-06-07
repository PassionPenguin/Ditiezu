package com.passionpenguin.ditiezu

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.children
import com.passionpenguin.ditiezu.helper.HttpExt
import com.passionpenguin.ditiezu.helper.tintDrawable
import kotlinx.android.synthetic.main.activity_reply.*
import org.jsoup.Jsoup
import java.net.URLEncoder

class ReplyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val e = intent.extras
        val tid = e?.get("tid")
        val reppid = e?.get("reppid")
        if (tid == null) onBackPressed()
        val type = e?.get("type") ?: "reply"
        val pid = e?.get("pid")
        when (type) {
            "reply" -> if (tid == null) onBackPressed()
            "edit" -> if (pid == null || tid == null) onBackPressed()
        }

        val formhash =
            Jsoup.parse(HttpExt().asyncRetrievePage("http://www.ditiezu.com/search.php?mod=forum"))
                .select("[name=\"formhash\"]").attr("value")

        findViewById<EditText>(R.id.app_search_input).visibility = View.GONE
        findViewById<TextView>(R.id.title).text = resources.getString(R.string.edit)
        with(findViewById<RadioButton>(R.id.actionBar_leftButton)) {
            this.buttonDrawable = resources.getDrawable(R.drawable.ic_baseline_close_24, null)
            this.setOnClickListener {
                onBackPressed()
            }
        }
        with(findViewById<RadioButton>(R.id.actionBar_rightButton)) {
            this.buttonDrawable = resources.getDrawable(R.drawable.ic_baseline_send_24, null)
            this.setOnClickListener {
                val str = HttpExt().asyncPostPage(
                    when (type) {
                        "edit" -> "http://www.ditiezu.com/forum.php?mod=post&action=edit&extra=&editsubmit=yes&inajax=1"
                        else -> "http://www.ditiezu.com/forum.php?mod=post&action=reply&tid=$tid&replysubmit=yes&inajax=1"
                    },
                    "message=" + URLEncoder.encode(
                        EditTextInput.text.toString(),
                        "GBK"
                    ) + "&formhash=$formhash&subject=" + when (type) {
                        "edit" -> {
                            "&pid=$pid&tid=$tid"
                        } // Edit
                        else -> {
                            if (reppid != null) {
                                "&reppid=$reppid&reppost=$reppid"
                            } else {
                                ""
                            }
                        } // Reply (DEFAULT)
                    }, false
                )

                val view = currentFocus
                if (view != null) {
                    val inputMethodManager =
                        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(
                        view.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    );
                }
                tips.removeAllViews()
                val tipView = when {
                    str == "Failed Retrieved" -> {
                        val v = LayoutInflater.from(applicationContext).inflate(
                            R.layout.tip_access_denied,
                            tips,
                            false
                        )
                        v.findViewById<TextView>(R.id.text).text =
                            resources.getString(R.string.failed_retrieved)
                        v
                    }
                    str.contains("succeed") -> {
                        val v = LayoutInflater.from(applicationContext)
                            .inflate(R.layout.tip_succeed, tips, false)
                        v.findViewById<TextView>(R.id.text).text = str.substring(
                            str.indexOf(", '") + 3,
                            str.indexOf("'", str.indexOf(", '") + 3)
                        )
                        postDelayed({
                            super.onBackPressed()
                            val intent = Intent()
                            intent.putExtra("reload", true)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }, 1500)
                        v
                    }
                    str.contains("error") -> {
                        val v =
                            LayoutInflater.from(applicationContext)
                                .inflate(R.layout.tip_not_applicable, tips, false)
                        v.findViewById<TextView>(R.id.text).text = str.substring(
                            str.indexOf(", '") + 3,
                            str.indexOf("'", str.indexOf(", '") + 3)
                        )
                        v
                    }
                    else -> {
                        val v =
                            LayoutInflater.from(applicationContext)
                                .inflate(R.layout.tip_not_applicable, tips, false)
                        v.findViewById<TextView>(R.id.text).text =
                            resources.getString(R.string.title_reply)
                        v
                    }
                }
                if (tipView != null) {
                    tips?.addView(tipView)
                    postDelayed({
                        tips?.removeAllViews()
                    }, 1500)
                }
            }
        }

        fun setCompoundButtonColor(e: CompoundButton, isChecked: Boolean) {
            val color = R.color.grey
            val colorChecked = R.color.primary500
            if (e.buttonDrawable != null)
                e.buttonDrawable = tintDrawable(
                    e.buttonDrawable, getColorStateList(
                        if (!e.isChecked || isChecked) {
                            color
                        } else {
                            colorChecked
                        }
                    )
                )
            e.setTextColor(
                resources.getColor(
                    if (!e.isChecked || isChecked) {
                        color
                    } else {
                        colorChecked
                    }, null
                )
            )
        }

        arrayOf(
            fontBoldToggle,
            fontItalicToggle,
            fontStrikeThroughToggle,
            fontUnderlinedToggle,
            fontQuoteToggle,
            fontSizeToggle,
            listToggle
        ).forEach {
            setCompoundButtonColor(it, true)
            it.setOnClickListener { _ ->
                setCompoundButtonColor(it, false)
            }
        }

        arrayOf(
            arrayOf(fontSizeToggle, fontSizeSelector),
            arrayOf(listToggle, listSelector)
        ).forEach { element ->
            val el =
                (element[1] as net.cachapa.expandablelayout.ExpandableLayout).children.toList()[0] as LinearLayout
            el.children.forEachIndexed { index, it ->
                val btn = it as RadioButton
                setCompoundButtonColor(btn, true)
                btn.setOnClickListener { _ ->
                    (el.children).forEach { e ->
                        setCompoundButtonColor(e as RadioButton, true)
                    }
                    setCompoundButtonColor(btn, false)
                }
                if (index == 0) {
                    setCompoundButtonColor(btn, false)
                }
            }

            element[0].setOnClickListener {
                (el.parent as net.cachapa.expandablelayout.ExpandableLayout).toggle()
            }
        }

        fun insert(
            contentBefore: String = "",
            contentAfter: String = "",
            replaceWith: String? = null
        ) {
            with(EditTextInput) {
                val s = this.selectionStart
                val e = this.selectionEnd
                val t =
                    this.text.substring(0, s) + contentBefore + (replaceWith ?: this.text.substring(
                        s,
                        e
                    )) + contentAfter + this.text.substring(
                        e,
                        this.length()
                    )
                this.setText(t)
                this.setSelection(s + contentBefore.length, e + contentAfter.length - 1)
            }
        }

        fontBoldToggle.setOnClickListener {
            insert("[b]", "[/b]")
        }

        fontItalicToggle.setOnClickListener {
            insert("[i]", "[/i]")
        }

        fontUnderlinedToggle.setOnClickListener {
            insert("[u]", "[/u]")
        }

        fontStrikeThroughToggle.setOnClickListener {
            insert("[s]", "[/s]")
        }

        fontQuoteToggle.setOnClickListener {
            insert("[quote]", "[/quote]")
        }

        (fontSizeSelector.children.toList()[0] as LinearLayout).children.forEachIndexed { index, el ->
            el.setOnClickListener {
                insert("[size=$index]", "[/size]")
            }
        }

        bulletedList.setOnClickListener {
            insert("[list]\n[*]", "\n[/list]")
        }

        numberedList.setOnClickListener {
            insert("[list=1]\n[*]", "\n[/list]")
        }
    }
}