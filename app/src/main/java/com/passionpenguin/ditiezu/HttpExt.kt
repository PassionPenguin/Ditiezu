package com.passionpenguin.ditiezu

import android.util.Log
import android.webkit.CookieManager
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection

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
                result = res;
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

    fun asyncRetrievePage(url: String): String {
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
        return result
    }

    fun checkLogin(): Boolean {
        return asyncRetrievePage("http://www.ditiezu.com/search.php?mod=forum&srchfrom=4000&searchsubmit=yes").indexOf(
            "抱歉，您所在的用户组(地铁游客)无法进行此操作"
        ) == -1
    }

    fun openConn(url: String, then: (urlConn: InputStream?) -> Unit) {
        var urlconn: URLConnection
        val thread = Thread {
            try {
                urlconn = URL(url).openConnection()
                then(urlconn.getInputStream())
            } catch (Exception: java.io.FileNotFoundException) {
                then(null)
            } catch (Exception: java.lang.Exception) {
                then(null)
            }
        }.start()
    }
}