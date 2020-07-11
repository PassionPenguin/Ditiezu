@file:Suppress("BlockingMethodInNonBlockingContext", "UNCHECKED_CAST")

package com.passionpenguin.ditiezu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.passionpenguin.ditiezu.helper.*
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URLEncoder
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class Editor : AppCompatActivity() {
    private var attachHash = ""
    private var uid = ""
    private var pid: String? = null
    private var fid: String? = null
    private var attachlist: ArrayList<String> = arrayListOf()
    private var formhash: String = ""

    private fun insert(contentBefore: String = "", contentAfter: String = "", replaceWith: String? = null) {
        with(EditTextInput) {
            val start = this.selectionStart
            val end = this.selectionEnd
            val t = this.text.substring(0, start) + contentBefore + (replaceWith ?: this.text.substring(start, end)) + contentAfter + this.text.substring(end, this.length())
            this.setText(t)
            if (contentAfter.isEmpty() || contentBefore.isEmpty()) this.setSelection(end, end)
            else this.setSelection(contentBefore.length + start, contentAfter.length + end)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        HeightHelper(this).init().setHeightListener(object : HeightHelper.HeightListener {
            override fun onHeightChanged(height: Int) {
                toolbar.translationY = (-height).toFloat()
            }
        })

        val e = intent.extras ?: return
        val type = e.get("type") ?: return
        val tid = e.get("tid")
        val reppid = e.get("reppid")
        pid = e.get("pid").toString()
        fid = e.get("fid").toString()

        var perm = 0
        var rewards = 0
        var rewardsTimes = 0
        var rewardsMaxTimes = 1
        var rewardsOdds = 1f
        var withPerm = false
        var withRewards = false

        lateinit var originParser: Document
        val typeNameList: ArrayList<String> = arrayListOf()
        val typeValueList: ArrayList<String> = arrayListOf()

        openGallery.setOnClickListener {
            PictureSelector.create(this@Editor)
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

        permWrap.visibility = View.GONE
        rewardsWrap.visibility = View.GONE
        GlobalScope.launch {
            when (type) {
                TYPE_REPLY -> {
                    if (tid == null) onBackPressed()
                    originParser = if (reppid != null) Jsoup.parse(HttpExt.retrievePage("http://www.ditiezu.com/forum.php?mod=post&action=reply&fid=$fid&tid=$tid&repquote=$reppid"))
                    else Jsoup.parse(HttpExt.retrievePage("http://www.ditiezu.com/forum.php?mod=post&action=reply&fid=$fid&tid=$tid"))
                    runOnUiThread {
                        editorTitle.text = resources.getString(R.string.replies)
                    }
                }
                TYPE_EDIT -> {
                    if (pid == null || tid == null) onBackPressed()
                    originParser = Jsoup.parse(HttpExt.retrievePage("http://www.ditiezu.com/forum.php?mod=post&action=edit&tid=$tid&pid=$pid"))
                    runOnUiThread {
                        EditTextInput.setText(originParser.select("textarea")[0].html())
                        editorTitle.text = resources.getString(R.string.edit)
                    }
                }
                TYPE_SIGHTML -> {
                    runOnUiThread {
                        imageSelectorToggle.visibility = View.GONE
                        EditTextInput.setText(Preference(this@Editor).getString(TYPE_SIGHTML))
                        editorTitle.text = resources.getString(R.string.signature)
                        signature.visibility = View.GONE
                    }
                }
                TYPE_NEW -> {
                    if (fid == null) onBackPressed()
                    originParser = Jsoup.parse(HttpExt.retrievePage("http://www.ditiezu.com/forum.php?mod=post&action=newthread&fid=$fid"))
                    originParser.select("#typeid option").forEach {
                        typeNameList.add(it.text())
                        typeValueList.add(it.attr("value"))
                    }
                    runOnUiThread {
                        NewThreadComponent.visibility = View.VISIBLE
                        val adapter = ArrayAdapter(
                            applicationContext,
                            R.layout.textview_dropdown,
                            typeNameList
                        )
                        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                        typeSelector.adapter = adapter
                        editorTitle.text = resources.getString(R.string.new_thread)
                    }
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

                var attachResult = HttpExt.retrievePage("http://www.ditiezu.com/forum.php?mod=ajax&action=imagelist&pid=$pid&fid=$fid&inajax=1&ajaxtarget=imgattachlist").substring(53)
                if (attachResult.length > 11) {
                    attachResult = attachResult.substring(0, attachResult.length - 11)
                    runOnUiThread {
                        Jsoup.parse(attachResult).select("[id^='imageattach']").forEach {
                            val v = attachImageView(
                                this@Editor,
                                it.select("img").attr("src")
                            )
                            v.setOnClickListener { _ ->
                                insert("", "[attachimg]${it.attr("id").substring(11)}[/attachimg]")
                            }
                            photo.addView(v)
                        }
                    }
                }

                uid = originParser.select("[name=\"uid\"]").attr("value")
                if (originParser.select("#extra_readperm_b").isNotEmpty()) withPerm = true
                if (originParser.select("#extra_replycredit_b").isNotEmpty()) withRewards = true

                runOnUiThread {
                    if (withPerm) permWrap.visibility = View.VISIBLE
                    if (withRewards) rewardsWrap.visibility = View.VISIBLE
                }
            }

            formhash = if (type == TYPE_NEW) originParser.select("[name='formhash']").attr("value") else Jsoup.parse(HttpExt.retrievePage("http://www.ditiezu.com/search.php?mod=forum")).select("[name=\"formhash\"]").attr("value")
        }

        permWrap.setOnClickListener {
            Dialog.create(
                this@Editor,
                Editor,
                resources.getString(R.string.permission),
                resources.getString(R.string.permission_title),
                resources.getString(R.string.permission_description),
                { v, _ ->
                    try {
                        perm = arrayOf(0, 1, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 150, 200, 255)[v.findViewById<Spinner>(R.id.permInput).selectedItemPosition]
                    } catch (e: NumberFormatException) {
                        Dialog.tip(resources.getString(R.string.not_a_number), R.drawable.ic_baseline_close_24, R.color.danger, this@Editor, Editor, Dialog.TIME_SHORT)
                    }
                }) { v, w ->
                v.addView(LayoutInflater.from(this@Editor).inflate(R.layout.fragment_perm, v, false))
                with(v.findViewById<Spinner>(R.id.permInput)) {
                    val adapter = ArrayAdapter<String>(this@Editor, R.layout.spinner_item,
                        listOf("不限权限", "地铁游客/等待验证用户", "地铁族 Ⅰ", "地铁族 Ⅱ", "地铁族 Ⅲ", "地铁族 Ⅳ", "地铁族 Ⅴ", "地铁族 Ⅵ", "地铁族 Ⅶ", "地铁族 Ⅷ", "地铁族 Ⅸ", "地铁族 Ⅹ", "版主", "超级版主", "管理员"))
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    this.adapter = adapter
                    this.setSelection(10)
                }
                w.update()
            }
        }

        rewardsWrap.setOnClickListener {
            Dialog.create(
                this@Editor,
                Editor,
                resources.getString(R.string.rewards),
                resources.getString(R.string.rewards_title),
                resources.getString(R.string.rewards_description),
                { v, _ ->
                    try {
                        rewards = v.findViewById<EditText>(R.id.everytime_reward_input).text.toString().toInt()
                        rewardsTimes = v.findViewById<EditText>(R.id.times_input).text.toString().toInt()
                        rewardsMaxTimes = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)[v.findViewById<Spinner>(R.id.max_times_input).selectedItemPosition]
                        rewardsOdds = arrayOf(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1f)[v.findViewById<Spinner>(R.id.odds_input).selectedItemPosition]
                    } catch (e: NumberFormatException) {
                        Dialog.tip(resources.getString(R.string.not_a_number), R.drawable.ic_baseline_close_24, R.color.danger, this@Editor, Editor, Dialog.TIME_SHORT)
                    }
                }) { v, w ->
                v.addView(LayoutInflater.from(this@Editor).inflate(R.layout.fragment_rewards, v, false))
                with(v.findViewById<Spinner>(R.id.max_times_input)) {
                    val adapter = ArrayAdapter<Int>(this@Editor, R.layout.spinner_item, listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    this.adapter = adapter
                    this.setSelection(9)
                }
                with(v.findViewById<Spinner>(R.id.odds_input)) {
                    val adapter = ArrayAdapter<String>(this@Editor, R.layout.spinner_item, listOf("10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%"))
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                    this.adapter = adapter
                    this.setSelection(9)
                }
                w.update()
            }
        }

        BackButton.setOnClickListener {
            onBackPressed()
        }
        SubmitButton.setOnClickListener {
            var postAID = ""
            attachlist.forEach {
                postAID += "&attachnew[$it][description]="
            }

            GlobalScope.launch {
                val str = HttpExt.postPage(
                    when (type) {
                        TYPE_EDIT -> "http://www.ditiezu.com/forum.php?mod=post&action=edit&extra=&editsubmit=yes&inajax=1"
                        TYPE_NEW -> "http://www.ditiezu.com/forum.php?mod=post&action=newthread&fid=$fid&extra=&topicsubmit=yes&inajax=1"
                        TYPE_SIGHTML -> "http://www.ditiezu.com/home.php?mod=spacecp&ac=profile"
                        else -> "http://www.ditiezu.com/forum.php?mod=post&action=reply&tid=$tid&replysubmit=yes&inajax=1"
                    },
                    "message=${URLEncoder.encode(EditTextInput.text.toString(), "GBK")}&formhash=$formhash$postAID" + when (type) {
                        TYPE_EDIT -> "&pid=$pid&tid=$tid"
                        TYPE_NEW -> "&typeid=" + typeValueList[typeSelector.selectedItemPosition] + "&subject=" + subject.text.toString()
                        TYPE_SIGHTML -> "&profilesubmit=true&sightml=${URLEncoder.encode(EditTextInput.text.toString(), "GBK")}"
                        else -> {
                            if (reppid != null) {
                                "&reppid=$reppid&reppost=$reppid&noticeauthor=${originParser.select("[name='noticeauthor']").attr("value")}" +
                                        "&noticetrimstr=${URLEncoder.encode(originParser.select("[name='noticetrimstr']").attr("value"), "GBK")}" +
                                        "&noticeauthormsg=${URLEncoder.encode(originParser.select("[name='noticeauthormsg']").attr("value"), "GBK")}"
                            } else {
                                ""
                            }
                        }
                    } + if (withPerm) "&readperm=$perm" else "" + if (withRewards) "&replycredit_extcredits=$rewards&replycredit_times=$rewardsTimes&replycredit_membertimes=$rewardsMaxTimes&replycredit_random=$rewardsOdds" else ""
                            + if (type != TYPE_SIGHTML && signatureInput.isChecked) "&usesig=1" else ""
                )

                val view = currentFocus
                if (view != null) {
                    val inputMethodManager = this@Editor.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                }
                val response = str.substring(
                    str.indexOf("', '", str.indexOf("handle")) + 4,
                    str.indexOf("'", str.indexOf("', '", str.indexOf("handle")) + 4)
                )
                when {
                    str == "Failed Retrieved" -> {
                        Dialog.tip(
                            resources.getString(R.string.failed_retrieved),
                            R.drawable.ic_baseline_close_24,
                            R.color.danger,
                            this@Editor,
                            Editor,
                            Dialog.TIME_SHORT
                        )
                    }
                    str.contains("succeed") || str.contains("success") -> {
                        Dialog.tip(
                            response,
                            R.drawable.ic_baseline_check_24,
                            R.color.success,
                            this@Editor,
                            Editor,
                            Dialog.TIME_SHORT
                        )
                        Timer().schedule(object : TimerTask() {
                            override fun run() {
                                onBackPressed()
                                val intent = Intent()
                                intent.putExtra("reload", true)
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }
                        }, 1500)
                        if (type == TYPE_SIGHTML) Preference(this@Editor).edit(
                            TYPE_SIGHTML,
                            EditTextInput.text.toString()
                        )
                    }
                    str.contains("error") -> {
                        Dialog.tip(
                            response,
                            R.drawable.ic_baseline_close_24,
                            R.color.danger,
                            this@Editor,
                            Editor,
                            Dialog.TIME_SHORT
                        )
                    }
                }
            }
        }

        fun setCompoundButtonColor(e: CompoundButton, isChecked: Boolean) {
            val color = R.color.grey
            val colorChecked = R.color.primary_default
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
            setCompoundButtonColor(imageSelectorToggle, false)
            setCompoundButtonColor(emotionSelectorToggle, true)
            if ((it as CheckBox).isChecked) {
                photoWrap.visibility = View.VISIBLE
                emotionWrap.visibility = View.GONE
            } else {
                photoWrap.visibility = View.GONE
                emotionWrap.visibility = View.GONE
            }
            EditTextInput.clearFocus()
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(EditTextInput.windowToken, 0)
        }

        emotionSelectorToggle.setOnClickListener {
            setCompoundButtonColor(emotionSelectorToggle, false)
            setCompoundButtonColor(imageSelectorToggle, true)
            if ((it as CheckBox).isChecked) {
                photoWrap.visibility = View.GONE
                emotionWrap.visibility = View.VISIBLE
            } else {
                photoWrap.visibility = View.GONE
                emotionWrap.visibility = View.GONE
            }
            EditTextInput.clearFocus()
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(EditTextInput.windowToken, 0)
        }

        emotionList.layoutManager = GridLayoutManager(
            this@Editor,
            @Suppress("DEPRECATION")
            with((windowManager.defaultDisplay.width / resources.getDimension(R.dimen._32)).toInt() - 2) {
                if (this > 0) this else 1
            }
        )
        val emlist = emotionData[0]
        emotionList.adapter = EmotionItemAdapter(this@Editor, emlist[1] as List<EmotionItem>, emlist[0] as String) {
            insert("", "", it)
        }
        emotionData.forEach {
            val text = TextView(this@Editor)
            text.text = (it[2] as String)
            text.setTextColor(resources.getColor(R.color.black, null))
            text.setOnClickListener { _ ->
                emotionList.adapter = EmotionItemAdapter(this@Editor, it[1] as List<EmotionItem>, it[0] as String) { value ->
                    insert("", "", value)
                }
                emotionSelectorList.children.toList().forEach { v ->
                    v.background = null
                }
                text.background = resources.getDrawable(R.drawable.border_bottom, null)
            }
            text.setPadding(
                resources.getDimension(R.dimen._8).toInt(),
                resources.getDimension(R.dimen._16).toInt(),
                resources.getDimension(R.dimen._8).toInt(),
                resources.getDimension(R.dimen._16).toInt()
            )
            emotionSelectorList.addView(text)
        }
        (emotionSelectorList.children.toList()[0] as TextView).background =
            resources.getDrawable(R.drawable.border_bottom, null)
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

        EditTextInput.setOnFocusChangeListener { _, b ->
            if (b) {
                photoWrap.visibility = View.GONE
                emotionWrap.visibility = View.GONE
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 结果回调
                    GlobalScope.launch {
                        val selectList: List<LocalMedia> = PictureSelector.obtainMultipleResult(data)
                        selectList.forEach {
                            val s = HttpExt.uploadFile(
                                it.path,
                                this@Editor,
                                uid,
                                attachHash,
                                fid.toString(),
                                it.mimeType
                            )
                            if (Pattern.compile("^[0-9]*$").matcher(s).matches()) attachlist.add(s)
                            else Dialog.tip(
                                resources.getString(R.string.failed),
                                R.drawable.ic_baseline_close_24,
                                R.color.danger,
                                this@Editor,
                                Editor,
                                Dialog.TIME_SHORT
                            )
                        }
                        GlobalScope.launch {
                            var attachResult = HttpExt.retrievePage("http://www.ditiezu.com/forum.php?mod=ajax&action=imagelist&pid=$pid&fid=$fid&inajax=1&ajaxtarget=imgattachlist").substring(53)
                            if (attachResult.length > 11) {
                                runOnUiThread {
                                    photo.removeAllViews()
                                }
                                attachResult = attachResult.substring(0, attachResult.length - 11)
                                Jsoup.parse(attachResult).select("[id^='imageattach']").forEach {
                                    val v = attachImageView(
                                        this@Editor,
                                        it.select("img").attr("src")
                                    )
                                    v.setOnClickListener { _ ->
                                        insert("", "[attachimg]${it.attr("id").substring(11)}[/attachimg]")
                                    }
                                    runOnUiThread {
                                        photo.addView(v)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val r = Rect()
                toolbar.getGlobalVisibleRect(r)
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt()) && !r.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    companion object {
        const val TYPE_REPLY = "reply"
        const val TYPE_EDIT = "edit"
        const val TYPE_NEW = "newthread"
        const val TYPE_SIGHTML = "sightml"
    }
}