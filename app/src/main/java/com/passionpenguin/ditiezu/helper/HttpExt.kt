package com.passionpenguin.ditiezu.helper

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.webkit.CookieManager
import android.widget.Toast
import androidx.core.content.FileProvider
import com.passionpenguin.ditiezu.R
import kotlinx.android.synthetic.main.fragment_action_bar.view.*
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.nio.charset.Charset

class HttpExt {
    fun retrievePage(url: String, then: (res: String) -> Unit) {
        val urlConnection = URL(url).openConnection() as HttpURLConnection
        val cookieManager = CookieManager.getInstance()
        var cookie = cookieManager.getCookie(url)

        Thread {
            if (cookie == null)
                cookie = ""

            urlConnection.setRequestProperty(
                "Cookie",
                cookie
            )

            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty(
                "Referer",
                "http://www.ditiezu.com"
            )
            urlConnection.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded;"
            )
            urlConnection.setRequestProperty(
                "Origin",
                "http://www.ditiezu.com"
            )
            urlConnection.setRequestProperty(
                "Host",
                "www.ditiezu.com"
            )
            urlConnection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36"
            )
            urlConnection.setRequestProperty(
                "DNT",
                "1"
            )
            urlConnection.setRequestProperty(
                "Proxy-Connection",
                "keep-alive"
            )

            var result: String? = null

            try {
                val inputStream: InputStream = urlConnection.inputStream
                val reader = InputStreamReader(inputStream, "GBK")
                var str = reader.readText()
                var res = ""
                while (str != "") {
                    res += str
                    str = reader.readText()
                }
                result = res
            } catch (e: Exception) {
                result = "Failed Retrieved"
                Log.i("HttpExt Exception", e.toString())
            } finally {
                urlConnection.disconnect()
                if (result == null)
                    then("Failed Retrieved")
                else then(result)
            }
        }.start()

    }

    fun asyncRetrievePage(url: String, charsetName: String = "GBK"): String {
        val urlConnection = URL(url).openConnection() as HttpURLConnection
        val cookieManager = CookieManager.getInstance()
        var cookie = cookieManager.getCookie(url)

        var result = ""

        val thread = Thread {
            if (cookie == null)
                cookie = ""

            urlConnection.setRequestProperty(
                "Cookie",
                cookie
            )

            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty(
                "Referer",
                "http://www.ditiezu.com"
            )
            urlConnection.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded;"
            )
            urlConnection.setRequestProperty(
                "Origin",
                "http://www.ditiezu.com"
            )
            urlConnection.setRequestProperty(
                "Host",
                "www.ditiezu.com"
            )
            urlConnection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36"
            )
            urlConnection.setRequestProperty(
                "DNT",
                "1"
            )
            urlConnection.setRequestProperty(
                "Proxy-Connection",
                "keep-alive"
            )

            try {
                val inputStream: InputStream = urlConnection.inputStream
                val reader = InputStreamReader(inputStream, charsetName)
                var str = reader.readText()
                var res = ""
                while (str != "") {
                    res += str
                    str = reader.readText()
                }
                result = res
            } catch (e: Exception) {
                result = "Failed Retrieved"
                Log.i("HttpExt Exception", e.toString())
            } finally {
                urlConnection.disconnect()
            }
        };
        thread.start()
        thread.join()
        return result
    }

    fun asyncRetrieveNonForumPage(url: String, charsetName: String = "UTF-8"): String {
        val urlConnection = URL(url).openConnection() as HttpURLConnection
        val cookieManager = CookieManager.getInstance()
        var cookie = cookieManager.getCookie(url)

        var result = ""

        val thread = Thread {
            if (cookie == null)
                cookie = ""

            urlConnection.setRequestProperty(
                "Cookie",
                cookie
            )

            try {
                val inputStream: InputStream = urlConnection.inputStream
                val reader = InputStreamReader(inputStream, charsetName)
                var str = reader.readText()
                var res = ""
                while (str != "") {
                    res += str
                    str = reader.readText()
                }
                result = res;
            } catch (e: Exception) {
                result = "Failed Retrieved"
                Log.i("HttpExt Exception", e.toString())
            } finally {
                urlConnection.disconnect()
            }
        };
        thread.start()
        thread.join()
        return result
    }

    fun asyncPostPage(url: String, params: String, followRedirected: Boolean = true): String {
        val urlConnection = URL(url).openConnection() as HttpURLConnection
        urlConnection.instanceFollowRedirects = followRedirected
        val cookieManager = CookieManager.getInstance()
        var cookie = cookieManager.getCookie(url)

        var result = ""

        val thread = Thread {
            if (cookie == null)
                cookie = ""

            urlConnection.setRequestProperty(
                "Cookie",
                cookie
            )

            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty(
                "Referer",
                "http://www.ditiezu.com"
            )
            urlConnection.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded;"
            )
            urlConnection.setRequestProperty(
                "Origin",
                "http://www.ditiezu.com"
            )
            urlConnection.setRequestProperty(
                "Host",
                "www.ditiezu.com"
            )
            urlConnection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36"
            )
            urlConnection.setRequestProperty(
                "DNT",
                "1"
            )
            urlConnection.setRequestProperty(
                "Proxy-Connection",
                "keep-alive"
            )
            urlConnection.doOutput = true
            urlConnection.outputStream.write(params.toByteArray(Charset.forName("GBK")))

            try {
                val inputStream: InputStream = urlConnection.inputStream
                val reader = InputStreamReader(inputStream, "GBK")
                var str = reader.readText()
                var res = ""
                while (str != "") {
                    res += str
                    str = reader.readText()
                }
                result = res;
            } catch (e: Exception) {
                result = "Failed Retrieved"
                Log.i("HttpExt Exception", e.toString())
            } finally {
                urlConnection.disconnect()
            }
        };
        thread.start()
        thread.join()
        return when (urlConnection.responseCode) {
            in 300..399 -> "succeed, '${urlConnection.responseCode} ${urlConnection.responseMessage}'"
            in 400..599 -> "error, '${urlConnection.responseCode} ${urlConnection.responseMessage}'"
            else -> result
        }
    }

    fun checkLogin(): Boolean {
        return asyncRetrievePage("http://www.ditiezu.com/search.php?mod=forum&srchfrom=4000&searchsubmit=yes").indexOf(
            "抱歉，您所在的用户组(地铁游客)无法进行此操作"
        ) == -1
    }

    fun retrieveRedirect(url: String): Array<String>? {
        val c = URL(url).openConnection() as HttpURLConnection
        c.instanceFollowRedirects = false
        var result = arrayOf("-1", "1")
        val t = Thread {
            c.setRequestProperty(
                "Cookie",
                CookieManager.getInstance().getCookie(url)
            )

            c.requestMethod = "POST"
            c.setRequestProperty(
                "Referer",
                "http://www.ditiezu.com"
            )
            c.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded;"
            )
            c.setRequestProperty(
                "Origin",
                "http://www.ditiezu.com"
            )
            c.setRequestProperty(
                "Host",
                "www.ditiezu.com"
            )
            c.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36"
            )
            c.setRequestProperty(
                "DNT",
                "1"
            )
            c.setRequestProperty(
                "Proxy-Connection",
                "keep-alive"
            )
            val it: String = c.getHeaderField("Location") ?: ""
            Log.i(it, it)
            it.let {
                result =
                    if (c.responseCode == HttpURLConnection.HTTP_MOVED_PERM || c.responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                        arrayOf(
                            it.substring(
                                it.indexOf("tid=") + 4,
                                it.indexOf("&", it.indexOf("tid=") + 4)
                            ),
                            it.substring(
                                it.indexOf("page=") + 5, it.indexOf("#", it.indexOf("page=") + 5)
                            )
                        )
                    } else arrayOf("1", "1")
            }
        }
        t.start()
        t.join()
        return result
    }


    //下载器
    private var downloadManager: DownloadManager? = null
    private var mContext: Context? = null

    //下载的ID
    private var downloadId: Long = 0
    private var name: String? = null
    private var pathstr: String? = null

    fun downloadUtils(
        context: Context?,
        url: String,
        name: String
    ) {
        mContext = context
        downloadAPK(url, name)
        this.name = name
    }

    //下载apk
    private fun downloadAPK(url: String, name: String) {
        //创建下载任务
        val request = DownloadManager.Request(Uri.parse(url))
        //移动网络情况下是否允许漫游
        request.setAllowedOverRoaming(false)
        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setTitle(mContext?.resources?.getString(R.string.app_name))
        request.setDescription("Downloading...")

        //设置下载的路径
        val file = File(
            mContext!!.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            name
        )
        request.setDestinationUri(Uri.fromFile(file))
        pathstr = file.absolutePath
        //获取DownloadManager
        if (downloadManager == null) downloadManager =
            mContext!!.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        //将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
        if (downloadManager != null) {
            downloadId = downloadManager!!.enqueue(request)
        }

        //注册广播接收者，监听下载状态
        mContext!!.registerReceiver(
            receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    //广播监听下载的各个状态
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            checkStatus()
        }
    }

    //检查下载状态
    private fun checkStatus() {
        val query = DownloadManager.Query()
        //通过下载的id查找
        query.setFilterById(downloadId)
        val cursor: Cursor = downloadManager!!.query(query)
        if (cursor.moveToFirst()) {
            val status: Int = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            when (status) {
                DownloadManager.STATUS_PAUSED -> {
                }
                DownloadManager.STATUS_PENDING -> {
                }
                DownloadManager.STATUS_RUNNING -> {
                }
                DownloadManager.STATUS_SUCCESSFUL -> {
                    //下载完成安装APK
                    installAPK()
                    cursor.close()
                }
                DownloadManager.STATUS_FAILED -> {
                    Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show()
                    cursor.close()
                    mContext!!.unregisterReceiver(receiver)
                }
            }
        }
    }

    private fun installAPK() {
        setPermission(pathstr)
        val intent = Intent(Intent.ACTION_VIEW)
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        //Android 7.0以上要使用FileProvider
        if (Build.VERSION.SDK_INT >= 24) {
            val file = File(pathstr)
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            val apkUri =
                FileProvider.getUriForFile(
                    mContext!!,
                    "com.passionpenguin.ditiezu.fileprovider",
                    file
                )
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(
                Uri.fromFile(
                    File(
                        Environment.DIRECTORY_DOWNLOADS,
                        name
                    )
                ), "application/vnd.android.package-archive"
            )
        }
        mContext!!.startActivity(intent)
    }

    //修改文件权限
    private fun setPermission(absolutePath: String?) {
        val command = "chmod 777 $absolutePath"
        val runtime = Runtime.getRuntime()
        try {
            runtime.exec(command)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}