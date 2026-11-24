# QuickFast — Java Android MVP

A simple intermittent-fasting timer built with **Java** and the Android SDK. The app uses native Android Views (no Kotlin and no Jetpack Compose).

## Included in this starter
- Live fasting timer with elapsed time and estimated end time
- Preset 12:12, 14:10, 16:8, 18:6 and 20:4 schedules
- Start and end fasting sessions
- Local session history (up to 30 sessions)
- Light, gender-neutral cream and sage theme
- Local-only storage; no account or internet permission

## Build
This project uses Android Gradle Plugin 8.7.3, Gradle 8.9, Java 17, compileSdk 35 and minSdk 26.

The debug APK is normally created at `app/build/outputs/apk/debug/app-debug.apk`.

## GitHub Actions
The `.github/workflows/android-apk.yml` workflow builds the APK when changes are pushed to `main`, or when manually started from the Actions tab.

## Current MVP limitations
Custom schedules, notifications, water tracking, calendar view and cloud backup are not included in this Java conversion yet. The fasting timer is based on a saved start timestamp and continues accurately when the app is closed and reopened. QuickFast is a tracking tool, not medical advice.
