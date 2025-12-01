package com.example.firstbit_app.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.firstbit_app.R;

/**
 * активность настройки - отображает данные пользователя
 */
public class SettingActivity extends AppCompatActivity {

    private LinearLayout navHome, navCart, navSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }
}