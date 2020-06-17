package com.passionpenguin.ditiezu.helper

import android.content.Context
import com.passionpenguin.ditiezu.R

class CategoryContent(val context: Context) {
    private fun string(id: Int): String {
        var str = ""
        try {
            str = context.resources.getString(id)
        } catch (ignored: Exception) {
        }
        return str
    }

    val categoryList = listOf(
        CategoryItem(
            string(R.string.category_Beijing),
            string(R.string.description_Beijing),
            R.drawable.beijing,
            "", 7
        ),
        CategoryItem(
            string(R.string.category_Tianjin),
            string(R.string.description_Tianjin),
            R.drawable.tianjin,
            "", 6
        ),
        CategoryItem(
            string(R.string.category_Shanghai),
            string(R.string.description_Shanghai),
            R.drawable.shanghai,
            "", 8
        ),
        CategoryItem(
            string(R.string.category_Guangzhou),
            string(R.string.description_Guangzhou),
            R.drawable.guangzhou,
            "", 23
        ),
        CategoryItem(
            string(R.string.category_Changchun),
            string(R.string.description_Changchun),
            R.drawable.changchun,
            "", 40
        ),
        CategoryItem(
            string(R.string.category_Dalian),
            string(R.string.description_Dalian),
            R.drawable.dalian,
            "", 41
        ),
        CategoryItem(
            string(R.string.category_Wuhan),
            string(R.string.description_Wuhan),
            R.drawable.wuhan,
            "", 39
        ),
        CategoryItem(
            string(R.string.category_Chongqing),
            string(R.string.description_Chongqing),
            R.drawable.chongqing,
            "", 38
        ),
        CategoryItem(
            string(R.string.category_Shenzhen),
            string(R.string.description_Shenzhen),
            R.drawable.shenzhen,
            "", 24
        ),
        CategoryItem(
            string(R.string.category_Nanjing),
            string(R.string.description_Nanjing),
            R.drawable.nanjing,
            "", 22
        ),
        CategoryItem(
            string(R.string.category_Chengdu),
            string(R.string.description_Chengdu),
            R.drawable.chengdu,
            "", 53
        ),
        CategoryItem(
            string(R.string.category_Shenyang),
            string(R.string.description_Shenyang),
            R.drawable.shenyang,
            "", 50
        ),
        CategoryItem(
            string(R.string.category_Foshan),
            string(R.string.description_Foshan),
            R.drawable.foshan,
            "", 56
        ),
        CategoryItem(
            string(R.string.category_Xian),
            string(R.string.description_Xian),
            R.drawable.xian,
            "", 54
        ),
        CategoryItem(
            string(R.string.category_Suzhou),
            string(R.string.description_Suzhou),
            R.drawable.suzhou,
            "", 51
        ),
        CategoryItem(
            string(R.string.category_Kunming),
            string(R.string.description_Kunming),
            R.drawable.kunming,
            "", 70
        ),
        CategoryItem(
            string(R.string.category_Hangzhou),
            string(R.string.description_Hangzhou),
            R.drawable.hangzhou,
            "", 52
        ),
        CategoryItem(
            string(R.string.category_Harbin),
            string(R.string.description_Harbin),
            R.drawable.harbin,
            "", 55
        ),
        CategoryItem(
            string(R.string.category_Zhengzhou),
            string(R.string.description_Zhengzhou),
            R.drawable.zhengzhou,
            "", 64
        ),
        CategoryItem(
            string(R.string.category_Changsha),
            string(R.string.description_Changsha),
            R.drawable.changsha,
            "", 67
        ),
        CategoryItem(
            string(R.string.category_Ningbo),
            string(R.string.description_Ningbo),
            R.drawable.ningbo,
            "", 65
        ),
        CategoryItem(
            string(R.string.category_Wuxi),
            string(R.string.description_Wuxi),
            R.drawable.wuxi,
            "", 68
        ),
        CategoryItem(
            string(R.string.category_Qingdao),
            string(R.string.description_Qingdao),
            R.drawable.qingdao,
            "", 66
        ),
        CategoryItem(
            string(R.string.category_Nanchang),
            string(R.string.description_Nanchang),
            R.drawable.nanchang,
            "", 71
        ),
        CategoryItem(
            string(R.string.category_Fuzhou),
            string(R.string.description_Fuzhou),
            R.drawable.fuzhou,
            "", 72
        ),
        CategoryItem(
            string(R.string.category_Dongguan),
            string(R.string.description_Dongguan),
            R.drawable.dongguan,
            "", 75
        ),
        CategoryItem(
            string(R.string.category_Nanning),
            string(R.string.description_Nanning),
            R.drawable.nanning,
            "", 73
        ),
        CategoryItem(
            string(R.string.category_Hefei),
            string(R.string.description_Hefei),
            R.drawable.hefei,
            "", 74
        ),
        CategoryItem(
            string(R.string.category_Shijiazhuang),
            string(R.string.description_Shijiazhuang),
            R.drawable.shijiazhuang,
            "", 140
        ),
        CategoryItem(
            string(R.string.category_Guiyang),
            string(R.string.description_Guiyang),
            R.drawable.guiyang,
            "", 76
        ),
        CategoryItem(
            string(R.string.category_Xiamen),
            string(R.string.description_Xiamen),
            R.drawable.xiamen,
            "", 77
        ),
        CategoryItem(
            string(R.string.category_Urumqi),
            string(R.string.description_Urumqi),
            R.drawable.urumqi,
            "", 143
        ),
        CategoryItem(
            string(R.string.category_Wenzhou),
            string(R.string.description_Wenzhou),
            R.drawable.wenzhou,
            "", 142
        ),
        CategoryItem(
            string(R.string.category_Jinan),
            string(R.string.description_Jinan),
            R.drawable.jinan,
            "", 148
        ),
        CategoryItem(
            string(R.string.category_Lanzhou),
            string(R.string.description_Lanzhou),
            R.drawable.lanzhou,
            "", 78
        ),
        CategoryItem(
            string(R.string.category_Changzhou),
            string(R.string.description_Changzhou),
            R.drawable.changzhou,
            "", 48
        ),
        CategoryItem(
            string(R.string.category_Xuzhou),
            string(R.string.description_Xuzhou),
            R.drawable.xuzhou,
            "", 144
        ),
        CategoryItem(
            string(R.string.category_Hohhot),
            string(R.string.description_Hohhot),
            R.drawable.hohhot,
            "", 151
        ),
        CategoryItem(
            string(R.string.category_Hongkong),
            string(R.string.description_Hongkong),
            R.drawable.hongkong,
            "", 28
        ),
        CategoryItem(
            string(R.string.category_Macau),
            string(R.string.description_Macau),
            R.drawable.macau,
            "", 79
        ),
        CategoryItem(
            string(R.string.category_Taiwan),
            string(R.string.description_Taiwan),
            R.drawable.taiwan,
            "", 36
        ),
        CategoryItem(
            string(R.string.category_Oversea),
            string(R.string.description_Oversea),
            R.drawable.oversea,
            "", 47
        ),
        CategoryItem(
            string(R.string.category_Comprehensive),
            string(R.string.description_Comprehensive),
            R.drawable.comprehensive,
            "", 37
        ),
        CategoryItem(
            string(R.string.category_Collection),
            string(R.string.description_Collection),
            R.drawable.stamp,
            "", 33
        ),
        CategoryItem(
            string(R.string.category_City),
            string(R.string.description_City),
            R.drawable.park,
            "", 16
        ),
        CategoryItem(
            string(R.string.category_Estate),
            string(R.string.description_Estate),
            R.drawable.building,
            "", 31
        ),
        CategoryItem(
            string(R.string.category_Food),
            string(R.string.description_Food),
            R.drawable.food,
            "", 15
        ),
        CategoryItem(
            string(R.string.category_Market),
            string(R.string.description_Market),
            R.drawable.market,
            "", 60
        ),
        CategoryItem(
            string(R.string.category_Game),
            string(R.string.description_Game),
            R.drawable.game,
            "", 145
        ),
        CategoryItem(
            string(R.string.category_Conversation),
            string(R.string.description_Conversation),
            R.drawable.message,
            "", 21
        ),
        CategoryItem(
            string(R.string.category_Railway),
            string(R.string.description_Railway),
            R.drawable.high_speed_railway,
            "", 46
        ),
        CategoryItem(
            string(R.string.category_Knowledge),
            string(R.string.description_Knowledge),
            R.drawable.book,
            "", 43
        ),
        CategoryItem(
            string(R.string.category_Suggestion),
            string(R.string.description_Suggestion),
            R.drawable.feedback,
            "", 48
        ),
        CategoryItem(
            string(R.string.category_Announcement),
            string(R.string.description_Announcement),
            R.drawable.announcement,
            "", 17
        )
    )
}