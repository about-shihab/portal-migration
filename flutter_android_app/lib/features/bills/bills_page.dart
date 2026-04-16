import 'package:flutter/material.dart';

import '../../core/api_endpoints.dart';
import '../../core/app_api.dart';
import '../../widgets/api_card.dart';

class BillsPage extends StatefulWidget {
  const BillsPage({Key? key}) : super(key: key);

  @override
  State<BillsPage> createState() => _BillsPageState();
}

class _BillsPageState extends State<BillsPage> {
  String output = 'Tap a button to load billing data.';

  Future<void> _load(String path) async {
    setState(() => output = 'Loading...');
    try {
      final data = await apiFor(context).getJson(path);
      setState(() => output = data.toString());
    } catch (e) {
      setState(() => output = e.toString());
    }
  }

  @override
  Widget build(BuildContext context) {
    return ListView(
      padding: const EdgeInsets.only(bottom: 16),
      children: <Widget>[
        ApiCard(
          title: 'Billing Dashboard',
          child: Wrap(
            spacing: 8,
            runSpacing: 8,
            children: <Widget>[
              FilledButton(
                onPressed: () => _load(ApiEndpoints.unpaidBills),
                child: const Text('Unpaid Bills'),
              ),
              FilledButton.tonal(
                onPressed: () => _load(ApiEndpoints.billHistory),
                child: const Text('Bill History'),
              ),
              FilledButton.tonal(
                onPressed: () => _load(ApiEndpoints.billCollection),
                child: const Text('Collection Report'),
              ),
            ],
          ),
        ),
        ApiCard(
          title: 'Response',
          child: SelectableText(output),
        ),
      ],
    );
  }
}
