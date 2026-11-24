# Still — Android MVP

A minimal intermittent-fasting timer built with Kotlin and Jetpack Compose.

## Included
- Live fasting timer with elapsed and estimated remaining time
- Preset 12:12, 14:10, 16:8, 18:6 and 20:4 schedules
- Start/end fasting sessions
- Local session history (up to 30 sessions)
- Light, gender-neutral visual theme
- Local-only storage; no account or internet permission

## Build an APK
1. Install Android Studio (with Android SDK 35).
2. Open this folder (`Still`) as an existing project.
3. Allow Gradle to sync.
4. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. Android Studio will show the generated APK location (normally `app/build/outputs/apk/debug/app-debug.apk`).

The project uses Android Gradle Plugin 8.7.3 and Kotlin 2.0.21. This environment did not have the Android SDK or Gradle installed, so an APK could not be compiled here.

## Notes
This is an early MVP. It does not yet include notifications, custom schedules, water tracking, or cloud backup. The timer is based on a saved start timestamp, so it continues accurately when the app is closed and reopened. Fasting is not suitable for everyone; the app is a tracker, not medical advice.
