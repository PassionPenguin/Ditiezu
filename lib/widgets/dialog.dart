import 'package:flutter/material.dart';

Future<void> displayTextInputDialog(BuildContext context, TextEditingController _textFieldController, String dialogName, ValueChanged callback) async {
  return showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: Text(dialogName),
          content: Container(
              child: Column(crossAxisAlignment: CrossAxisAlignment.end, children: [
                TextField(
                  controller: _textFieldController,
                  decoration: InputDecoration(hintText: dialogName),
                ),
                SizedBox(height: 12),
                MaterialButton(
                    color: Colors.grey[100],
                    height: 36,
                    onPressed: () {
                      Navigator.pop(context);
                      callback(_textFieldController.text);
                    },
                    child: Text("确认"))
              ]),
              height: 108),
        );
      });
}
