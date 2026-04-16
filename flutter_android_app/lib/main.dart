import 'package:flutter/material.dart';

import 'core/app_state.dart';
import 'features/auth/login_page.dart';
import 'features/home/home_page.dart';
import 'services/session_store.dart';

void main() {
  runApp(const CustomerPortalApp());
}

class CustomerPortalApp extends StatefulWidget {
  const CustomerPortalApp({super.key});

  @override
  State<CustomerPortalApp> createState() => _CustomerPortalAppState();
}

class _CustomerPortalAppState extends State<CustomerPortalApp> {
  final AppState appState = AppState(SessionStore());

  @override
  void initState() {
    super.initState();
    appState.bootstrap();
  }

  @override
  Widget build(BuildContext context) {
    return AppStateScope(
      notifier: appState,
      child: MaterialApp(
        title: 'Customer Portal',
        debugShowCheckedModeBanner: false,
        theme: ThemeData(
          useMaterial3: true,
          colorSchemeSeed: const Color(0xFF0061A4),
          cardTheme: const CardTheme(margin: EdgeInsets.symmetric(horizontal: 12, vertical: 8)),
        ),
        home: AnimatedBuilder(
          animation: appState,
          builder: (_, __) {
            if (appState.loading) {
              return const Scaffold(body: Center(child: CircularProgressIndicator()));
            }
            if (appState.session == null) {
              return const LoginPage();
            }
            return const HomePage();
          },
        ),
      ),
    );
  }
}

class AppStateScope extends InheritedNotifier<AppState> {
  const AppStateScope({required super.child, required AppState super.notifier, super.key});

  static AppState of(BuildContext context) {
    final scope = context.dependOnInheritedWidgetOfExactType<AppStateScope>();
    assert(scope != null, 'AppStateScope not found in context');
    return scope!.notifier!;
  }
}
