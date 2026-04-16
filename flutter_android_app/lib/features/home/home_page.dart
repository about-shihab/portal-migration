import 'package:flutter/material.dart';

import '../../main.dart';
import '../bills/bills_page.dart';
import '../certificates/certificate_page.dart';
import '../complaints/complaints_page.dart';
import '../profile/profile_page.dart';
import '../reconnection/reconnection_page.dart';
import '../registration_card/registration_card_page.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  int index = 0;

  static const List<Widget> _pages = <Widget>[
    BillsPage(),
    ProfilePage(),
    ComplaintsPage(),
    CertificatePage(),
    RegistrationCardPage(),
    ReconnectionPage(),
  ];

  @override
  Widget build(BuildContext context) {
    final appState = AppStateScope.of(context);
    return Scaffold(
      appBar: AppBar(
        title: Text('Welcome, ${appState.session?.username ?? ''}'),
        actions: <Widget>[
          IconButton(
            onPressed: () => appState.logout(),
            icon: const Icon(Icons.logout),
          ),
        ],
      ),
      body: _pages[index],
      bottomNavigationBar: NavigationBar(
        selectedIndex: index,
        onDestinationSelected: (int value) => setState(() => index = value),
        destinations: const <NavigationDestination>[
          NavigationDestination(icon: Icon(Icons.receipt_long), label: 'Bills'),
          NavigationDestination(icon: Icon(Icons.person), label: 'Profile'),
          NavigationDestination(icon: Icon(Icons.support_agent), label: 'Complaint'),
          NavigationDestination(icon: Icon(Icons.file_download), label: 'Certificate'),
          NavigationDestination(icon: Icon(Icons.badge), label: 'Reg Card'),
          NavigationDestination(icon: Icon(Icons.power), label: 'Reconnect'),
        ],
      ),
    );
  }
}
