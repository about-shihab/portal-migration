import 'package:flutter/material.dart';

import '../../core/api_endpoints.dart';
import '../../core/app_api.dart';
import '../../widgets/api_card.dart';

class RegistrationCardPage extends StatefulWidget {
  const RegistrationCardPage({Key? key}) : super(key: key);

  @override
  State<RegistrationCardPage> createState() => _RegistrationCardPageState();
}

class _RegistrationCardPageState extends State<RegistrationCardPage> {
  final TextEditingController otpController = TextEditingController();
  String output = '';

  @override
  Widget build(BuildContext context) {
    return ListView(
      children: <Widget>[
        ApiCard(
          title: 'Duplicate Registration Card',
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              OutlinedButton(
                onPressed: () async {
                  final data = await apiFor(context).postJson(ApiEndpoints.registrationRequest, <String, dynamic>{});
                  setState(() => output = data.toString());
                },
                child: const Text('Request Issue OTP'),
              ),
              const SizedBox(height: 10),
              TextField(controller: otpController, decoration: const InputDecoration(labelText: 'OTP')),
              const SizedBox(height: 10),
              ElevatedButton(
                onPressed: () async {
                  final data = await apiFor(context)
                      .postJson(ApiEndpoints.registrationOtp, <String, dynamic>{'otp': otpController.text.trim()});
                  setState(() => output = data.toString());
                },
                child: const Text('Validate OTP'),
              ),
            ],
          ),
        ),
        ApiCard(title: 'Response', child: SelectableText(output)),
      ],
    );
  }
}
