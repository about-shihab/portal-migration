# Flutter Android migration for Customer Portal

This directory contains a Flutter Android client that mirrors the major customer-facing functionality currently served by the Spring app.

## Functional mapping from Spring -> Flutter

Spring endpoint constants are defined in:
- `src/main/java/com/iict/buet/customer_portal/util/UrlConstants.java`

The Flutter app maps those API groups into mobile screens:
- **Auth**: login, forgot/otp endpoints (`lib/features/auth/login_page.dart`)
- **Bills**: unpaid list, bill history, collection report (`lib/features/bills/bills_page.dart`)
- **Profile**: profile info, password change (`lib/features/profile/profile_page.dart`)
- **Complaints**: cause list, ticket status, create complaint (`lib/features/complaints/complaints_page.dart`)
- **Certificate**: certificate download (`lib/features/certificates/certificate_page.dart`)
- **Registration Card**: issue request + OTP validation (`lib/features/registration_card/registration_card_page.dart`)
- **Reconnection**: reconnection request (`lib/features/reconnection/reconnection_page.dart`)

## Modern Android UI approach

- Material 3 design system
- Mobile-first navigation using `BottomNavigationBar`
- Feature cards with concise actions and scrollable response panes
- Persisted auth session using `shared_preferences`

## Required Flutter version

- Flutter **2.10.5** (stable)
- Dart **2.16.x**

This project intentionally avoids Dart 3-only language features so it can run on Flutter 2.10.5.

## Run

```bash
cd flutter_android_app
flutter pub get
flutter run -d android
```

By default, Android emulator backend URL is set to `http://10.0.2.2:8080`.
