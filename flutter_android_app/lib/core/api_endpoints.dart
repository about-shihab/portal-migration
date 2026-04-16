class ApiEndpoints {
  ApiEndpoints._();

  static const String api = '/api';
  static const String version = '/v1';

  // Auth
  static const String authRoot = '$api/auth';
  static const String login = '$authRoot/login';
  static const String signup = '$authRoot/signup';
  static const String forgotPassword = '$authRoot/forgotPassword';
  static const String otpVerify = '$authRoot/otp';

  // Profile/User
  static const String usersRoot = '$api$version/users';
  static const String userInfo = '$usersRoot/info';
  static const String changePassword = '$usersRoot/changePassword';
  static const String changeMobile = '$usersRoot/changeMobileNumber';

  // Bills
  static const String billRoot = '$api$version/billInfo';
  static const String billHistory = '$billRoot/billHistory';
  static const String unpaidBills = '$billRoot/unpaidList';
  static const String billCollection = '$billRoot/billCollectionReport';
  static const String gatewayFee = '$billRoot/calculateGatewayFee';

  // Complaints
  static const String complaintRoot = '$api$version/complain-infos';
  static const String createComplaint = '$complaintRoot/create';
  static const String complaintCauses = '$complaintRoot/cause-list';
  static const String complaintTickets = '$complaintRoot/ticket-status-list';
  static const String complaintReviews = '$complaintRoot/ticket-review-list';

  // Certificate / registration / reconnection
  static const String certificateRoot = '$api$version/certificate';
  static const String certificateDownload = '$certificateRoot/download';

  static const String registrationRoot = '$api$version/registration-card';
  static const String registrationRequest = '$registrationRoot/issue-request';
  static const String registrationOtp = '$registrationRoot/validate-otp';
  static const String registrationDownload = '$registrationRoot/download';

  static const String reconnectionRoot = '/reconnection';
  static const String reconnectionRequest = '$reconnectionRoot/request';
}
