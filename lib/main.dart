import 'package:ditiezu/pages/main/main-page.dart';
import 'package:ditiezu/pages/splash/splash-page.dart';
import 'package:fluro/fluro.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(Ditiezu());
}

class Ditiezu extends StatelessWidget {
  static FluroRouter router = FluroRouter();

  @override
  Widget build(BuildContext context) {
    var mainPageHandler = Handler(
        handlerFunc: (BuildContext context, Map<String, dynamic> params) {
      return MainPage();
    });
    router.define("/main", handler: mainPageHandler);

    SystemChrome.setEnabledSystemUIOverlays([]);
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Ditiezu',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: SplashPage(),
    );
  }
}
