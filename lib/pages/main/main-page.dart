import 'package:ditiezu/widgets/animated-bubble-tabbar.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class MainPage extends StatelessWidget {
  final tabData = [
    TabData(Icons.home_outlined, Colors.blue, "Home", () {}),
    TabData(Icons.category_outlined, Colors.yellow[900], "Category", () {}),
    TabData(Icons.person_outlined, Colors.green, "Me", () {},
        specialShape: true),
    TabData(Icons.notifications_outlined, Colors.red, "Notification", () {}),
    TabData(Icons.settings_outlined, Colors.purple, "Settings", () {})
  ];

  @override
  Widget build(BuildContext context) {
    SystemChrome.setEnabledSystemUIOverlays(
        [SystemUiOverlay.top, SystemUiOverlay.bottom]);
    return Scaffold(
        body: Container(color: Theme.of(context).canvasColor),
        bottomNavigationBar: AnimatedBubbleTabBar(tabData: tabData));
    // Column(children: [HeaderBar(), Expanded(child: ListView()), BottomNavBar()]));
  }
}

class HeaderBar extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Row();
  }
}
