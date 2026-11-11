# FleetCRM — Travel CRM & Admin Operations Command Center

Android Travel CRM & Fleet Management application built with Kotlin, Jetpack Compose (Material 3), Cloud Firestore, and Google Sign-In (`CredentialManager`).

## Automatic Debug APK Build & Direct Download (GitHub Actions + Releases)

This project includes a pre-configured GitHub Actions workflow at `.github/workflows/build-debug-apk.yml` that automatically builds a signed Debug APK (`FleetCRM-debug.apk`) and publishes it to **GitHub Releases** for direct download.

### How It Works
1. **Push to GitHub**: Push this repository to `main` or `master` (or manually trigger **Build & Release Debug APK** from the **Actions** tab via `workflow_dispatch`).
2. **Firebase Keystore Preservation**: The workflow automatically restores `debug.keystore` from `debug.keystore.base64` so that the generated APK is signed with the exact SHA-1/SHA-256 certificate registered in `app/google-services.json` for Google Sign-In.
3. **Direct APK Download**:
   - **Via GitHub Releases (Direct URL)**: Open your repository's **Releases** section (`https://github.com/<owner>/<repo>/releases/latest`) and tap **`FleetCRM-debug.apk`** under **Assets** to download and install directly on any Android phone or tablet.
   - **Via GitHub Actions Artifacts**: Open the **Actions** tab, select the latest **Build & Release Debug APK** run, and download the **`FleetCRM-debug-apk`** artifact.
