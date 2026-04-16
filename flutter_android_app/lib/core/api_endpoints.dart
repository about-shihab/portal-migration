class ApiEndpoints {
  ApiEndpoints._();

  // Auth (use UrlUtils.authBaseUrl)
  static const String login = 'auth/login';
  static const String signup = 'auth/signup';
  static const String forgotPassword = 'auth/forgotPassword';
  static const String otpVerify = 'auth/otp';

  // Profile/User (use UrlUtils.baseUrl)
  static const String userInfo = 'users/info';
  static const String changePassword = 'users/changePassword';
  static const String changeMobile = 'users/changeMobileNumber';

  // Bills
  static const String billRoot = 'billInfo';
  static const String billHistory = '$billRoot/billHistory';
  static const String unpaidBills = '$billRoot/unpaidList';
  static const String billCollection = '$billRoot/billCollectionReport';
  static const String gatewayFee = '$billRoot/calculateGatewayFee';

  // Complaints
  static const String complaintRoot = 'complain-infos';
  static const String createComplaint = '$complaintRoot/create';
  static const String complaintCauses = '$complaintRoot/cause-list';
  static const String complaintTickets = '$complaintRoot/ticket-status-list';
  static const String complaintReviews = '$complaintRoot/ticket-review-list';

  // Certificate / registration / reconnection
  static const String certificateDownload = 'certificate/download';

  static const String registrationRoot = 'registration-card';
  static const String registrationRequest = '$registrationRoot/issue-request';
  static const String registrationOtp = '$registrationRoot/validate-otp';
  static const String registrationDownload = '$registrationRoot/download';

  static const String reconnectionRequest = 'reconnection/request';
}
