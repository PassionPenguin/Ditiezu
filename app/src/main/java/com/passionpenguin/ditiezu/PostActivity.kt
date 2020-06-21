package com.passionpenguin.ditiezu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.passionpenguin.ditiezu.helper.*
import kotlinx.android.synthetic.main.activity_post.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URLEncoder
import java.util.regex.Matcher
import java.util.regex.Pattern

class PostActivity : AppCompatActivity() {
    private var attachHash = ""
    private var uid = ""
    private var pid: String? = null
    private var fid: String? = null
    private var attachlist: ArrayList<String> = arrayListOf()

    private fun insert(
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
            if (contentAfter.isEmpty() || contentBefore.isEmpty())
                this.setSelection(end, end)
            else this.setSelection(contentBefore.length + start, contentAfter.length + end)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        val e = intent.extras ?: return
        val type = e.get("type") ?: return
        val tid = e.get("tid")
        val reppid = e.get("reppid")
        pid = e.get("pid").toString()
        fid = e.get("fid").toString()
        lateinit var originParser: Document
        val typeNameList: ArrayList<String> = arrayListOf()
        val typeValueList: ArrayList<String> = arrayListOf()
        when (type) {
            TYPE_REPLY -> {
                if (tid == null) onBackPressed()
                originParser = if (reppid != null)
                    Jsoup.parse(HttpExt().asyncRetrievePage("http://www.ditiezu.com/forum.php?mod=post&action=reply&fid=$fid&tid=$tid&repquote=$reppid"))
                else Jsoup.parse(HttpExt().asyncRetrievePage("http://www.ditiezu.com/forum.php?mod=post&action=reply&fid=$fid&tid=$tid"))
            }
            TYPE_EDIT -> {
                if (pid == null || tid == null) onBackPressed()
                originParser =
                    Jsoup.parse(HttpExt().asyncRetrievePage("http://www.ditiezu.com/forum.php?mod=post&action=edit&tid=$tid&pid=$pid"))
                EditTextInput.setText(originParser.select("textarea")[0].html())
            }
            TYPE_SIGHTML -> {
                imageSelectorToggle.visibility = View.GONE
                imageListWrap.visibility = View.GONE
                EditTextInput.setText(Preference(this@PostActivity).getString(TYPE_SIGHTML))
            }
            TYPE_NEW -> {
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
        if (type != TYPE_SIGHTML) {
            attachHash = originParser.select("[name=\"hash\"]").attr("value")

            var s = originParser.select("body").html()
            val pattern = Pattern.compile("IMGUNUSEDAID\\[\\d*] = '(\\d*)'")
            var matcher: Matcher = pattern.matcher(s)
            while (matcher.find()) {
                matcher.group(1)?.toString()?.let { attachlist.add(it) }
                s = s.substring(matcher.start() + 1) // ignore the just-matched and move on;
                matcher = pattern.matcher(s)
            }
            var attachResult =
                HttpExt().asyncRetrievePage("http://www.ditiezu.com/forum.php?mod=ajax&action=imagelist&pid=$pid&fid=$fid&inajax=1&ajaxtarget=imgattachlist")
                    .substring(53)
            if (attachResult.length > 11) {
                attachResult = attachResult.substring(0, attachResult.length - 11)
                Jsoup.parse(attachResult).select("[id^='imageattach']").forEach {
                    val v = attachImageView(
                        this,
                        it.select("img").attr("src")
                    )
                    v.setOnClickListener { _ ->
                        insert("", "[attachimg]${it.attr("id").substring(11)}[/attachimg]")
                    }
                    ImageList.addView(v)
                }
            }

            uid = originParser.select("[name=\"uid\"]").attr("value")
        }

        val formhash =
            if (type == TYPE_NEW) originParser.select("[name='formhash']").attr("value") else
                Jsoup.parse(HttpExt().asyncRetrievePage("http://www.ditiezu.com/search.php?mod=forum"))
                    .select("[name=\"formhash\"]").attr("value")

        findViewById<EditText>(R.id.app_search_input).visibility = View.GONE
        with(findViewById<RadioButton>(R.id.actionBar_leftButton)) {
            this.buttonDrawable = resources.getDrawable(R.drawable.ic_baseline_close_24, null)
            this.setOnClickListener {
                onBackPressed()
            }
        }
        with(findViewById<RadioButton>(R.id.actionBar_rightButton)) {
            this.buttonDrawable = resources.getDrawable(R.drawable.ic_baseline_send_24, null)
            this.setOnClickListener {
                var postAID = ""
                attachlist.forEach {
                    postAID += "&attachnew[$it][description]="
                }
                val str = HttpExt().asyncPostPage(
                    when (type) {
                        TYPE_EDIT -> "http://www.ditiezu.com/forum.php?mod=post&action=edit&extra=&editsubmit=yes&inajax=1"
                        TYPE_NEW -> "http://www.ditiezu.com/forum.php?mod=post&action=newthread&fid=$fid&extra=&topicsubmit=yes&inajax=1"
                        TYPE_SIGHTML -> "http://www.ditiezu.com/home.php?mod=spacecp&ac=profile"
                        else -> "http://www.ditiezu.com/forum.php?mod=post&action=reply&tid=$tid&replysubmit=yes&inajax=1"
                    },
                    "message=" + URLEncoder.encode(
                        EditTextInput.text.toString(),
                        "GBK"
                    ) + "&formhash=$formhash$postAID&subject=" + when (type) {
                        TYPE_EDIT -> "&pid=$pid&tid=$tid" // Edit
                        TYPE_NEW -> "&typeid=" + typeValueList[typeSelector.selectedItemPosition] + "&subject=" + subject.text.toString() // New Thread
                        TYPE_SIGHTML -> "&profilesubmit=true&sightml=${URLEncoder.encode(
                            EditTextInput.text.toString(),
                            "GBK"
                        )}"
                        else -> {
                            if (reppid != null) {
                                "&reppid=$reppid&reppost=$reppid&noticeauthor=${originParser.select(
                                    "[name='noticeauthor']"
                                ).attr("value")}&noticetrimstr=${URLEncoder.encode(
                                    originParser.select(
                                        "[name='noticetrimstr']"
                                    ).attr("value"), "GBK"
                                )}&noticeauthormsg=${URLEncoder.encode(
                                    originParser.select(
                                        "[name='noticeauthormsg']"
                                    ).attr("value"), "GBK"
                                )}"
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
                val response = str.substring(
                    str.indexOf("', '", str.indexOf("handle")) + 4,
                    str.indexOf("'", str.indexOf("', '", str.indexOf("handle")) + 4)
                )
                when {
                    str == "Failed Retrieved" -> {
                        Dialog().tip(
                            resources.getString(R.string.failed_retrieved),
                            R.drawable.ic_baseline_close_24,
                            R.color.danger,
                            this@PostActivity,
                            PostActivity,
                            Dialog.TIME_SHORT
                        )
                    }
                    str.contains("succeed") || str.contains("success") -> {
                        Dialog().tip(
                            response,
                            R.drawable.ic_baseline_check_24,
                            R.color.primary500,
                            this@PostActivity,
                            PostActivity,
                            Dialog.TIME_SHORT
                        )
                        postDelayed({
                            super.onBackPressed()
                            val intent = Intent()
                            intent.putExtra("reload", true)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }, 1500)
                        if (type == TYPE_SIGHTML) Preference(this@PostActivity).edit(
                            TYPE_SIGHTML,
                            EditTextInput.text.toString()
                        )
                    }
                    str.contains("error") -> {
                        Dialog().tip(
                            response,
                            R.drawable.ic_baseline_close_24,
                            R.color.danger,
                            this@PostActivity,
                            PostActivity,
                            Dialog.TIME_SHORT
                        )
                    }
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

        fontBoldToggle.setOnClickListener {
            setCompoundButtonColor(fontBoldToggle, true)
            insert("[b]", "[/b]")
        }

        fontItalicToggle.setOnClickListener {
            setCompoundButtonColor(fontItalicToggle, true)
            insert("[i]", "[/i]")
        }

        fontUnderlinedToggle.setOnClickListener {
            setCompoundButtonColor(fontUnderlinedToggle, true)
            insert("[u]", "[/u]")
        }

        fontStrikeThroughToggle.setOnClickListener {
            setCompoundButtonColor(fontStrikeThroughToggle, true)
            insert("[s]", "[/s]")
        }

        fontQuoteToggle.setOnClickListener {
            setCompoundButtonColor(fontQuoteToggle, true)
            insert("[quote]", "[/quote]")
        }

        imageSelectorToggle.setOnClickListener {
            setCompoundButtonColor(imageSelectorToggle, true)
            PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .theme(R.style.picture_WeChat_style)
                .selectionMode(PictureConfig.MULTIPLE)
                .isPageStrategy(true, 240, true)
                .isSingleDirectReturn(true)
                .isCamera(true)
                .isZoomAnim(true)
                .maxSelectNum(20)
                .minSelectNum(1)
                .imageSpanCount(3)
                .isGif(true)
                .isReturnEmpty(true)
                .isAndroidQTransform(true)
                .isOriginalImageControl(true)
                .imageFormat("JPG")
                .forResult(PictureConfig.CHOOSE_REQUEST)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 结果回调
                    val selectList: List<LocalMedia> = PictureSelector.obtainMultipleResult(data)
                    selectList.forEach {

                        val s =
                            HttpExt().uploadFile(
                                it.path,
                                this@PostActivity,
                                uid,
                                attachHash,
                                fid.toString(),
                                it.mimeType
                            )
                        if (Pattern.compile("^[0-9]*$").matcher(s).matches()) attachlist.add(s)
                        else Dialog().tip(
                            resources.getString(R.string.failed),
                            R.drawable.ic_baseline_close_24,
                            R.color.danger,
                            this,
                            PostActivity,
                            Dialog.TIME_SHORT
                        )
                    }

                    ImageList.removeAllViews()
                    var attachResult =
                        HttpExt().asyncRetrievePage("http://www.ditiezu.com/forum.php?mod=ajax&action=imagelist&pid=$pid&fid=$fid&inajax=1&ajaxtarget=imgattachlist")
                            .substring(53)
                    if (attachResult.length > 11) {
                        attachResult = attachResult.substring(0, attachResult.length - 11)
                        Jsoup.parse(attachResult).select("[id^='imageattach']").forEach {
                            val v = attachImageView(
                                this,
                                it.select("img").attr("src")
                            )
                            v.setOnClickListener { _ ->
                                insert("", "[attachimg]${it.attr("id").substring(11)}[/attachimg]")
                            }
                            ImageList.addView(v)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val TYPE_REPLY = "reply"
        const val TYPE_EDIT = "edit"
        const val TYPE_NEW = "newthread"
        const val TYPE_SIGHTML = "sightml"
    }
}