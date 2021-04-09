import 'dart:async';

import 'package:ditiezu/utils/utils.dart';
import 'package:ditiezu/widgets/post-widget.dart';
import 'package:ditiezu/widgets/skeleton.dart';
import 'package:flutter/material.dart';

class HomePage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => HomePageState();
}

class HomePageState extends State<HomePage> {
  CheckingState state = CheckingState.pending;

  @override
  void initState() {
    Timer(Duration(seconds: 3), () {
      if (this.mounted)
        setState(() {
          state = CheckingState.finished_true;
        });
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
        color: Theme.of(context).canvasColor,
        padding: EdgeInsets.only(top: 54, left: 16, right: 16),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Text("Ditiezu X", style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
          SizedBox(height: 8),
          Expanded(child: HomePageContent(state: state))
        ]));
  }
}

class HomePageContent extends StatelessWidget {
  final CheckingState state;

  const HomePageContent({Key key, this.state}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    switch (state) {
      case CheckingState.finished_true:
        return HomePageContent();
      case CheckingState.finished_false:
        return Container(color: Colors.green);
      default:
        return HomePageListWidget(isSkeleton: state == CheckingState.pending);
    }
  }
}

class HomePageListWidget extends StatefulWidget {
  final bool isSkeleton;

  HomePageListWidget({this.isSkeleton = false});

  @override
  State<StatefulWidget> createState() => HomePageListWidgetState();
}

class HomePageListWidgetState extends State<HomePageListWidget> {
  @override
  Widget build(BuildContext context) {
    Size size = MediaQuery.of(context).size;
    if (widget.isSkeleton)
      return ListView.builder(
          itemBuilder: (context, index) {
            if (index == 4) return SizedBox(height: 16);
            return Skeleton(height: 16, width: size.width - 32);
          },
          itemCount: 9);

    return Container(
        padding: EdgeInsets.only(top: 72, left: 32, right: 32, bottom: 32),
        color: Theme.of(context).canvasColor,
        child: ListView.builder(
            itemBuilder: (index, context) {
              return PostWidget();
            },
            itemCount: 20));
  }
}
