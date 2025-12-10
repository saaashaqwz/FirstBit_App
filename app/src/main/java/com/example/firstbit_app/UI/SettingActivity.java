package com.example.firstbit_app.UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firstbit_app.DbHelper;
import com.example.firstbit_app.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * активность настройки - отображает данные пользователя
 */
public class SettingActivity extends AppCompatActivity {

    private TextInputEditText etName, etLogin, etPhone;
    private TextInputLayout tilPhone;
    private TextView tvPhoneHint, btnBack;
    private Button btnSave;
    private DbHelper dbHelper;
    private int userId;
    private String originalPhoneAtFirstLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        dbHelper = new DbHelper(this, null);

        etName = findViewById(R.id.et_name);
        etLogin = findViewById(R.id.et_login);
        etPhone = findViewById(R.id.et_phone);
        tilPhone = findViewById(R.id.til_phone);
        tvPhoneHint = findViewById(R.id.tv_phone_hint);
        btnSave = findViewById(R.id.btn_save_settings);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Настройки профиля");
        }

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Ошибка: не авторизован", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadUserData();
        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void loadUserData() {
        String login = dbHelper.getUserLoginById(userId);
        String name = dbHelper.getUserNameById(userId);
        String phoneFromDb = dbHelper.getUserPhoneById(userId);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        originalPhoneAtFirstLaunch = prefs.getString("original_phone", null);

        if (originalPhoneAtFirstLaunch == null) {
            prefs.edit().putString("original_phone", phoneFromDb).apply();
            originalPhoneAtFirstLaunch = phoneFromDb;
        }

        etLogin.setText(login);
        etName.setText(name != null ? name : "");
        etPhone.setText(phoneFromDb);

        if (!phoneFromDb.equals(originalPhoneAtFirstLaunch)) {
            etPhone.setEnabled(false);
            tvPhoneHint.setText("Номер уже был изменён");
            tvPhoneHint.setTextColor(getResources().getColor(android.R.color.darker_gray, null));
        } else {
            tvPhoneHint.setText("Можно изменить только один раз");
            tvPhoneHint.setTextColor(getResources().getColor(R.color.magenta, null));
        }
    }

    private void saveChanges() {
        String newName = etName.getText().toString().trim();
        String newPhone = etPhone.getText().toString().trim();

        if (newName.isEmpty()) {
            Toast.makeText(this, "Введите ваше имя", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPhone.isEmpty()) {
            Toast.makeText(this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentPhoneInDb = dbHelper.getUserPhoneById(userId);

        if (!newPhone.equals(currentPhoneInDb) && !currentPhoneInDb.equals(originalPhoneAtFirstLaunch)) {
            Toast.makeText(this, "Вы уже меняли номер телефона!", Toast.LENGTH_LONG).show();
            return;
        }

        boolean success = dbHelper.updateUserNameAndPhone(userId, newName, newPhone);

        if (success) {
            Toast.makeText(this, "Данные сохранены!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}