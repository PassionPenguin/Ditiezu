import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:ditiezu/color.dart';

class MainPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(body: Container(color: AppColors.primary));
    // Column(children: [HeaderBar(), Expanded(child: ListView()), BottomNavBar()]));
  }
}

class HeaderBar extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Row();
  }
}

class BottomNavBar extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    List<BottomNavigationBarItem> items = [];
    return BottomNavigationBar(items: items);
  }
}
