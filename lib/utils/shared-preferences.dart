import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

class Application {
  static SharedPreferences sp;
  static BuildContext ctx;

  static getInstance() async {
    sp = await SharedPreferences.getInstance();
  }

  static buildContext(BuildContext context) async {
    ctx = context;
  }
}
