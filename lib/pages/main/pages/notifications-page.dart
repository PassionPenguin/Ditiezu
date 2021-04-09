import 'dart:async';

import 'package:ditiezu/utils/utils.dart';
import 'package:ditiezu/widgets/skeleton.dart';
import 'package:flutter/material.dart';

class NotificationsPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => NotificationsPageState();
}

class NotificationsPageState extends State<NotificationsPage> {
  CheckingState state = CheckingState.pending;

  @override
  void initState() {
    Timer(Duration(seconds: 1), () {
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
          Text("全部消息", style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
          SizedBox(height: 8),
          Expanded(child: NotificationsListWidget(isSkeleton: state != CheckingState.finished_true))
        ]));
  }
}

class NotificationsListWidget extends StatelessWidget {
  final bool isSkeleton;
  final List<Notification> items = [];

  NotificationsListWidget({Key key, this.isSkeleton}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    Size size = MediaQuery.of(context).size;
    if (isSkeleton)
      return ListView.builder(
          itemBuilder: (context, index) {
            if (index == 4) return SizedBox(height: 16);
            return Skeleton(height: 16, width: size.width - 32);
          },
          itemCount: 9);

    return ListView.builder(
        itemBuilder: (context, index) {
          return Column();
        },
        shrinkWrap: true,
        physics: BouncingScrollPhysics(),
        itemCount: items.length);
  }
}
