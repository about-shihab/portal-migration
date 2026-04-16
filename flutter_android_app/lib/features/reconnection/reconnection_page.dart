import 'package:flutter/material.dart';

import '../../core/api_endpoints.dart';
import '../../core/app_api.dart';
import '../../widgets/api_card.dart';

class ReconnectionPage extends StatefulWidget {
  const ReconnectionPage({Key? key}) : super(key: key);

  @override
  State<ReconnectionPage> createState() => _ReconnectionPageState();
}

class _ReconnectionPageState extends State<ReconnectionPage> {
  final TextEditingController reason = TextEditingController();
  String output = '';

  Future<void> _submit() async {
    try {
      final data = await apiFor(context).postJson(ApiEndpoints.reconnectionRequest, <String, dynamic>{
        'reason': reason.text,
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
          title: 'Reconnection Request',
          child: Column(
            children: <Widget>[
              TextField(
                controller: reason,
                minLines: 2,
                maxLines: 4,
                decoration: const InputDecoration(labelText: 'Reason for reconnection'),
              ),
              const SizedBox(height: 12),
              FilledButton(onPressed: _submit, child: const Text('Submit Request')),
            ],
          ),
        ),
        ApiCard(title: 'Response', child: SelectableText(output)),
      ],
    );
  }
}
