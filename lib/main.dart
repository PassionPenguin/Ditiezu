import 'package:ditiezu/pages/main/main-page.dart';
import 'package:ditiezu/pages/splash/splash-page.dart';
import 'package:ditiezu/utils/shared-preferences.dart';
import 'package:fluro/fluro.dart';
import 'package:flutter/material.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Application.getInstance();
  runApp(Ditiezu());
}

class Ditiezu extends StatelessWidget {
  static FluroRouter router = FluroRouter();

  @override
  Widget build(BuildContext context) {
    var mainPageHandler = Handler(handlerFunc: (BuildContext context, Map<String, dynamic> params) {
      return MainPage();
    });
    router.define("/main", handler: mainPageHandler);

    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Ditiezu',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        primaryColor: Colors.blue,
        fontFamily: "Source Han Sans"
      ),
      home: SplashPage(),
    );
  }
}
