# BorrowBox Release and Deployment Guide

This guide describes how to generate release signing credentials, build a signed production Android App Bundle (AAB) or APK, and publish to Google Play Internal Testing.

---

## 1. Keystore Generation

Generate a release signing key using the Java `keytool` utility (available with any standard JDK):

```bash
keytool -genkey -v \
  -keystore release.jks \
  -alias borrowbox \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -dname "CN=BorrowBox, OU=Mobile, O=BorrowBox, L=Pilani, ST=Rajasthan, C=IN"
```

Save `release.jks` in a secure location outside the repository root or configure `.gitignore` to ensure it is never committed.

---

## 2. Configure Signing in `local.properties` or `gradle.properties`

Add the signing credentials to your user-specific `local.properties` (or user `~/.gradle/gradle.properties`). **Never commit passwords to version control.**

```properties
# Add to local.properties:
RELEASE_STORE_FILE=C:/secure_keys/release.jks
RELEASE_STORE_PASSWORD=your_keystore_password
RELEASE_KEY_ALIAS=borrowbox
RELEASE_KEY_PASSWORD=your_key_password
```

The `app/build.gradle` file is pre-configured to read these properties automatically:
```groovy
signingConfigs {
    release {
        def keystoreFile = project.findProperty('RELEASE_STORE_FILE') ?: System.getenv('RELEASE_STORE_FILE')
        if (keystoreFile && file(keystoreFile).exists()) {
            storeFile file(keystoreFile)
            storePassword project.findProperty('RELEASE_STORE_PASSWORD') ?: System.getenv('RELEASE_STORE_PASSWORD')
            keyAlias project.findProperty('RELEASE_KEY_ALIAS') ?: System.getenv('RELEASE_KEY_ALIAS')
            keyPassword project.findProperty('RELEASE_KEY_PASSWORD') ?: System.getenv('RELEASE_KEY_PASSWORD')
        } else {
            // Fallback to debug keystore for local build verification
            storeFile file("${System.getProperty('user.home')}/.android/debug.keystore")
            storePassword "android"
            keyAlias "androiddebugkey"
            keyPassword "android"
        }
    }
}
```

---

## 3. Build Signed Production Artifacts

### Build Android App Bundle (.aab) for Google Play:
```bash
./gradlew bundleRelease
```
The resulting bundle will be generated at:
`app/build/outputs/bundle/release/app-release.aab`

### Build Signed APK (.apk) for Local Testing / Direct Distribution:
```bash
./gradlew assembleRelease
```
The resulting APK will be generated at:
`app/build/outputs/apk/release/app-release.apk`

---

## 4. Verifying ProGuard and R8 Optimization

Run release assembly with minification and resource shrinking enabled:
```bash
./gradlew check assembleRelease
```

To inspect retained symbols and mapping files:
- Mapping file: `app/build/outputs/mapping/release/mapping.txt`
- Seeds: `app/build/outputs/mapping/release/seeds.txt`
- Usage (removed code): `app/build/outputs/mapping/release/usage.txt`

---

## 5. Uploading to Google Play Console (Internal Testing)

1. Open the [Google Play Console](https://play.google.com/console).
2. Select or create the application **BorrowBox** (`com.borrowbox.app`).
3. Under the **Release** section in the left sidebar, navigate to **Testing** -> **Internal testing**.
4. Click **Create new release**.
5. Upload `app-release.aab`.
6. Enter release notes:
   ```text
   BorrowBox v2.0.0
   - Modern Material 3 day and night theme support
   - Secure PBKDF2 password authentication
   - CameraX item photo capture and gallery import
   - Physical handover QR code generation and ML Kit scanning
   - Real-time personal dashboard with live borrowing metrics
   ```
7. Click **Next**, review the release checks, and click **Start rollout to Internal testing**.
8. Add internal tester email addresses under the **Testers** tab and share the join link.
