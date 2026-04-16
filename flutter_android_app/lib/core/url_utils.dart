class UrlUtils {
  UrlUtils._();

  // static const String ip = "192.168.43.177"; // device IP
  static const String ip = 'api.kgdcl.gov.bd'; // live IP
  static const String port = ':8050'; // previous IP

  static const String authBaseUrl = 'http://' + ip + port + '/kgdclApps/api/';
  static const String baseUrl = 'http://' + ip + port + '/kgdclApps/api/v1/';
}
