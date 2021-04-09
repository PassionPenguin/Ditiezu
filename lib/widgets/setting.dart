import 'package:ditiezu/pages/main/pages/settings-page.dart';
import 'package:ditiezu/utils/shared-preferences.dart';
import 'package:ditiezu/widgets/dialog.dart';
import 'package:ditiezu/widgets/switch.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:tuple/tuple.dart';

ListView settingsSetBuilder(BuildContext context, List<SettingsSet> sets, ValueSetter<VoidCallback> setState) {
  return ListView.builder(
      physics: BouncingScrollPhysics(),
      itemBuilder: (context, index) {
        List<SettingRow> settingRows = sets[index].settingRows;
        return Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          index != 0 ? Column(children: [SizedBox(height: 16), Divider(), SizedBox(height: 16)]) : SizedBox(),
          Text(sets[index].setName, style: TextStyle(fontSize: 15, fontWeight: FontWeight.w600, color: Colors.grey[500])),
          settingRowBuilder(context, settingRows, setState)
        ]);
      },
      itemCount: sets.length);
}

ListView settingRowBuilder(BuildContext context, List<SettingRow> settings, ValueSetter<VoidCallback> setState) {
  return ListView.builder(
      shrinkWrap: true,
      physics: NeverScrollableScrollPhysics(),
      itemBuilder: (context, index) {
        SettingRow setting = settings[index];
        Tuple2<Widget, VoidCallback> buildResult = buildSettingControl(context, setting, setState);

        return Container(
            child: GestureDetector(
                child: Row(children: [
                  Expanded(
                      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                    Text(setting.settingName, style: TextStyle(fontSize: 18, color: Colors.black), maxLines: 1, overflow: TextOverflow.ellipsis),
                    SizedBox(height: 1),
                    Text(setting.settingDescription, style: TextStyle(fontSize: 13.8, color: Colors.grey[700]), maxLines: 1, overflow: TextOverflow.ellipsis)
                  ])),
                  buildResult.item1
                ]),
                onTap: buildResult.item2),
            padding: EdgeInsets.symmetric(vertical: 12));
      },
      itemCount: settings.length);
}

Tuple2<Widget, VoidCallback> buildSettingControl(BuildContext context, SettingRow setting, ValueSetter<VoidCallback> setState) {
  SharedPreferences sp = Application.sp;
  Widget control;
  VoidCallback callback;

  switch (setting.settingType) {
    case SettingType.toggle:
      control = SwitchWidget(
          value: sp.getBool(setting.sharedPreferenceName) == true,
          onChanged: (value) {
            setState(() {
              setting.callback(value);
            });
          });
      break;

    case SettingType.link:
      control = Icon(Icons.chevron_right);
      callback = () {
        setting.callback(null);
      };
      break;

    case SettingType.textInput:
      control = Icon(Icons.chevron_right);
      callback = () async {
        TextEditingController controller = TextEditingController(text: sp.getString(setting.sharedPreferenceName));
        await displayTextInputDialog(context, controller, setting.settingName, (result) {
          sp.setString(setting.sharedPreferenceName, result);
        });
      };
      break;
  }
  return new Tuple2(control, callback);
}
