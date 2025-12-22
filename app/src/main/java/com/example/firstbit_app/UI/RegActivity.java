package com.example.firstbit_app.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firstbit_app.DbHelper;
import com.example.firstbit_app.Models.User;
import com.example.firstbit_app.R;

/**
 * активность для регистрации нового пользователя
 */
public class RegActivity extends AppCompatActivity {

    private EditText userPassword;
    private ImageButton passwordToggle;
    private boolean isPasswordVisible = false;

    /**
     * вызывается при первом создании активности
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        EditText userLogin = findViewById(R.id.user_login);
        EditText userPhone = findViewById(R.id.user_phone);
        userPassword = findViewById(R.id.user_password);
        Button button = findViewById(R.id.button_reg);
        TextView linkToAuth = findViewById(R.id.link_to_auth);
        passwordToggle = findViewById(R.id.password_toggle_reg);

        DbHelper db = new DbHelper(RegActivity.this, null);

        // обработчик для переключения видимости пароля
        passwordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        linkToAuth.setOnClickListener(new View.OnClickListener() {
            /**
             * обрабатывает событие клика по ссылке для перехода к экрану авторизации
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegActivity.this, AuthActivity.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            /**
             * обрабатывает событие клика по кнопке регистрации
             */
            @Override
            public void onClick(View v) {
                String login = userLogin.getText().toString().trim();
                String rawPhone = userPhone.getText().toString().trim();
                String normalizedPhone = normalizePhone(rawPhone);
                String password = userPassword.getText().toString().trim();

                if (login.isEmpty() || normalizedPhone.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegActivity.this, "Не все поля заполнены", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (login.length() > 30 || login.length() < 4) {
                    Toast.makeText(RegActivity.this, "Логин должен быть от 4 до 30 символов", Toast.LENGTH_SHORT).show();
                    return;
                }

                String phoneDigits = normalizedPhone.replaceAll("\\D", "");
                if (phoneDigits.length() != 11 || (!phoneDigits.startsWith("7") && !phoneDigits.startsWith("8"))) {
                    Toast.makeText(RegActivity.this, "Некорректный телефон", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() > 50 || password.length() < 8) {
                    Toast.makeText(RegActivity.this, "Пароль должен быть от 8 до 50 символов", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (db.isLoginExists(login) || db.isPhoneExists(normalizedPhone)) {
                    Toast.makeText(RegActivity.this, "Такой пользователь уже существует", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = new User(login, normalizedPhone, password);

                db.addUser(user);
                Toast.makeText(RegActivity.this, "Пользователь " + login + " зарегистрирован", Toast.LENGTH_SHORT).show();

                int userId = db.getUserId(login);
                SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                prefs.edit().putInt("user_id", userId).apply();

                userLogin.getText().clear();
                userPhone.getText().clear();
                userPassword.getText().clear();

                Intent intent = new Intent(RegActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * переключает видимость пароля
     */
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            userPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordToggle.setImageResource(R.drawable.icon_eye_close);
        } else {
            userPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            passwordToggle.setImageResource(R.drawable.icon_eye_open);
        }
        isPasswordVisible = !isPasswordVisible;

        userPassword.setSelection(userPassword.getText().length());
    }

    /**
     * нормализация номера телефона: приводит к виду 7xxxxxxxxxx
     */
    private String normalizePhone(String phone) {
        if (phone == null) return null;

        String digits = phone.replaceAll("\\D", "");

        if (digits.startsWith("8") && digits.length() == 11) {
            digits = "7" + digits.substring(1);
        }
        else if (digits.length() == 10) {
            digits = "7" + digits;
        }
        else if (!digits.startsWith("7") || digits.length() != 11) {
            return null;
        }

        return digits;
    }
}