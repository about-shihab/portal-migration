import 'package:flutter/material.dart';

import '../../core/api_endpoints.dart';
import '../../core/app_api.dart';
import '../../widgets/api_card.dart';

class ProfilePage extends StatefulWidget {
  const ProfilePage({Key? key}) : super(key: key);

  @override
  State<ProfilePage> createState() => _ProfilePageState();
}

class _ProfilePageState extends State<ProfilePage> {
  String output = '';
  final TextEditingController oldPassword = TextEditingController();
  final TextEditingController newPassword = TextEditingController();

  Future<void> _info() async {
    try {
      final data = await apiFor(context).getJson(ApiEndpoints.userInfo);
      setState(() => output = data.toString());
    } catch (e) {
      setState(() => output = e.toString());
    }
  }

  Future<void> _changePassword() async {
    try {
      final data = await apiFor(context).postJson(ApiEndpoints.changePassword, <String, dynamic>{
        'oldPassword': oldPassword.text,
        'newPassword': newPassword.text,
      });
      setState(() => output = data.toString());
    } catch (e) {
      setState(() => output = e.toString());
    }
  }

  @override
  Widget build(BuildContext context) {
    return ListView(
      children: <Widget>[
        ApiCard(
          title: 'Profile & Security',
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              OutlinedButton(onPressed: _info, child: const Text('Load Profile')),
              const SizedBox(height: 12),
              TextField(controller: oldPassword, decoration: const InputDecoration(labelText: 'Old password')),
              TextField(controller: newPassword, decoration: const InputDecoration(labelText: 'New password')),
              const SizedBox(height: 12),
              ElevatedButton(onPressed: _changePassword, child: const Text('Change Password')),
            ],
          ),
        ),
        ApiCard(title: 'Response', child: SelectableText(output)),
      ],
    );
  }
}
