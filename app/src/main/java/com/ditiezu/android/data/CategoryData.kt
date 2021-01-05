/*
 * ==================================================
 * =  PROJECT     地下铁的故事
 * =  MODULE      地下铁的故事.app
 * =  FILE NAME   CategoryData.kt
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

package com.ditiezu.android.data

import com.ditiezu.android.R

class CategoryItem(val name: String, val description: String, val icon: Int, val ID: Int)

val categoryList = listOf(
    CategoryItem("北京区", "地下通衢肇始燕，铁流暗涌古城垣。北畅南达东西贯，京都旧貌换新颜。", R.drawable.beijing, 7),
    CategoryItem("天津区", "跨越海河津门，连接新区古镇，通达京都江淮，品味公交百年，感受城轨脉动。", R.drawable.tianjin, 6),
    CategoryItem("上海区", "浦江两岸齐飞腾，城厢南北共繁盛；上海，因你努力而精彩。", R.drawable.shanghai, 8),
    CategoryItem("广州区", "上班、落班，地铁带你返工放工。地下铁嘅故事就喺呢度发生。", R.drawable.guangzhou, 23),
    CategoryItem("长春区", "长春区版块介绍征集中，欢迎投稿。", R.drawable.changchun, 40),
    CategoryItem("大连区", "大连地铁版块，介绍征集中，欢迎投稿。", R.drawable.dalian, 41),
    CategoryItem("武汉区", "长江汉水分三镇，楚天大地展宏图，遁地铁龙潜南北，巍巍江城遍通途。", R.drawable.wuhan, 39),
    CategoryItem("重庆区", "观两江品山城神韵，登缙云感雾都风华。居蜀道享轨道便捷，立西南叹重庆雄起。", R.drawable.chongqing, 38),
    CategoryItem("深圳区", "享罗宝快捷，乘蛇口舒适，坐龙岗欢乐，拥龙华便利，试环中悠然，爱鹏城地铁。", R.drawable.shenzhen, 24),
    CategoryItem("南京区", "虎踞龙蟠，六朝遗韵；金陵地铁，闪耀古都。", R.drawable.nanjing, 22),
    CategoryItem("成都区", "九天开出一成都，万户千门入画图。飞云流彩织锦绣，生活一脉乐悠悠。", R.drawable.chengdu, 53),
    CategoryItem("沈阳区", "沈阳地铁，乐享都市新生活！", R.drawable.shenyang, 50),
    CategoryItem("佛山区", "穿越千年美丽，相约动感佛山。", R.drawable.foshan, 56),
    CategoryItem("西安区", "一朝步入西安，一日读懂千年。", R.drawable.xian, 54),
    CategoryItem("苏州区", "山水地堑园林重镇，轨道穿越水城古今。", R.drawable.suzhou, 51),
    CategoryItem("昆明区", "昆明区板块介绍征集中，欢迎投稿。", R.drawable.kunming, 70),
    CategoryItem("杭州区", "畅行城市山林，串联钱塘古今，云汇科创新风，轨通吴越大地。", R.drawable.hangzhou, 52),
    CategoryItem("哈尔滨区", "哈尔滨地铁版块，介绍征集中，欢迎投稿。", R.drawable.harbin, 55),
    CategoryItem("郑州区", "郑州地铁讨论区", R.drawable.zhengzhou, 64),
    CategoryItem("长沙区", "长沙地铁讨论区，版块介绍征集中，欢迎投稿", R.drawable.changsha, 67),
    CategoryItem("宁波区", "宁波地铁版块，介绍征集中，欢迎投稿。", R.drawable.ningbo, 65),
    CategoryItem("无锡区", "无锡地铁版块，介绍征集中，欢迎投稿。", R.drawable.wuxi, 68),
    CategoryItem("青岛区", "青岛地铁论坛", R.drawable.qingdao, 66),
    CategoryItem("南昌区", "南昌地铁论坛", R.drawable.nanchang, 71),
    CategoryItem("福州区", "福州轨道交通讨论区", R.drawable.fuzhou, 72),
    CategoryItem("东莞区", "东莞轨道交通讨论区", R.drawable.dongguan, 75),
    CategoryItem("南宁区", "南宁轨道交通讨论区", R.drawable.nanning, 73),
    CategoryItem("合肥区", "合肥轨道交通讨论区", R.drawable.hefei, 74),
    CategoryItem("石家庄区", "石家庄轨道交通讨论区", R.drawable.shijiazhuang, 140),
    CategoryItem("贵阳区", "贵阳轨道交通讨论区", R.drawable.guiyang, 76),
    CategoryItem("厦门区", "厦门轨道交通讨论区", R.drawable.xiamen, 77),
    CategoryItem("乌鲁木齐区", "乌鲁木齐轨道交通讨论区", R.drawable.urumqi, 143),
    CategoryItem("温州区", "温州轨道交通讨论版块。", R.drawable.wenzhou, 142),
    CategoryItem("济南区", "济南轨道交通讨论版块。", R.drawable.jinan, 148),
    CategoryItem("兰州区", "兰州轨道交通讨论版块。", R.drawable.lanzhou, 78),
    CategoryItem("常州区", "常州轨道交通讨论版块。", R.drawable.changzhou, 48),
    CategoryItem("徐州区", "徐州轨道交通讨论版块。", R.drawable.xuzhou, 144),
    CategoryItem("呼和浩特", "呼和浩特轨道交通讨论版块。", R.drawable.hohhot, 151),
    CategoryItem("香港", "地下铁碰着她好比心中爱神进入梦，地下铁再遇她沉默对望车厢中。", R.drawable.hongkong, 28),
    CategoryItem("澳门", "澳门轨道交通讨论区", R.drawable.macau, 79),
    CategoryItem("台湾", "连结每一处繁华，连结每一处幸福。遇见未来，美丽正在发生。", R.drawable.taiwan, 36),
    CategoryItem("海外", "国外地铁列车、车站、路线、贴图讨论与资料分享。", R.drawable.oversea, 47),
    CategoryItem("综合区", "国内地铁轻轨建设中城市以及地铁轻轨规划中城市讨论区。", R.drawable.comprehensive, 37),
    CategoryItem("轨道收藏", "轨道交通模型、磁卡、票据等周边产品收藏交流区。", R.drawable.stamp, 33),
    CategoryItem("都市风情", "轨道交通城市风情生活展示区。", R.drawable.park, 16),
    CategoryItem("都市地产", "地铁周边房地产讨论及地铁建设动迁信息发布。", R.drawable.building, 31),
    CategoryItem("地铁美食", "全国地铁城市地铁美食发布分享区。", R.drawable.food, 15),
    CategoryItem("交易市场", "族友专属交易区，轨道交通周边产品及其他闲置物品。（商家绕道）", R.drawable.market, 60),
    CategoryItem("轨交游戏", "城市及轨道交通类游戏讨论区", R.drawable.game, 145),
    CategoryItem("站前广场", "闲谈杂侃版块，严禁灌水。", R.drawable.message, 21),
    CategoryItem("城际铁路", "城际高速铁路路线、特色车站、国内外交通枢纽及转乘车站、城际铁路选线、城际铁路列车站。", R.drawable.high_speed_railway, 46),
    CategoryItem("轨道知识", "地铁和轨道交通相关技术、知识发表讨论区。", R.drawable.book, 43),
    CategoryItem("意见建议", "意见建议、反馈中心", R.drawable.feedback, 48),
    CategoryItem("站务公告", "站务公告中心", R.drawable.announcement, 17)
)