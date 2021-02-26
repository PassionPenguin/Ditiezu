package io.hoarfroster.ditiezu.utilities

import android.os.NetworkOnMainThreadException
import androidx.annotation.WorkerThread
import io.hoarfroster.ditiezu.models.ThreadListItem
import io.hoarfroster.ditiezu.network.Network
import org.jsoup.Jsoup

class DataFetcher {
    companion object {
        @WorkerThread
        @Throws(NetworkOnMainThreadException::class)
        suspend fun fetchMainPage(): List<ThreadListItem> {
            val str = Network.retrievePage("http://www.ditiezu.com/")
            val document = Jsoup.parse(str)

            return document.select("#portal_block_55_content li").map { e ->
                ThreadListItem(
                    e.selectFirst(".blackvs").text(),
                    "",
                    e.selectFirst("code").text(),
                    Regex("uid-([0-9]+?).html").find(
                        e.selectFirst("code a").attr("href")
                    )?.groupValues?.get(1)?.toInt() ?: -1,
                    e.selectFirst(".orgen").text()
                )
            }
        }
    }
}