import 'package:flutter/foundation.dart';

import '../models/session.dart';
import '../services/session_store.dart';

class AppState extends ChangeNotifier {
  AppState(this._sessionStore);

  final SessionStore _sessionStore;
  Session? session;
  bool loading = true;

  Future<void> bootstrap() async {
    final stored = await _sessionStore.load();
    if (stored != null) {
      session = Session(token: stored.$1, username: stored.$2);
    }
    loading = false;
    notifyListeners();
  }

  Future<void> setSession(Session newSession) async {
    session = newSession;
    await _sessionStore.save(newSession.token, newSession.username);
    notifyListeners();
  }

  Future<void> logout() async {
    session = null;
    await _sessionStore.clear();
    notifyListeners();
  }
}
