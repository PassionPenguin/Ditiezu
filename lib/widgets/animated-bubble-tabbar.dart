import 'package:flutter/material.dart';

class AnimatedBubbleTabBar extends StatefulWidget {
  final List<TabData> tabData;

  AnimatedBubbleTabBar({this.tabData = const [], Key key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => AnimatedBubbleTabBarState();
}

class TabData {
  final IconData tabIcon;
  final Color tabColor;
  final String tabTitle;
  final Function tabCallback;
  final bool specialShape;

  int tabIndex = 0;

  TabData(this.tabIcon, this.tabColor, this.tabTitle, this.tabCallback, {this.specialShape = false, active = false});

  void updateTabIndex(index) {
    tabIndex = index;
  }
}

class AnimatedBubbleTabBarState extends State<AnimatedBubbleTabBar> {
  List<BubbleTabItem> tabChildren = [];
  int currentIndex = 0;

  void updateIndex(int index) {
    setState(() {
      tabChildren.forEach((e) {
        e.notifyUpdate(index);
      }); // Notify every TabBarItem to update `currentIndex` value
      currentIndex = index;
      tabChildren[index].tabData.tabCallback();
    });
  }

  @override
  void initState() {
    widget.tabData.asMap().forEach((index, element) {
      element.updateTabIndex(index);
      tabChildren.add(BubbleTabItem(element, 0, updateIndex));
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
        color: Colors.white,
        height: 54,
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: tabChildren,
        ));
  }
}

class BubbleTabItem extends StatefulWidget {
  final TabData tabData;
  final int currentIndex;
  final Function updateIndex;
  final BubbleTabItemState state = BubbleTabItemState();

  BubbleTabItem(this.tabData, this.currentIndex, this.updateIndex);

  @override
  State<StatefulWidget> createState() => state;

  void notifyUpdate(int index) => state.updateIndex(index);
}

class BubbleTabItemState extends State<BubbleTabItem> with TickerProviderStateMixin {
  int currentIndex = 0;

  void updateIndex(int index) => setState(() {
        this.currentIndex = index;
      });

  @override
  void initState() {
    this.currentIndex = widget.currentIndex;
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedSize(
        vsync: this,
        duration: Duration(seconds: 1),
        curve: Curves.fastOutSlowIn,
        child: InkWell(
            child: BubbleShapeBuilder(widget.tabData, currentIndex),
            onTap: () {
              onTapHandler();
            }));
  }

  void onTapHandler() => setState(() {
        currentIndex = widget.tabData.tabIndex;
        widget.updateIndex(widget.tabData.tabIndex);
      });

  bool getActive() => currentIndex == widget.tabData.tabIndex;
}

class BubbleShapeBuilder extends StatelessWidget {
  final TabData tabData;
  final int currentIndex;

  BubbleShapeBuilder(this.tabData, this.currentIndex);

  @override
  Widget build(BuildContext context) {
    if (tabData.specialShape)
      return Container(
          width: 48,
          height: 48,
          padding: EdgeInsets.all(4),
          decoration: BoxDecoration(borderRadius: BorderRadius.circular(24), color: getActive() ? tabData.tabColor : Colors.white),
          child: Icon(tabData.tabIcon, color: getActive() ? Colors.white : Colors.blueGrey[300]));
    else if (getActive())
      return Container(
          padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
          decoration: BoxDecoration(borderRadius: BorderRadius.circular(24), color: tabData.tabColor.withOpacity(.2)),
          child: Row(children: [
            Icon(tabData.tabIcon, color: tabData.tabColor, size: 24),
            SizedBox(width: 8),
            Text(tabData.tabTitle, style: TextStyle(color: tabData.tabColor, fontWeight: FontWeight.bold, fontSize: 17))
          ]));
    else
      return Container(width: 48, height: 48, child: Icon(tabData.tabIcon, color: Colors.blueGrey[300], size: 24));
  }

  bool getActive() => currentIndex == tabData.tabIndex;
}
