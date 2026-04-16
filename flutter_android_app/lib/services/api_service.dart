import 'dart:convert';

import 'package:http/http.dart' as http;

class ApiService {
  ApiService({required this.baseUrl, this.token});

  final String baseUrl;
  String? token;

  Map<String, String> get _headers => <String, String>{
        'Content-Type': 'application/json',
        if (token != null && token!.isNotEmpty) 'Authorization': 'Bearer $token',
      };

  Future<dynamic> getJson(String path) async {
    final response = await http.get(Uri.parse('$baseUrl$path'), headers: _headers);
    return _decode(response);
  }

  Future<dynamic> postJson(String path, Map<String, dynamic> body) async {
    final response = await http.post(
      Uri.parse('$baseUrl$path'),
      headers: _headers,
      body: jsonEncode(body),
    );
    return _decode(response);
  }

  dynamic _decode(http.Response response) {
    final payload = response.body.isEmpty ? <String, dynamic>{} : jsonDecode(response.body);
    if (response.statusCode >= 200 && response.statusCode < 300) {
      return payload;
    }
    throw Exception('Request failed (${response.statusCode}): $payload');
  }
}
