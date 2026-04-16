import 'package:flutter/widgets.dart';

import '../main.dart';
import '../services/api_service.dart';
import 'url_utils.dart';

ApiService apiFor(BuildContext context) {
  final appState = AppStateScope.of(context);
  return ApiService(baseUrl: UrlUtils.baseUrl, token: appState.session?.token);
}
