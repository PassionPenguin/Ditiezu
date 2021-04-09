import 'package:ditiezu/utils/utils.dart';
import 'package:flutter/material.dart';

class AccountPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => AccountPageState();
}

class AccountPageState extends State<AccountPage> {
  CheckingState state = CheckingState.pending;

  @override
  Widget build(BuildContext context) {
    switch (state) {
      case CheckingState.finished_true: // Already Login, show Account Page
        return AccountPageContent();
      case CheckingState.finished_false: // Not Login, show login banner
        return Container(color: Colors.green);
      default: // Checking Login Status, show skeleton
        return AccountPageContent(isSkeleton: true);
    }
  }
}

class AccountPageContent extends StatefulWidget {
  final bool isSkeleton;

  AccountPageContent({this.isSkeleton = false});

  @override
  State<StatefulWidget> createState() => AccountPageContentState();
}

class AccountPageContentState extends State<AccountPageContent> {
  @override
  Widget build(BuildContext context) {
    return Container(padding: EdgeInsets.only(top: 72, left: 32, right: 32, bottom: 32), child: Column(children: [ProfileCard(widget.isSkeleton, null, "")]));
  }
}

class ProfileCard extends StatefulWidget {
  final bool isSkeleton;
  final ImageProvider avatar;
  final String userName;

  const ProfileCard(this.isSkeleton, this.avatar, this.userName);

  @override
  State<StatefulWidget> createState() => ProfileCardState();
}

class ProfileCardState extends State<ProfileCard> {
  @override
  Widget build(BuildContext context) {
    return Container(
        child: Row(children: [
          Container(
              decoration: BoxDecoration(color: Colors.grey[200], borderRadius: BorderRadius.circular(48)),
              width: 96,
              height: 96,
              child: widget.isSkeleton ? SizedBox() : Image(image: widget.avatar)),
          SizedBox(width: 24),
          Expanded(
              child: Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
            SizedBox(height: 12),
            Container(decoration: BoxDecoration(color: Colors.grey[200]), height: 36, child: Text(widget.userName)),
            SizedBox(height: 12),
            Container(decoration: BoxDecoration(color: Colors.grey[200]), height: 20, child: widget.isSkeleton ? SizedBox() : UserLevelWidget()),
            Expanded(child: SizedBox())
          ]))
        ]),
        height: 96);
  }
}

class UserLevelWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Container();
  }
}
