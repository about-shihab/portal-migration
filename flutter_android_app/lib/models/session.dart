class Session {
  const Session({required this.token, required this.username});

  final String token;
  final String username;

  factory Session.fromJson(Map<String, dynamic> json) {
    return Session(
      token: json['accessToken'] as String? ?? '',
      username: json['username'] as String? ?? '',
    );
  }
}
