package com.example.simplekeyboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        int pad = 48;
        layout.setPadding(pad, pad, pad, pad);

        TextView text = new TextView(this);
        text.setText("Simple Keyboard\n\n1) Enable this keyboard below\n2) Then switch to it from any text field");
        text.setTextSize(16);
        text.setGravity(Gravity.CENTER);
        layout.addView(text);

        Button enableBtn = new Button(this);
        enableBtn.setText("Open Keyboard Settings");
        enableBtn.setOnClickListener(v ->
                startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)));
        layout.addView(enableBtn);

        Button switchBtn = new Button(this);
        switchBtn.setText("Choose Input Method");
        switchBtn.setOnClickListener(v -> {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showInputMethodPicker();
            }
        });
        layout.addView(switchBtn);

        setContentView(layout);
    }
}
