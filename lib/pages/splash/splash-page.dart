import 'dart:async';

import 'package:fluro/fluro.dart';
import 'package:flutter/material.dart';

import 'package:ditiezu/main.dart';

class SplashPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => SplashPageState();
}

class SplashPageState extends State<StatefulWidget>
    with SingleTickerProviderStateMixin {
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
    Timer(const Duration(seconds: 2), () {
      Ditiezu.router.navigateTo(context, "/main",
          transition: TransitionType.fadeIn,
          transitionDuration: Duration(milliseconds: 750));
    });
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
            child: Stack(children: [
              Positioned(
                  top: size.height / 3,
                  left: size.width / 2 - 128,
                  child: Image.asset("assets/TextNBrand.png", width: 256))
            ])));
  }
}
