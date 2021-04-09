import 'dart:async';

import 'package:ditiezu/utils/utils.dart';
import 'package:ditiezu/utils/shared-preferences.dart';
import 'package:ditiezu/widgets/dialog.dart';
import 'package:ditiezu/widgets/setting.dart';
import 'package:ditiezu/widgets/skeleton.dart';
import 'package:ditiezu/widgets/switch.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:shared_preferences/shared_preferences.dart';

class SettingsPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => SettingsPageState();
}

class SettingsPageState extends State<SettingsPage> {
  CheckingState state = CheckingState.pending;

  @override
  void initState() {
    Timer(Duration(seconds: 3), () {
      if (this.mounted)
        setState(() {
          state = CheckingState.finished_true;
        });
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
        color: Theme.of(context).canvasColor,
        padding: EdgeInsets.only(top: 54, left: 16, right: 16),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Text("偏好设置", style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
          SizedBox(height: 8),
          Expanded(child: SettingsListWidget(isSkeleton: state != CheckingState.finished_true, context: context))
        ]));
  }
}

class SettingsListWidget extends StatefulWidget {
  final bool isSkeleton;
  final BuildContext context;

  SettingsListWidget({Key key, this.isSkeleton, this.context});

  @override
  State<StatefulWidget> createState() => SettingsListWidgetState();
}

class SettingsListWidgetState extends State<SettingsListWidget> {
  BuildContext context;
  List<SettingsSet> sets;

  SettingsListWidgetState();

  @override
  void initState() {
    context = widget.context;
    sets = getSettings(context);
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    Size size = MediaQuery.of(context).size;
    if (widget.isSkeleton)
      return ListView.builder(
          itemBuilder: (context, index) {
            if (index == 4) return SizedBox(height: 16);
            return Skeleton(height: 16, width: size.width - 32);
          },
          itemCount: 9);
    return settingsSetBuilder(context, sets, setState);
  }
}

class SettingsSet {
  final String setName;
  final List<SettingRow> settingRows;

  SettingsSet({this.setName, this.settingRows});
}

class SettingRow {
  final String settingName;
  final String settingDescription;
  final String sharedPreferenceName;
  final SettingType settingType;
  final ValueChanged callback;

  SettingRow(
      {@required this.settingName,
      this.settingDescription = "",
      @required this.sharedPreferenceName,
      this.settingType = SettingType.toggle,
      @required this.callback});
}

enum SettingType { link, toggle, textInput }

List<SettingsSet> getSettings(BuildContext context) {
  List<SettingsSet> sets = [];
  SharedPreferences sp = Application.sp;
  sets.add(getFetchingSetting(context, sp));
  sets.add(getRenderingSetting(context, sp));
  return sets;
}

SettingsSet getRenderingSetting(BuildContext context, SharedPreferences sp) {
  List<SettingRow> renderingSettingRows = [
    SettingRow(
        settingName: "显示布局尺寸",
        settingDescription: "显示 RenderBox 尺寸",
        sharedPreferenceName: "debugPaintSizeEnabled",
        settingType: SettingType.toggle,
        callback: (value) {
          sp.setBool("debugPaintSizeEnabled", value);
          debugPaintSizeEnabled = value;
        }),
    SettingRow(
        settingName: "显示布局基线",
        settingDescription: "显示 RenderBox 的基线",
        sharedPreferenceName: "debugPaintBaselinesEnabled",
        settingType: SettingType.toggle,
        callback: (value) {
          sp.setBool("debugPaintBaselinesEnabled", value);
          debugPaintBaselinesEnabled = value;
        }),
    SettingRow(
        settingName: "显示布局边界",
        settingDescription: "显示 RenderBox 的边界、边距等",
        sharedPreferenceName: "debugPaintLayerBordersEnabled",
        settingType: SettingType.toggle,
        callback: (value) {
          sp.setBool("debugPaintLayerBordersEnabled", value);
          debugPaintLayerBordersEnabled = value;
        }),
    SettingRow(
        settingName: "显示点按的监听区块",
        settingDescription: "显示 RenderPointerListener 的区块",
        sharedPreferenceName: "debugPaintPointersEnabled",
        settingType: SettingType.toggle,
        callback: (value) {
          sp.setBool("debugPaintPointersEnabled", value);
          debugPaintPointersEnabled = value;
        }),
    SettingRow(
        settingName: "显示更新的布局",
        settingDescription: "显示更新的 RenderBox",
        sharedPreferenceName: "debugRepaintRainbowEnabled",
        settingType: SettingType.toggle,
        callback: (value) {
          sp.setBool("debugRepaintRainbowEnabled", value);
          debugRepaintRainbowEnabled = value;
        }),
    SettingRow(
        settingName: "显示更新的文字",
        settingDescription: "显示更新的文字",
        sharedPreferenceName: "debugRepaintTextRainbowEnabled",
        settingType: SettingType.toggle,
        callback: (value) {
          sp.setBool("debugRepaintTextRainbowEnabled", value);
          debugRepaintTextRainbowEnabled = value;
        }),
    SettingRow(
        settingName: "显示布局层级",
        settingDescription: "显示 RenderBox 的层级",
        sharedPreferenceName: "debugCheckElevationsEnabled",
        settingType: SettingType.toggle,
        callback: (value) {
          sp.setBool("debugCheckElevationsEnabled", value);
          debugCheckElevationsEnabled = value;
        }),
    SettingRow(
        settingName: "显示重新布局的渲染对象",
        settingDescription: "显示要求重新布局的 RenderObject",
        sharedPreferenceName: "debugPrintMarkNeedsLayoutStacks",
        settingType: SettingType.toggle,
        callback: (value) {
          sp.setBool("debugPrintMarkNeedsLayoutStacks", value);
          debugPrintMarkNeedsLayoutStacks = value;
        }),
    SettingRow(
        settingName: "显示重新绘制的渲染对象",
        settingDescription: "显示要求重新绘制的 RenderObject",
        sharedPreferenceName: "debugPrintMarkNeedsPaintStacks",
        settingType: SettingType.toggle,
        callback: (value) {
          sp.setBool("debugPrintMarkNeedsPaintStacks", value);
          debugPrintMarkNeedsPaintStacks = value;
        }),
    SettingRow(
        settingName: "打印需要改变的渲染对象",
        settingDescription: "打印需要改变的 RenderObject",
        sharedPreferenceName: "debugPrintLayouts",
        settingType: SettingType.toggle,
        callback: (value) {
          sp.setBool("debugPrintLayouts", value);
          debugPrintLayouts = value;
        }),
    SettingRow(
        settingName: "检查布局原始尺寸",
        settingDescription: "在布局时检查 RenderBox 的尺寸",
        sharedPreferenceName: "debugCheckIntrinsicSizes",
        settingType: SettingType.toggle,
        callback: (value) {
          sp.setBool("debugCheckIntrinsicSizes", value);
          debugCheckIntrinsicSizes = value;
        }),
    SettingRow(
        settingName: "向布局监听时间线事件",
        settingDescription: "向 RenderBox 监听 developer.Timeline 事件",
        sharedPreferenceName: "debugProfileLayoutsEnabled",
        settingType: SettingType.toggle,
        callback: (value) {
          sp.setBool("debugProfileLayoutsEnabled", value);
          debugProfileLayoutsEnabled = value;
        }),
    SettingRow(
        settingName: "禁用剪切层",
        settingDescription: "禁用剪切效果",
        sharedPreferenceName: "debugDisableClipLayers",
        settingType: SettingType.toggle,
        callback: (value) {
          sp.setBool("debugDisableClipLayers", value);
          debugDisableClipLayers = value;
        }),
    SettingRow(
        settingName: "禁用物理形状图层",
        settingDescription: "禁用阴影、提升等效果",
        sharedPreferenceName: "debugDisablePhysicalShapeLayers",
        settingType: SettingType.toggle,
        callback: (value) {
          sp.setBool("debugDisablePhysicalShapeLayers", value);
          debugDisablePhysicalShapeLayers = value;
        }),
    SettingRow(
        settingName: "禁用透明图层",
        settingDescription: "禁用透明效果",
        sharedPreferenceName: "debugDisableOpacityLayers",
        settingType: SettingType.toggle,
        callback: (value) {
          sp.setBool("debugDisableOpacityLayers", value);
          debugDisableOpacityLayers = value;
        })
  ];
  return SettingsSet(setName: "绘图", settingRows: renderingSettingRows);
}

SettingsSet getFetchingSetting(BuildContext context, SharedPreferences sp) {
  List<SettingRow> fetchingSettingRow = [
    SettingRow(
        settingName: "浏览器 User Agent",
        settingDescription: sp.getString("browserUserAgent"),
        sharedPreferenceName: "browserUserAgent",
        settingType: SettingType.textInput,
        callback: (_) {}),
  ];
  return SettingsSet(setName: "数据爬取", settingRows: fetchingSettingRow);
}
