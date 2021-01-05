/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   NetUtils.kt
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

package com.passionpenguin

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.NetworkOnMainThreadException
import android.util.Log
import android.webkit.CookieManager
import androidx.annotation.WorkerThread
import com.ditiezu.android.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.bither.util.NativeUtil
import org.jsoup.Jsoup
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset

@Suppress("BlockingMethodInNonBlockingContext")
class NetUtils(private val activity: Activity) {
    private val pref = activity.getSharedPreferences(activity.getString(R.string.app_name), Context.MODE_PRIVATE)
    private val userAgent = pref.getString("userAgent", activity.getString(R.string.user_agent_default))

    @WorkerThread
    @Throws(NetworkOnMainThreadException::class)
    private fun openConn(url: String, retrieveAsDesktopPage: Boolean): HttpURLConnection {
        val urlConnection = URL(url).openConnection() as HttpURLConnection
        urlConnection.setRequestProperty("Cookie", CookieManager.getInstance().getCookie(url) ?: "")
        urlConnection.setRequestProperty("Referer", "http://www.ditiezu.com")
        urlConnection.setRequestProperty("Origin", "http://www.ditiezu.com")
        urlConnection.setRequestProperty("Host", "www.ditiezu.com")
        urlConnection.setRequestProperty("DNT", "1")
        urlConnection.setRequestProperty("Proxy-Connection", "keep-alive")
        if (retrieveAsDesktopPage)
            urlConnection.setRequestProperty("User-Agent", userAgent)
        return urlConnection
    }

    @Throws(IOException::class)
    fun openHttpUrlConn(strURL: String, returnVal: (inputStream: InputStream?) -> Unit) {
        val t = Thread {
            val url = URL(strURL)
            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("User-Agent", userAgent)
            conn.setRequestProperty("Cookie", CookieManager.getInstance().getCookie(strURL))
            try {
                conn.requestMethod = "GET"
                conn.connect()
                if (conn.responseCode == HttpURLConnection.HTTP_OK) returnVal(conn.inputStream)
            } catch (ex: Exception) {
            }
        }
        t.start()
    }

    @WorkerThread
    private fun storeCookie(urlConnection: HttpURLConnection) {
        with(urlConnection.headerFields["Set-Cookie"]) {
            this?.forEach {
                CookieManager.getInstance().setCookie(urlConnection.url.toString(), it, null)
            }
        }
    }

    suspend fun retrievePage(url: String, retrieveAsDesktopPage: Boolean = true, charsetName: String = "GBK", customHeader: Array<HttpHeader> = arrayOf()): String {
        /**
         * [Function] retrievePage
         * @param url: String -> Remote URL
         * @param retrieveAsDesktopPage: Boolean -> if true: use USERAGENT_DESKTOP as User-Agent
         * @param charsetName: String -> Charset Name for input stream reader
         * @param customHeader: Array<HttpHeader> -> Headers that will be sent with the request
         */

        return withContext(Dispatchers.IO) {
            var result: String?
            val urlConnection = openConn(url, retrieveAsDesktopPage)
            urlConnection.requestMethod = "GET"
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

                storeCookie(urlConnection)
            } catch (e: Exception) {
                result = ""
            } finally {
                urlConnection.disconnect()
            }
            if (urlConnection.responseCode >= 400 || urlConnection.responseCode == 0) {
                Alert(activity, "HTTP ${urlConnection.responseCode.toString() + urlConnection.responseMessage}").error()
                result = ""
            }
            checkLogin(result ?: "")
            result ?: ""
        }
    }

    suspend fun retrieveBitmap(url: String, retrieveAsDesktopPage: Boolean = true): Bitmap? {
        /**
         * [Function] retrieveBitmap
         * @param url: String -> Remote URL
         * @param retrieveAsDesktopPage: Boolean -> if true: use USERAGENT_DESKTOP as User-Agent
         */

        return withContext(Dispatchers.IO) {
            val urlConnection = openConn(url, retrieveAsDesktopPage)
            urlConnection.requestMethod = "GET"
            storeCookie(urlConnection)
            try {
                BitmapFactory.decodeStream(urlConnection.inputStream)
            } catch (ignored: Exception) {
                println(ignored)
                null
            }
        }
    }

    suspend fun postPage(url: String, params: String, customHeader: Array<HttpHeader> = arrayOf(), retrieveAsDesktopPage: Boolean = true, charsetName: String = "GBK", followRedirected: Boolean = true): String {
        return withContext(Dispatchers.IO) {
            var result: String?
            val urlConnection = openConn(url, retrieveAsDesktopPage)
            customHeader.forEach {
                urlConnection.setRequestProperty(it.key, it.value)
            }
            urlConnection.requestMethod = "POST"
            urlConnection.doOutput = true
            urlConnection.outputStream.write(params.toByteArray(Charset.forName(charsetName)))
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

                storeCookie(urlConnection)
            } catch (e: Exception) {
                result = ""
            } finally {
                urlConnection.disconnect()
            }
            if (urlConnection.responseCode >= 400 || urlConnection.responseCode == 0) {
                Alert(activity, "HTTP ${urlConnection.responseCode.toString() + urlConnection.responseMessage}").error()
                result = ""
            }
            checkLogin(result ?: "")
            result ?: ""
        }
    }

    private fun checkLogin(content: String) {
        if (Jsoup.parse(content).select("#um").isNotEmpty()) {
            SPHelper(activity).edit("login", "true")
            println(true)
        }
    }

    suspend fun checkLogin(): Boolean {
        return withContext(Dispatchers.IO) {
            when (Jsoup.parse(retrievePage("http://www.ditiezu.com/forum.php?gid=149")).select("#lsform").isEmpty()) {
                true -> {
                    SPHelper(activity).edit("login", "true")
                    true
                }
                else -> {
                    SPHelper(activity).edit("login", "false")
                    false
                }
            }
        }
    }

    suspend fun retrieveRedirect(url: String, retrieveAsDesktopPage: Boolean = true, charsetName: String = "GBK"): Array<String>? {
        return withContext(Dispatchers.IO) {
            var result: Array<String>?
            val urlConnection = openConn(url, retrieveAsDesktopPage)
            urlConnection.requestMethod = "GET"
            urlConnection.instanceFollowRedirects = false
            try {
                val l = urlConnection.getHeaderField("Location") ?: ""
                result = l.let {
                    if (urlConnection.responseCode == HttpURLConnection.HTTP_MOVED_PERM || urlConnection.responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                        arrayOf(
                            it.substring(
                                it.indexOf("tid=") + 4,
                                it.indexOf("&", it.indexOf("tid=") + 4)
                            ),
                            it.substring(
                                it.indexOf("page=") + 5,
                                it.indexOf("#", it.indexOf("page=") + 5)
                            )
                        )
                    } else arrayOf("1", "1")
                }
            } catch (e: Exception) {
                result = arrayOf("1", "1")
            } finally {
                urlConnection.disconnect()
            }
            if (urlConnection.responseCode >= 400 || urlConnection.responseCode == 0) {
                Alert(activity, "HTTP ${urlConnection.responseCode.toString() + urlConnection.responseMessage}").error()
                result = arrayOf("1", "1")
            }
            result ?: arrayOf("1", "1")
        }
    }
//
//    //下载器
//    private var downloadManager: DownloadManager? = null
//    private var mContext: Context? = null
//
//    //下载的ID
//    private var downloadId: Long = 0
//    private var name: String? = null
//    private var pathstr: String? = null
//
//    fun downloadUtils(context: Context?, url: String, name: String) {
//        mContext = context
//        downloadAPK(url, name)
//        this.name = name
//    }
//
//    //下载apk
//    private fun downloadAPK(url: String, name: String) {
//        //创建下载任务
//        val request = DownloadManager.Request(Uri.parse(url))
//        //移动网络情况下是否允许漫游
//        request.setAllowedOverRoaming(false)
//        //在通知栏中显示，默认就是显示的
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
//        request.setTitle(mContext?.resources?.getString(R.string.app_name))
//        request.setDescription("Downloading...")
//
//        //设置下载的路径
//        val file = File(
//            mContext!!.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
//            name
//        )
//        request.setDestinationUri(Uri.fromFile(file))
//        pathstr = file.absolutePath
//        //获取DownloadManager
//        if (downloadManager == null) downloadManager =
//            mContext!!.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
//        //将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
//        if (downloadManager != null) {
//            downloadId = downloadManager!!.enqueue(request)
//        }
//
//        //注册广播接收者，监听下载状态
//        mContext!!.registerReceiver(
//            receiver,
//            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
//        )
//    }
//
//    //广播监听下载的各个状态
//    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            checkStatus()
//        }
//    }
//
//    //检查下载状态
//    private fun checkStatus() {
//        val query = DownloadManager.Query()
//        //通过下载的id查找
//        query.setFilterById(downloadId)
//        val cursor: Cursor = downloadManager!!.query(query)
//        if (cursor.moveToFirst()) {
//            when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
//                DownloadManager.STATUS_PAUSED -> {
//                }
//                DownloadManager.STATUS_PENDING -> {
//                }
//                DownloadManager.STATUS_RUNNING -> {
//                }
//                DownloadManager.STATUS_SUCCESSFUL -> {
//                    //下载完成安装APK
//                    installAPK()
//                    cursor.close()
//                }
//                DownloadManager.STATUS_FAILED -> {
//                    Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show()
//                    cursor.close()
//                    mContext!!.unregisterReceiver(receiver)
//                }
//            }
//        }
//    }
//
//    private fun installAPK() {
//        setPermission(pathstr)
//        val intent = Intent(Intent.ACTION_VIEW)
//        // 由于没有在Activity环境下启动Activity,设置下面的标签
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        //Android 7.0以上要使用FileProvider
//        if (Build.VERSION.SDK_INT >= 24) {
//            val file = File(pathstr.toString())
//            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
//            val apkUri =
//                FileProvider.getUriForFile(
//                    mContext!!,
//                    "com.ditiezu.android.fileprovider",
//                    file
//                )
//            //添加这一句表示对目标应用临时授权该Uri所代表的文件
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
//        } else {
//            intent.setDataAndType(
//                Uri.fromFile(
//                    File(
//                        Environment.DIRECTORY_DOWNLOADS,
//                        name.toString()
//                    )
//                ), "application/vnd.android.package-archive"
//            )
//        }
//        mContext!!.startActivity(intent)
//    }
//
//    //修改文件权限
//    private fun setPermission(absolutePath: String?) {
//        val command = "chmod 777 $absolutePath"
//        val runtime = Runtime.getRuntime()
//        try {
//            runtime.exec(command)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }

    @Throws(FileNotFoundException::class, IOException::class)
    fun getBitmapFormUri(ac: Activity, uri: Uri?): Bitmap? {
        var input = ac.contentResolver.openInputStream(uri!!)
        val onlyBoundsOptions = BitmapFactory.Options()
        onlyBoundsOptions.inJustDecodeBounds = true
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888 //optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
        input!!.close()
        val originalWidth = onlyBoundsOptions.outWidth
        val originalHeight = onlyBoundsOptions.outHeight
        if (originalWidth == -1 || originalHeight == -1) return null
        input = ac.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(input, null, null)
        input!!.close()
        return bitmap
    }

    suspend fun uploadFile(sourceFileUri: String, activity: Activity, uid: String, hash: String, fid: String, charsetName: String = "GBK"): String {
        val lineEnd = "\n"
        val twoHyphens = "--"
        val boundary = "----WebkitAppBoundary"
        var bytesRead: Int
        var bytesAvailable: Int
        var bufferSize: Int
        val maxBufferSize = 1 * 1024 * 1024
        var output: String
        val sourceFile = File(sourceFileUri)
        if (SPHelper(activity).getString("enable_compress") != "disabled")
            try {
                NativeUtil.compressBitmap(getBitmapFormUri(activity, Uri.fromFile(sourceFile))!!, sourceFile.absolutePath, true)
            } catch (e: Exception) {
                Alert(activity, activity.resources.getString(R.string.compress_failed)).error()
            }
        return if (!sourceFile.isFile) {
            Log.e("uploadFile", "Source File not exist : ${sourceFile.absolutePath}")
            Alert(activity, activity.resources.getString(R.string.file_not_found)).error()
            ""
        } else {
            withContext(Dispatchers.IO) {
                val urlConn = openConn(
                    "http://www.ditiezu.com/misc.php?mod=swfupload&operation=upload&hash=$hash&uid=$uid&type=image&filetype=image&fid=$fid",
                    true
                )
                try {
                    // open a URL connection to the Servlet
                    val fileInputStream = FileInputStream(sourceFile)
                    urlConn.doInput = true // Allow Inputs
                    urlConn.doOutput = true // Allow Outputs
                    urlConn.useCaches = false // Don't use a Cached Copy
                    urlConn.requestMethod = "POST"
                    urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
                    val dos = DataOutputStream(urlConn.outputStream)

                    // add parameters
                    dos.writeBytes(twoHyphens + boundary + lineEnd)
                    dos.writeBytes("Content-Disposition: form-data; name=\"uid\" $lineEnd$lineEnd$uid$lineEnd")
                    dos.writeBytes(twoHyphens + boundary + lineEnd)
                    dos.writeBytes("Content-Disposition: form-data; name=\"hash\" $lineEnd$lineEnd$hash$lineEnd")
                    dos.writeBytes(twoHyphens + boundary + lineEnd)
                    dos.writeBytes("Content-Disposition: form-data; name=\"Filedata\";${lineEnd}filename=\"${sourceFile.name}\"$lineEnd$lineEnd")
                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available()
                    bufferSize = bytesAvailable.coerceAtMost(maxBufferSize)
                    val buffer = ByteArray(bufferSize)

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize)
                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize)
                        bytesAvailable = fileInputStream.available()
                        bufferSize = bytesAvailable.coerceAtMost(maxBufferSize)
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize)
                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd)
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)

                    // close the streams
                    fileInputStream.close()
                    dos.flush()
                    dos.close()

                    try {
                        val inputStream: InputStream = urlConn.inputStream
                        val reader = InputStreamReader(inputStream, charsetName)
                        var str = reader.readText()
                        var res = ""
                        while (str != "") {
                            res += str
                            str = reader.readText()
                        }
                        output = res
                    } catch (e: Exception) {
                        output = "Failed Retrieved"
                        Log.i("HttpExt Exception", e.toString())
                    } finally {
                        urlConn.disconnect()
                    }
                } catch (ex: MalformedURLException) {
                    ex.printStackTrace()
                    Log.e("Upload file to server", "error: " + ex.message, ex)
                    output = "Failed Retrieved"
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("", "Exception" + "Exception : " + e.message, e)
                    output = "Failed Retrieved"
                }
                output
            }
        }
    }
}

class HttpHeader(val key: String, val value: String)