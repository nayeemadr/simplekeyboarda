# Simple Keyboard (Android)

A custom Android keyboard (IME) using `InputMethodService` + `KeyboardView`, with:
- English QWERTY layout (lowercase/uppercase via Shift)
- Bengali layout — Shift swaps to a completely different character layer
  (not just case), matching how Bangla Gboard's Shift key works
- 🌐 globe key switches between English and Bengali
- ?123 key opens a numbers/symbols page
- 🙂 key opens an emoji picker (tap ABC to return to the keyboard)

**Note on the Bengali layout:** the character-per-key mapping was read off
reference screenshots of Bangla Gboard. Most positions are double-checked
against the Shift layer for consistency, but a couple of less legible keys
are a best-effort guess. Test it and tell me which keys are wrong — easy
to fix once you point them out.

## Build via GitHub Actions
Push this repo to GitHub. The included workflow (`.github/workflows/android.yml`)
builds a debug APK automatically on every push, and you can also trigger it
manually from the Actions tab ("Run workflow"). Download the APK from the
workflow run's "Artifacts" section (`app-debug`).

## Install & test
1. Install the APK on your phone (enable "Install unknown apps" for your
   file manager/browser if prompted).
2. Open the "Simple Keyboard" app, tap "Open Keyboard Settings", enable it.
3. Tap into any text field, tap "Choose Input Method" (or use the keyboard
   switcher icon in the system nav bar) and select "Simple Keyboard".

## Next steps once this works
- Add a numbers/symbols page (mode-change key)
- Add emoji support
- Add haptic feedback / key sounds
- Custom themes/colors
