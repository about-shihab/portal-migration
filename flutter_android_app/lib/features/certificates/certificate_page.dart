import 'package:flutter/material.dart';

import '../../core/api_endpoints.dart';
import '../../core/app_api.dart';
import '../../widgets/api_card.dart';

class CertificatePage extends StatefulWidget {
  const CertificatePage({super.key});

  @override
  State<CertificatePage> createState() => _CertificatePageState();
}

class _CertificatePageState extends State<CertificatePage> {
  String output = 'Generate and download no-dues/dues certificates from backend API.';

  Future<void> _downloadCertificate() async {
    try {
      final data = await apiFor(context).getJson(ApiEndpoints.certificateDownload);
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
          title: 'Customer Certificate',
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              const Text('This mirrors the certificate feature from the Spring portal.'),
              const SizedBox(height: 12),
              FilledButton.icon(
                onPressed: _downloadCertificate,
                icon: const Icon(Icons.download),
                label: const Text('Download Certificate'),
              ),
            ],
          ),
        ),
        ApiCard(title: 'Response', child: SelectableText(output)),
      ],
    );
  }
}
