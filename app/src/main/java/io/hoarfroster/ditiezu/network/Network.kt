package io.hoarfroster.ditiezu.network

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.NetworkOnMainThreadException
import android.util.Log
import android.webkit.CookieManager
import androidx.annotation.WorkerThread
import com.hjq.toast.ToastUtils
import io.hoarfroster.ditiezu.utilities.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset

@Suppress("BlockingMethodInNonBlockingContext")
class Network(private val activity: Activity) {
    companion object {
        private val userAgent =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.192 Safari/537.36"

        @WorkerThread
        @Throws(NetworkOnMainThreadException::class)
        private fun openConn(url: String, retrieveAsDesktopPage: Boolean): HttpURLConnection {
            val urlConnection = URL(url).openConnection() as HttpURLConnection
            urlConnection.setRequestProperty(
                "Cookie",
                CookieManager.getInstance().getCookie(url) ?: ""
            )
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

        suspend fun retrievePage(
            url: String,
            retrieveAsDesktopPage: Boolean = true,
            charsetName: String = "GBK",
            customHeader: Array<HttpHeader> = arrayOf()
        ): String {
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
                    ToastUtils.show("HTTP ${urlConnection.responseCode.toString() + urlConnection.responseMessage}")
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

        suspend fun postPage(
            url: String,
            params: String,
            customHeader: Array<HttpHeader> = arrayOf(),
            retrieveAsDesktopPage: Boolean = true,
            charsetName: String = "GBK",
            followRedirected: Boolean = true
        ): String {
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
                    ToastUtils.show("HTTP ${urlConnection.responseCode.toString() + urlConnection.responseMessage}")
                    result = ""
                }
                checkLogin(result ?: "")
                result ?: ""
            }
        }

        private fun checkLogin(content: String) {
            if (Jsoup.parse(content).select("#um").isNotEmpty()) {
                SharedPreferences.edit("login", "true")
                println(true)
            }
        }

        suspend fun checkLogin(): Boolean {
            return withContext(Dispatchers.IO) {
                when (Jsoup.parse(retrievePage("http://www.ditiezu.com/forum.php?gid=149"))
                    .select("#lsform").isEmpty()) {
                    true -> {
                        SharedPreferences.edit("login", "true")
                        true
                    }
                    else -> {
                        SharedPreferences.edit("login", "false")
                        false
                    }
                }
            }
        }

        suspend fun retrieveRedirect(
            url: String,
            retrieveAsDesktopPage: Boolean = true,
            charsetName: String = "GBK"
        ): Array<String>? {
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
                    ToastUtils.show("HTTP ${urlConnection.responseCode.toString() + urlConnection.responseMessage}")
                    result = arrayOf("1", "1")
                }
                result ?: arrayOf("1", "1")
            }
        }

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

        suspend fun uploadFile(
            sourceFileUri: String,
            activity: Activity,
            uid: String,
            hash: String,
            fid: String,
            charsetName: String = "GBK"
        ): String {
            val lineEnd = "\n"
            val twoHyphens = "--"
            val boundary = "----WebkitAppBoundary"
            var bytesRead: Int
            var bytesAvailable: Int
            var bufferSize: Int
            val maxBufferSize = 1 * 1024 * 1024
            var output: String
            val sourceFile = File(sourceFileUri)
            return if (!sourceFile.isFile) {
                Log.e("uploadFile", "Source File not exist : ${sourceFile.absolutePath}")
                ToastUtils.show("File Not Found")
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
                        urlConn.setRequestProperty(
                            "Content-Type",
                            "multipart/form-data; boundary=$boundary"
                        )
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
}

class HttpHeader(val key: String, val value: String)