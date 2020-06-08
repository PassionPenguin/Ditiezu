package com.passionpenguin.ditiezu

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.children
import com.passionpenguin.ditiezu.helper.HttpExt
import com.passionpenguin.ditiezu.helper.tintDrawable
import kotlinx.android.synthetic.main.activity_post.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URLEncoder

class PostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val e = intent.extras
        val tid = e?.get("tid")
        val reppid = e?.get("reppid")
        val type = e?.get("type") ?: "reply"
        val pid = e?.get("pid")
        val fid = e?.get("fid")
        lateinit var originParser: Document
        val typeNameList: ArrayList<String> = arrayListOf()
        val typeValueList: ArrayList<String> = arrayListOf()
        when (type) {
            "reply" -> if (tid == null) onBackPressed()
            "edit" -> {
                if (pid == null || tid == null) onBackPressed()
                EditTextInput.setText(HttpExt().asyncRetrievePage("http://www.ditiezu.com/forum.php?mod=post&action=edit&tid=$tid&pid=$pid"))
            }
            "newthread" -> {
                if (fid == null) onBackPressed()
                NewThreadComponent.visibility = View.VISIBLE
                originParser =
                    Jsoup.parse(HttpExt().asyncRetrievePage("http://www.ditiezu.com/forum.php?mod=post&action=newthread&fid=$fid"))
                originParser.select("#typeid option").forEach {
                    typeNameList.add(it.text())
                    typeValueList.add(it.attr("value"))
                }
                val adapter = ArrayAdapter(
                    applicationContext,
                    R.layout.textview_dropdown,
                    typeNameList
                )
                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                typeSelector.adapter = adapter
            }
        }

        val formhash =
            if (type == "newthread") originParser.select("[name='formhash']").attr("value") else
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
                        "newthread" -> "http://www.ditiezu.com/forum.php?mod=post&action=newthread&fid=$fid&extra=&topicsubmit=yes&inajax=1"
                        else -> "http://www.ditiezu.com/forum.php?mod=post&action=reply&tid=$tid&replysubmit=yes&inajax=1"
                    },
                    "message=" + URLEncoder.encode(
                        EditTextInput.text.toString(),
                        "GBK"
                    ) + "&formhash=$formhash&subject=" + when (type) {
                        "edit" -> {
                            "&pid=$pid&tid=$tid"
                        } // Edit
                        "newthread" -> {
                            "&typeid=" + typeValueList[typeSelector.selectedItemPosition] + "&subject=" + subject.text.toString()
                        } // New Thread
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
                    )
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
                btn.setOnClickListener {
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
                val start = this.selectionStart
                val end = this.selectionEnd
                val t =
                    this.text.substring(0, start) + contentBefore + (replaceWith
                        ?: this.text.substring(
                            start,
                            end
                        )) + contentAfter + this.text.substring(
                        end, this.length()
                    )
                this.setText(t)
                this.setSelection(start + contentBefore.length, end + contentAfter.length - 1)
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