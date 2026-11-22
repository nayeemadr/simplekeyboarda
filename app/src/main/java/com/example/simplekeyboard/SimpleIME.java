package com.example.simplekeyboard;

import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class SimpleIME extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    // Custom key codes (negative, outside the standard Keyboard.KEYCODE_* range)
    private static final int CODE_GLOBE = -101;
    private static final int CODE_EMOJI = -102;

    private enum Language { ENGLISH, BENGALI }
    private enum Panel { LETTERS, SYMBOLS, EMOJI }

    private FrameLayout container;
    private KeyboardView keyboardView;
    private View emojiView;

    private Keyboard kbEnglish;
    private Keyboard kbBengali;
    private Keyboard kbBengaliShifted;
    private Keyboard kbSymbols;

    private Language currentLanguage = Language.ENGLISH;
    private Panel currentPanel = Panel.LETTERS;

    private boolean englishShift = false;
    private boolean bengaliShift = false;

    private static final String[] EMOJIS = {
            "😀", "😁", "😂", "🤣", "😊", "😍", "😘", "😎", "🤔", "😴",
            "😢", "😭", "😡", "🥳", "😇", "🤗", "🙄", "😅", "😉", "🙂",
            "👍", "👎", "👏", "🙏", "💪", "👌", "✌️", "🤝", "👋", "🤞",
            "❤️", "🔥", "✨", "🎉", "⭐", "💯", "✅", "❌", "⚡", "🎶",
            "☀️", "🌙", "⛅", "🌧️", "❄️", "🌸", "🐱", "🐶", "🐼", "🦋",
            "🍕", "🍔", "☕", "🍰", "🍎", "⚽", "🎮", "📱", "💻", "📷"
    };

    @Override
    public View onCreateInputView() {
        container = new FrameLayout(this);
        container.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
        keyboardView.setOnKeyboardActionListener(this);
        container.addView(keyboardView);

        kbEnglish = new Keyboard(this, R.xml.qwerty);
        kbBengali = new Keyboard(this, R.xml.bengali);
        kbBengaliShifted = new Keyboard(this, R.xml.bengali_shifted);
        kbSymbols = new Keyboard(this, R.xml.symbols);

        emojiView = buildEmojiView();
        emojiView.setVisibility(View.GONE);
        container.addView(emojiView);

        currentLanguage = Language.ENGLISH;
        currentPanel = Panel.LETTERS;
        englishShift = false;
        bengaliShift = false;
        keyboardView.setKeyboard(kbEnglish);

        return container;
    }

    private View buildEmojiView() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#1A1A1A"));

        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setPadding(12, 12, 12, 12);

        Button backBtn = new Button(this);
        backBtn.setText("ABC");
        backBtn.setOnClickListener(v -> showLetters());
        topBar.addView(backBtn);

        Button delBtn = new Button(this);
        delBtn.setText("⌫");
        delBtn.setOnClickListener(v -> {
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.deleteSurroundingText(2, 0);
            }
        });
        topBar.addView(delBtn);

        root.addView(topBar);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(220)));

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(8);
        grid.setPadding(8, 8, 8, 8);

        for (String emoji : EMOJIS) {
            TextView tv = new TextView(this);
            tv.setText(emoji);
            tv.setTextSize(22);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(dp(8), dp(10), dp(8), dp(10));
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = dp(40);
            lp.height = dp(44);
            tv.setLayoutParams(lp);
            tv.setClickable(true);
            tv.setFocusable(true);
            tv.setOnClickListener(v -> {
                InputConnection ic = getCurrentInputConnection();
                if (ic != null) {
                    ic.commitText(emoji, 1);
                }
            });
            grid.addView(tv);
        }

        scrollView.addView(grid);
        root.addView(scrollView);

        return root;
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }

    private void showLetters() {
        currentPanel = Panel.LETTERS;
        emojiView.setVisibility(View.GONE);
        keyboardView.setVisibility(View.VISIBLE);
        applyCurrentLetterKeyboard();
    }

    private void showSymbols() {
        currentPanel = Panel.SYMBOLS;
        emojiView.setVisibility(View.GONE);
        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setKeyboard(kbSymbols);
    }

    private void showEmoji() {
        currentPanel = Panel.EMOJI;
        keyboardView.setVisibility(View.GONE);
        emojiView.setVisibility(View.VISIBLE);
    }

    private void applyCurrentLetterKeyboard() {
        if (currentLanguage == Language.ENGLISH) {
            kbEnglish.setShifted(englishShift);
            keyboardView.setKeyboard(kbEnglish);
        } else {
            keyboardView.setKeyboard(bengaliShift ? kbBengaliShifted : kbBengali);
        }
    }

    private void toggleLanguage() {
        currentLanguage = (currentLanguage == Language.ENGLISH) ? Language.BENGALI : Language.ENGLISH;
        englishShift = false;
        bengaliShift = false;
        showLetters();
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) {
            return;
        }

        switch (primaryCode) {
            case CODE_GLOBE:
                toggleLanguage();
                break;

            case CODE_EMOJI:
                showEmoji();
                break;

            case Keyboard.KEYCODE_MODE_CHANGE:
                if (currentPanel == Panel.SYMBOLS) {
                    showLetters();
                } else {
                    showSymbols();
                }
                break;

            case Keyboard.KEYCODE_SHIFT:
                if (currentPanel == Panel.LETTERS && currentLanguage == Language.ENGLISH) {
                    englishShift = !englishShift;
                    kbEnglish.setShifted(englishShift);
                    keyboardView.invalidateAllKeys();
                } else if (currentPanel == Panel.LETTERS && currentLanguage == Language.BENGALI) {
                    bengaliShift = !bengaliShift;
                    keyboardView.setKeyboard(bengaliShift ? kbBengaliShifted : kbBengali);
                }
                break;

            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;

            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                break;

            default:
                char code = (char) primaryCode;
                if (currentPanel == Panel.LETTERS
                        && currentLanguage == Language.ENGLISH
                        && Character.isLetter(code)
                        && englishShift) {
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code), 1);
        }
    }

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onText(CharSequence text) {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeUp() {
    }
}
