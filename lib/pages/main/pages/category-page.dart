import 'dart:async';

import 'package:ditiezu/data/category-data.dart';
import 'package:ditiezu/utils/utils.dart';
import 'package:ditiezu/widgets/skeleton.dart';
import 'package:flutter/material.dart';

class CategoryPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => CategoryPageState();
}

class CategoryPageState extends State<CategoryPage> {
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
          Text("全部板块", style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
          SizedBox(height: 8),
          Expanded(child: CategoryListWidget(isSkeleton: state != CheckingState.finished_true))
        ]));
  }
}

class CategoryListWidget extends StatelessWidget {
  final List<CategoryItem> items = categoryList;
  final bool isSkeleton;

  CategoryListWidget({Key key, this.isSkeleton}) : super(key: key);

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

    return GridView.count(
      shrinkWrap: true,
      physics: BouncingScrollPhysics(),
      children: getCategoryWidgets(context),
      crossAxisCount: 3,
      crossAxisSpacing: 8,
      mainAxisSpacing: 0,
      childAspectRatio: ((size.width - 32) / 3 - 24) / 36,
    );
  }

  List<Widget> getCategoryWidgets(BuildContext context) {
    List<Widget> list = [];
    items.asMap().forEach((key, value) {
      list.add(MaterialButton(
          child: Text(value.categoryName, style: TextStyle(fontSize: 16), textAlign: TextAlign.center),
          onPressed: () {
            print("Clicked " + key.toString());
          }));
    });
    return list;
  }
}
