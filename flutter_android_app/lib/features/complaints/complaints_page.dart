import 'package:flutter/material.dart';

import '../../core/api_endpoints.dart';
import '../../core/app_api.dart';
import '../../widgets/api_card.dart';

class ComplaintsPage extends StatefulWidget {
  const ComplaintsPage({Key? key}) : super(key: key);

  @override
  State<ComplaintsPage> createState() => _ComplaintsPageState();
}

class _ComplaintsPageState extends State<ComplaintsPage> {
  final TextEditingController message = TextEditingController();
  String output = '';

  Future<void> _loadCauses() async {
    try {
      final data = await apiFor(context).getJson(ApiEndpoints.complaintCauses);
      setState(() => output = data.toString());
    } catch (e) {
      setState(() => output = e.toString());
    }
  }

  Future<void> _create() async {
    try {
      final data = await apiFor(context).postJson(ApiEndpoints.createComplaint, <String, dynamic>{
        'complain': message.text,
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
          title: 'Complaint Center',
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              Wrap(
                spacing: 8,
                runSpacing: 8,
                children: <Widget>[
                  OutlinedButton(onPressed: _loadCauses, child: const Text('Load Causes')),
                  OutlinedButton(
                    onPressed: () async {
                      final data = await apiFor(context).getJson(ApiEndpoints.complaintTickets);
                      setState(() => output = data.toString());
                    },
                    child: const Text('Ticket Status'),
                  ),
                ],
              ),
              const SizedBox(height: 10),
              TextField(
                controller: message,
                minLines: 3,
                maxLines: 5,
                decoration: const InputDecoration(labelText: 'Complaint description'),
              ),
              const SizedBox(height: 10),
              ElevatedButton(onPressed: _create, child: const Text('Submit Complaint')),
            ],
          ),
        ),
        ApiCard(title: 'Response', child: SelectableText(output)),
      ],
    );
  }
}
