# ZHE AUDIO PRO Diagnostic v0.5

Android diagnostic project for Android 9 / API 28 devices.

## Build with GitHub Actions
1. Upload the contents of this ZIP to a new GitHub repository (extract first).
2. Open the **Actions** tab.
3. Run **Build Android APK** (or push to main).
4. Download the artifact named `ZHE-AUDIO-PRO-Diagnostic-v0.5`.

## Safety scope
This app reports Android audio metadata, package metadata and filesystem access flags.
It does not open DVB/ALSA devices, issue ioctl calls, tune channels, mute audio, or invoke vendor services.

`RECORD_AUDIO` is declared for permission-state reporting only; the app does not record audio.
