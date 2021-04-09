import 'dart:math';

import 'package:ditiezu/utils/utils.dart';
import 'package:flutter/material.dart';

class Skeleton extends StatefulWidget {
  final double width;
  final double height;
  final Shape shape;

  const Skeleton({Key key, this.width, this.height, this.shape}) : super(key: key);

  @override
  State<Skeleton> createState() => SkeletonState();
}

class SkeletonState extends State<Skeleton> with SingleTickerProviderStateMixin {
  AnimationController _controller;
  Animation gradientPosition;
  VoidCallback animationListener;

  @override
  void initState() {
    _controller = AnimationController(duration: Duration(milliseconds: 1500), vsync: this)..repeat();
    animationListener = () {
      if (this.mounted) setState(() {});
    };
    gradientPosition = Tween<double>(
      begin: -3,
      end: 10,
    ).animate(
      CurvedAnimation(parent: _controller, curve: Curves.linear),
    )..addListener(animationListener);
    super.initState();
  }

  @override
  void dispose() {
    _controller.dispose();
    gradientPosition.removeListener(animationListener);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: EdgeInsets.symmetric(vertical: 8),
      width: widget.width,
      height: widget.height,
      decoration: BoxDecoration(
          borderRadius: widget.shape == Shape.circle ? BorderRadius.circular(max(widget.width, widget.height) / 2) : BorderRadius.zero,
          gradient: LinearGradient(
              begin: Alignment(gradientPosition.value, 0), end: Alignment(-1, 0), colors: [Colors.grey[100], Colors.grey[200], Colors.grey[100]])),
    );
  }
}
