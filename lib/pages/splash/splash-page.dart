import 'dart:async';

import 'package:ditiezu/utils/shared-preferences.dart';
import 'package:fluro/fluro.dart';
import 'package:flutter/material.dart';

import 'package:ditiezu/main.dart';
import 'package:flutter/rendering.dart';
import 'package:shared_preferences/shared_preferences.dart';

class SplashPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => SplashPageState();
}

class SplashPageState extends State<StatefulWidget> with SingleTickerProviderStateMixin {
  AnimationController _controller;
  Animation<double> _animation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: const Duration(seconds: 2),
      vsync: this,
    )..animateTo(1);
    _animation = CurvedAnimation(
      parent: _controller,
      curve: Curves.easeIn,
    );
    initConfiguration(context);
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    Size size = MediaQuery.of(context).size;
    return Scaffold(
        body: FadeTransition(
            opacity: _animation,
            child: Stack(children: [Positioned(top: size.height / 3, left: size.width / 2 - 128, child: Image.asset("assets/TextNBrand.png", width: 256))])));
  }
}

void initConfiguration(BuildContext context) async {
  SharedPreferences sp = Application.sp;
  if (sp.getString("browserUserAgent") == null)
    sp.setString("browserUserAgent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.0 (KHTML, like Gecko) Chrome/91.0 Safari/537.0");

  debugPaintSizeEnabled = sp.getBool("debugPaintSizeEnabled") == true;
  debugPaintBaselinesEnabled = sp.getBool("debugPaintBaselinesEnabled") == true;
  debugPaintLayerBordersEnabled = sp.getBool("debugPaintLayerBordersEnabled") == true;
  debugPaintPointersEnabled = sp.getBool("debugPaintPointersEnabled") == true;
  debugRepaintRainbowEnabled = sp.getBool("debugPaintPointersEnabled") == true;
  debugRepaintTextRainbowEnabled = sp.getBool("debugRepaintTextRainbowEnabled") == true;
  debugCheckElevationsEnabled = sp.getBool("debugCheckElevationsEnabled") == true;
  debugPrintMarkNeedsLayoutStacks = sp.getBool("debugPrintMarkNeedsLayoutStacks") == true;
  debugPrintMarkNeedsPaintStacks = sp.getBool("debugPrintMarkNeedsPaintStacks") == true;
  debugPrintLayouts = sp.getBool("debugPrintLayouts") == true;
  debugCheckIntrinsicSizes = sp.getBool("debugCheckIntrinsicSizes") == true;
  debugProfileLayoutsEnabled = sp.getBool("debugProfileLayoutsEnabled") == true;
  debugDisableClipLayers = sp.getBool("debugDisableClipLayers") == true;
  debugDisablePhysicalShapeLayers = sp.getBool("debugDisablePhysicalShapeLayers") == true;
  debugDisableOpacityLayers = sp.getBool("debugDisableOpacityLayers") == true;

  Timer(const Duration(seconds: 2), () {
    Ditiezu.router.navigateTo(context, "/main", transition: TransitionType.fadeIn, transitionDuration: Duration(milliseconds: 750));
  });
}
