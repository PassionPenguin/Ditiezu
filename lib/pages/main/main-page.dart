import 'package:ditiezu/pages/main/pages/account-page.dart';
import 'package:ditiezu/pages/main/pages/category-page.dart';
import 'package:ditiezu/pages/main/pages/home-page.dart';
import 'package:ditiezu/pages/main/pages/notifications-page.dart';
import 'package:ditiezu/pages/main/pages/settings-page.dart';
import 'package:ditiezu/widgets/animated-bubble-tabbar.dart';
import 'package:flutter/material.dart';

class MainPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => MainPageState();
}

class MainPageState extends State<MainPage> {
  PageController pageViewController = PageController(initialPage: 0);
  AnimatedBubbleTabBar animatedBubbleTabBar;
  List<TabData> tabData = [];
  bool animating = false;

  @override
  void initState() {
    tabData = [
      TabData(Icons.home_outlined, Colors.blue, "Home", () {
        animating = false;
        animateToPage(0);
      }),
      TabData(Icons.category_outlined, Colors.yellow[900], "Category", () {
        animating = false;
        animateToPage(1);
      }),
      TabData(Icons.person_outlined, Colors.green, "Me", () {
        animating = false;
        animateToPage(2);
      }, specialShape: true),
      TabData(Icons.notifications_outlined, Colors.red, "Notification", () {
        animating = false;
        animateToPage(3);
      }),
      TabData(Icons.settings_outlined, Colors.purple, "Settings", () {
        animating = false;
        animateToPage(4);
      })
    ];
    animatedBubbleTabBar = AnimatedBubbleTabBar(tabData: tabData);
    super.initState();
  }

  void animateToPage(int index) {
    animatedBubbleTabBar.notifyPositionUpdate(index);
    animating = true;
    pageViewController
        .animateToPage(index, duration: Duration(milliseconds: (pageViewController.page - index).abs().toInt() * 150), curve: Curves.easeIn)
        .whenComplete(() {
      animating = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: PageView.builder(
            scrollDirection: Axis.horizontal,
            controller: pageViewController,
            itemBuilder: (context, index) {
              return [HomePage(), CategoryPage(), AccountPage(), NotificationsPage(), SettingsPage()][index];
            },
            itemCount: 5,
            onPageChanged: (int page) {
              if (!animating) animatedBubbleTabBar.notifyPositionUpdate(page);
            }),
        bottomNavigationBar: animatedBubbleTabBar);
  }
}

class HeaderBar extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Row();
  }
}
