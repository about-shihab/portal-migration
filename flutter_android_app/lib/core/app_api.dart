import 'package:flutter/widgets.dart';

import '../main.dart';
import '../services/api_service.dart';

ApiService apiFor(BuildContext context) {
  final appState = AppStateScope.of(context);
  return ApiService(baseUrl: 'http://10.0.2.2:8080', token: appState.session?.token);
}
