/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   Editor.kt
 * =  LAST MODIFIED BY PASSIONPENGUIN [1/5/21, 9:25 PM]
 * ==================================================
 * Copyright 2021 PassionPenguin. All rights reserved.
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

@file:Suppress("BlockingMethodInNonBlockingContext", "UNCHECKED_CAST")

package com.ditiezu.android

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ditiezu.android.adapters.EmotionItem
import com.ditiezu.android.adapters.EmotionItemAdapter
import com.ditiezu.android.data.emotionData
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.passionpenguin.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.cachapa.expandablelayout.ExpandableLayout
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URLEncoder
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.properties.Delegates

class Editor : AppCompatActivity() {
    private var attachHash = ""
    private var uid = ""
    private var pid: String? = null
    private var fid: String? = null
    private var attachlist: ArrayList<String> = arrayListOf()
    private var formhash: String = ""

    private var editTextInput by Delegates.notNull<EditText>()
    private var toolbar by Delegates.notNull<LinearLayout>()
    private var openGallery by Delegates.notNull<TextView>()
    private var permWrap by Delegates.notNull<RelativeLayout>()
    private var rewardsWrap by Delegates.notNull<RelativeLayout>()
    private var editorTitle by Delegates.notNull<TextView>()
    private var signature by Delegates.notNull<RelativeLayout>()
    private var signatureInput by Delegates.notNull<CheckBox>()
    private var fontBoldToggle by Delegates.notNull<CheckBox>()
    private var fontItalicToggle by Delegates.notNull<CheckBox>()
    private var fontUnderlinedToggle by Delegates.notNull<CheckBox>()
    private var fontStrikeThroughToggle by Delegates.notNull<CheckBox>()
    private var fontQuoteToggle by Delegates.notNull<CheckBox>()
    private var emotionSelectorToggle by Delegates.notNull<CheckBox>()
    private var imageSelectorToggle by Delegates.notNull<CheckBox>()
    private var fontSizeToggle by Delegates.notNull<CheckBox>()
    private var fontSizeSelector by Delegates.notNull<ExpandableLayout>()
    private var listToggle by Delegates.notNull<CheckBox>()
    private var listSelector by Delegates.notNull<ExpandableLayout>()
    private var bulletedList by Delegates.notNull<RadioButton>()
    private var numberedList by Delegates.notNull<RadioButton>()
    private var typeSelector by Delegates.notNull<Spinner>()
    private var newThreadWrap by Delegates.notNull<LinearLayout>()
    private var photos by Delegates.notNull<LinearLayout>()
    private var backButton by Delegates.notNull<ImageButton>()
    private var submitButton by Delegates.notNull<ImageButton>()
    private var subject by Delegates.notNull<EditText>()
    private var photoWrap by Delegates.notNull<LinearLayout>()
    private var emotionWrap by Delegates.notNull<LinearLayout>()
    private var emotionList by Delegates.notNull<RecyclerView>()
    private var emotionSelectorList by Delegates.notNull<LinearLayout>()

    private fun insert(contentBefore: String = "", contentAfter: String = "", replaceWith: String? = null) {
        with(editTextInput) {
            val start = this.selectionStart
            val end = this.selectionEnd
            val t = this.text.substring(0, start) + contentBefore + (replaceWith ?: this.text.substring(start, end)) + contentAfter + this.text.substring(end, this.length())
            this.setText(t)
            if (contentAfter.isEmpty() || contentBefore.isEmpty()) this.setSelection(end, end)
            else this.setSelection(contentBefore.length + start, contentAfter.length + end)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        editTextInput = findViewById(R.id.editTextInput)
        toolbar = findViewById(R.id.toolbar)
        openGallery = findViewById(R.id.openGallery)
        permWrap = findViewById(R.id.permWrap)
        rewardsWrap = findViewById(R.id.rewardsWrap)
        editorTitle = findViewById(R.id.editorTitle)
        signature = findViewById(R.id.signature)
        signatureInput = findViewById(R.id.signatureInput)
        subject = findViewById(R.id.subject)
        imageSelectorToggle = findViewById(R.id.imageSelectorToggle)
        fontBoldToggle = findViewById(R.id.fontBoldToggle)
        fontItalicToggle = findViewById(R.id.fontItalicToggle)
        fontUnderlinedToggle = findViewById(R.id.fontUnderlinedToggle)
        fontStrikeThroughToggle = findViewById(R.id.fontStrikeThroughToggle)
        fontQuoteToggle = findViewById(R.id.fontQuoteToggle)
        emotionSelectorToggle = findViewById(R.id.emotionSelectorToggle)
        imageSelectorToggle = findViewById(R.id.imageSelectorToggle)
        fontSizeToggle = findViewById(R.id.fontSizeToggle)
        fontSizeSelector = findViewById(R.id.fontSizeSelector)
        listToggle = findViewById(R.id.listToggle)
        listSelector = findViewById(R.id.listSelector)
        bulletedList = findViewById(R.id.bulletedList)
        numberedList = findViewById(R.id.numberedList)
        typeSelector = findViewById(R.id.typeSelector)
        newThreadWrap = findViewById(R.id.newThreadWrap)
        photos = findViewById(R.id.photos)
        backButton = findViewById(R.id.BackButton)
        submitButton = findViewById(R.id.SubmitButton)
        photoWrap = findViewById(R.id.photoWrap)
        emotionWrap = findViewById(R.id.emotionWrap)
        emotionList = findViewById(R.id.emotionList)
        emotionSelectorList = findViewById(R.id.emotionSelectorList)

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
                    originParser = if (reppid != null) Jsoup.parse(NetUtils(this@Editor).retrievePage("http://www.ditiezu.com/forum.php?mod=post&action=reply&fid=$fid&tid=$tid&repquote=$reppid"))
                    else Jsoup.parse(NetUtils(this@Editor).retrievePage("http://www.ditiezu.com/forum.php?mod=post&action=reply&fid=$fid&tid=$tid"))
                    runOnUiThread {
                        editorTitle.text = resources.getString(R.string.replies)
                    }
                }
                TYPE_EDIT -> {
                    if (pid == null || tid == null) onBackPressed()
                    originParser = Jsoup.parse(NetUtils(this@Editor).retrievePage("http://www.ditiezu.com/forum.php?mod=post&action=edit&tid=$tid&pid=$pid"))
                    runOnUiThread {
                        editTextInput.setText(originParser.select("textarea")[0].html())
                        editorTitle.text = resources.getString(R.string.edit)
                    }
                }
                TYPE_SIGHTML -> {
                    runOnUiThread {
                        imageSelectorToggle.visibility = View.GONE
                        editTextInput.setText(SPHelper(this@Editor).getString(TYPE_SIGHTML))
                        editorTitle.text = resources.getString(R.string.signature)
                        signature.visibility = View.GONE
                    }
                }
                TYPE_NEW -> {
                    if (fid == null) onBackPressed()
                    originParser = Jsoup.parse(NetUtils(this@Editor).retrievePage("http://www.ditiezu.com/forum.php?mod=post&action=newthread&fid=$fid"))
                    originParser.select("#typeid option").forEach {
                        typeNameList.add(it.text())
                        typeValueList.add(it.attr("value"))
                    }
                    runOnUiThread {
                        newThreadWrap.visibility = View.VISIBLE
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

                var attachResult = NetUtils(this@Editor).retrievePage("http://www.ditiezu.com/forum.php?mod=ajax&action=imagelist&pid=$pid&fid=$fid&inajax=1&ajaxtarget=imgattachlist").substring(53)
                if (attachResult.length > 11) {
                    attachResult = attachResult.substring(0, attachResult.length - 11)
                    runOnUiThread {
                        Jsoup.parse(attachResult).select("[id^='imageattach']").forEach {
                            val v = attachImageView(this@Editor, it.select("img").attr("src"))
                            v.setOnClickListener { _ -> insert("", "[attachimg]${it.attr("id").substring(11)}[/attachimg]") }
                            photos.addView(v)
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

            formhash = if (type == TYPE_NEW) originParser.select("[name='formhash']").attr("value") else Jsoup.parse(NetUtils(this@Editor).retrievePage("http://www.ditiezu.com/search.php?mod=forum")).select("[name=\"formhash\"]").attr("value")
        }

        permWrap.setOnClickListener {
            object : PopupWindow(
                this@Editor,
                resources.getString(R.string.permission_title),
                resources.getString(R.string.permission_description)
            ) {
                override fun initContent(window: android.widget.PopupWindow, root: ViewGroup) {
                    try {
                        perm = arrayOf(0, 1, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 150, 200, 255)[root.findViewById<Spinner>(R.id.permInput).selectedItemPosition]
                    } catch (e: NumberFormatException) {
                        Alert(this@Editor, resources.getString(R.string.not_a_number)).error()
                    }
                }

                override fun onSubmit(window: android.widget.PopupWindow, root: ViewGroup) {
                    root.addView(LayoutInflater.from(this@Editor).inflate(R.layout.fragment_perm, root, false))
                    with(root.findViewById<Spinner>(R.id.permInput)) {
                        val adapter = ArrayAdapter(this@Editor, R.layout.spinner_dropdown_item, listOf("不限权限", "地铁游客/等待验证用户", "地铁族 Ⅰ", "地铁族 Ⅱ", "地铁族 Ⅲ", "地铁族 Ⅳ", "地铁族 Ⅴ", "地铁族 Ⅵ", "地铁族 Ⅶ", "地铁族 Ⅷ", "地铁族 Ⅸ", "地铁族 Ⅹ", "版主", "超级版主", "管理员"))
                        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                        this.adapter = adapter
                        this.setSelection(10)
                    }
                    window.update()
                }
            }
        }

        rewardsWrap.setOnClickListener {
            object : PopupWindow(
                this@Editor,
                resources.getString(R.string.rewards_title),
                resources.getString(R.string.rewards_description)
            ) {
                override fun onSubmit(window: android.widget.PopupWindow, root: ViewGroup) {
                    try {
                        rewards = root.findViewById<EditText>(R.id.everytime_reward_input).text.toString().toInt()
                        rewardsTimes = root.findViewById<EditText>(R.id.times_input).text.toString().toInt()
                        rewardsMaxTimes = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)[root.findViewById<Spinner>(R.id.max_times_input).selectedItemPosition]
                        rewardsOdds = arrayOf(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1f)[root.findViewById<Spinner>(R.id.odds_input).selectedItemPosition]
                    } catch (e: NumberFormatException) {
                        Alert(this@Editor, resources.getString(R.string.not_a_number)).error()
                    }
                }

                override fun initContent(window: android.widget.PopupWindow, root: ViewGroup) {
                    root.addView(LayoutInflater.from(this@Editor).inflate(R.layout.fragment_rewards, root, false))
                    with(root.findViewById<Spinner>(R.id.max_times_input)) {
                        val adapter = ArrayAdapter(this@Editor, R.layout.spinner_dropdown_item, listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
                        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                        this.adapter = adapter
                        this.setSelection(9)
                    }
                    with(root.findViewById<Spinner>(R.id.odds_input)) {
                        val adapter = ArrayAdapter(this@Editor, R.layout.spinner_dropdown_item, listOf("10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%"))
                        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                        this.adapter = adapter
                        this.setSelection(9)
                    }
                    window.update()
                }
            }
        }

        backButton.setOnClickListener {
            onBackPressed()
        }
        submitButton.setOnClickListener {
            var postAID = ""
            attachlist.forEach {
                postAID += "&attachnew[$it][description]="
            }

            GlobalScope.launch {
                val str = NetUtils(this@Editor).postPage(
                    when (type) {
                        TYPE_EDIT -> "http://www.ditiezu.com/forum.php?mod=post&action=edit&extra=&editsubmit=yes&inajax=1"
                        TYPE_NEW -> "http://www.ditiezu.com/forum.php?mod=post&action=newthread&fid=$fid&extra=&topicsubmit=yes&inajax=1"
                        TYPE_SIGHTML -> "http://www.ditiezu.com/home.php?mod=spacecp&ac=profile"
                        else -> "http://www.ditiezu.com/forum.php?mod=post&action=reply&tid=$tid&replysubmit=yes&inajax=1"
                    },
                    "message=${URLEncoder.encode(editTextInput.text.toString(), "GBK")}&formhash=$formhash$postAID" + when (type) {
                        TYPE_EDIT -> "&pid=$pid&tid=$tid"
                        TYPE_NEW -> "&typeid=" + typeValueList[typeSelector.selectedItemPosition] + "&subject=" + subject.text.toString()
                        TYPE_SIGHTML -> "&profilesubmit=true&sightml=${URLEncoder.encode(editTextInput.text.toString(), "GBK")}"
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
                    str == "" -> {
                    }
                    str.contains("succeed") || str.contains("success") -> {
                        Alert(this@Editor, response).success()
                        Timer().schedule(object : TimerTask() {
                            override fun run() {
                                onBackPressed()
                            }
                        }, 1500)
                        if (type == TYPE_SIGHTML) SPHelper(this@Editor).edit(TYPE_SIGHTML, editTextInput.text.toString())
                    }
                    str.contains("error") -> Alert(this@Editor, response).error()

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
                (element[1] as ExpandableLayout).children.toList()[0] as LinearLayout
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
                (el.parent as ExpandableLayout).toggle()
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
            editTextInput.clearFocus()
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editTextInput.windowToken, 0)
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
            editTextInput.clearFocus()
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editTextInput.windowToken, 0)
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
            text.text = it[2] as String
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
        (emotionSelectorList.children.toList()[0] as TextView).background = resources.getDrawable(R.drawable.border_bottom, null)
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

        editTextInput.setOnFocusChangeListener { _, b ->
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
                            val s = NetUtils(this@Editor).uploadFile(it.path, this@Editor, uid, attachHash, fid.toString(), it.mimeType)
                            if (Pattern.compile("^[0-9]*$").matcher(s).matches()) attachlist.add(s)
                            else Alert(this@Editor, resources.getString(R.string.failed)).error()
                        }
                        GlobalScope.launch {
                            var attachResult = NetUtils(this@Editor).retrievePage("http://www.ditiezu.com/forum.php?mod=ajax&action=imagelist&pid=$pid&fid=$fid&inajax=1&ajaxtarget=imgattachlist").substring(53)
                            if (attachResult.length > 11) {
                                runOnUiThread {
                                    photos.removeAllViews()
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
                                        photos.addView(v)
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