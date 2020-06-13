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

        val e = intent.extras
        val tid = e?.get("tid")
        val reppid = e?.get("reppid")
        val type = e?.get("type") ?: "reply"
        pid = e?.get("pid").toString()
        fid = e?.get("fid").toString()
        lateinit var originParser: Document
        val typeNameList: ArrayList<String> = arrayListOf()
        val typeValueList: ArrayList<String> = arrayListOf()
        when (type) {
            "reply" -> {
                if (tid == null) onBackPressed()
                originParser = if (reppid != null)
                    Jsoup.parse(HttpExt().asyncRetrievePage("http://www.ditiezu.com/forum.php?mod=post&action=reply&fid=$fid&tid=$tid&repquote=$reppid"))
                else Jsoup.parse(HttpExt().asyncRetrievePage("http://www.ditiezu.com/forum.php?mod=post&action=reply&fid=$fid&tid=$tid"))
            }
            "edit" -> {
                if (pid == null || tid == null) onBackPressed()
                originParser =
                    Jsoup.parse(HttpExt().asyncRetrievePage("http://www.ditiezu.com/forum.php?mod=post&action=edit&tid=$tid&pid=$pid"))
                EditTextInput.setText(originParser.select("textarea")[0].html())
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
        attachHash = originParser.select("[name=\"hash\"]").attr("value")

        var s = originParser.select("body").html()
        val pattern = Pattern.compile("IMGUNUSEDAID\\[\\d*] = '(\\d*)'")
        var matcher: Matcher = pattern.matcher(s)
        while (matcher.find()) {
            attachlist.add(matcher.group(1))
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
                    insert("[attachimg]${it.attr("id").substring(11)}[/attachimg]", "", "")
                }
                ImageList.addView(v)
            }
        }

        uid = originParser.select("[name=\"uid\"]").attr("value")

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
                var postAID = ""
                attachlist.forEach {
                    postAID += "&attachnew[$it][description]="
                }
                val str = HttpExt().asyncPostPage(
                    when (type) {
                        "edit" -> "http://www.ditiezu.com/forum.php?mod=post&action=edit&extra=&editsubmit=yes&inajax=1"
                        "newthread" -> "http://www.ditiezu.com/forum.php?mod=post&action=newthread&fid=$fid&extra=&topicsubmit=yes&inajax=1"
                        else -> "http://www.ditiezu.com/forum.php?mod=post&action=reply&tid=$tid&replysubmit=yes&inajax=1"
                    },
                    "message=" + URLEncoder.encode(
                        EditTextInput.text.toString(),
                        "GBK"
                    ) + "&formhash=$formhash$postAID&subject=" + when (type) {
                        "edit" -> {
                            "&pid=$pid&tid=$tid"
                        } // Edit
                        "newthread" -> {
                            "&typeid=" + typeValueList[typeSelector.selectedItemPosition] + "&subject=" + subject.text.toString()
                        } // New Thread
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
                    str.indexOf("_rate('") + 33,
                    str.indexOf(
                        "'", str.indexOf("_rate('") + 34
                    )
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
                    str.contains("succeed") -> {
                        Dialog().tip(
                            response,
                            R.drawable.ic_baseline_close_24,
                            R.color.danger,
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
                .theme(R.style.picture_default_style)// xml样式配制 R.style.picture_default_style、picture_WeChat_style or 更多参考Demo
                .selectionMode(PictureConfig.MULTIPLE)//单选or多选 PictureConfig.SINGLE PictureConfig.MULTIPLE
                .isPageStrategy(true)//开启分页模式，默认开启另提供两个参数；pageSize每页总数；isFilterInvalidFile是否过滤损坏图片
                .isSingleDirectReturn(true)//PictureConfig.SINGLE模式下是否直接返回
                .isCamera(true)//列表是否显示拍照按钮
                .isZoomAnim(true)//图片选择缩放效果
                .maxSelectNum(20)//最大选择数量,默认9张
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)//列表每行显示个数
                .isGif(true)//是否显示gif
                .freeStyleCropEnabled(true)//裁剪框是否可拖拽
                .circleDimmedLayer(true)// 是否开启圆形裁剪
                .rotateEnabled(true)//裁剪是否可旋转图片
                .scaleEnabled(true)//裁剪是否可放大缩小图片
                .isDragFrame(true)//是否可拖动裁剪框(固定)
                .compress(true)//是否压缩
                .compressFocusAlpha(false)//压缩后是否保持图片的透明通道
                .isMultipleSkipCrop(true)//多图裁剪是否支持跳过
                .isMultipleRecyclerAnimation(true)// 多图裁剪底部列表显示动画效果
                .isReturnEmpty(true)//未选择数据时按确定是否可以退出
                .isAndroidQTransform(true)//Android Q版本下是否需要拷贝文件至应用沙盒内
                .isOriginalImageControl(true)//开启原图选项
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
                                insert("[attachimg]${it.attr("id").substring(11)}[\\attachimg]", "")
                            }
                            ImageList.addView(v)
                        }
                    }
                }
            }
        }
    }
}