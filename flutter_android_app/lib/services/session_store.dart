import 'package:shared_preferences/shared_preferences.dart';

import '../models/stored_session.dart';

class SessionStore {
  static const String _tokenKey = 'auth_token';
  static const String _userKey = 'auth_user';

  Future<void> save(String token, String username) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_tokenKey, token);
    await prefs.setString(_userKey, username);
  }

  Future<StoredSession?> load() async {
    final prefs = await SharedPreferences.getInstance();
    final token = prefs.getString(_tokenKey);
    final user = prefs.getString(_userKey);
    if (token == null || token.isEmpty) {
      return null;
    }
    return StoredSession(token: token, username: user ?? '');
  }

  Future<void> clear() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_tokenKey);
    await prefs.remove(_userKey);
  }
}
