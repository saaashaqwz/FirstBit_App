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
import com.example.firstbit_app.R;

/**
 * активность для аутентификации пользователя
 */
public class AuthActivity extends AppCompatActivity {

    private EditText userLogin, userPassword;
    private ImageButton passwordToggle;
    private boolean isPasswordVisible = false;

    /**
     * вызывается при первом создании активности
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        userLogin = findViewById(R.id.user_login_auth);
        userPassword = findViewById(R.id.user_password_auth);
        Button button = findViewById(R.id.button_auth);
        TextView linkToReg = findViewById(R.id.link_to_reg);
        passwordToggle = findViewById(R.id.password_toggle_auth);

        // обработчик для переключения видимости пароля
        passwordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        linkToReg.setOnClickListener(new View.OnClickListener() {
            /**
             * обрабатывает событие клика по ссылке для перехода к экрану регистрации
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthActivity.this, RegActivity.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(v -> {
            String input = userLogin.getText().toString().trim();
            String password = userPassword.getText().toString().trim();

            if (input.isEmpty() || password.isEmpty()) {
                Toast.makeText(AuthActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            DbHelper db = new DbHelper(AuthActivity.this, null);
            boolean isAuthSuccessful = false;
            int userId = -1;

            // существует ли такой логин
            if (db.isLoginExists(input)) {
                if (db.getUser(input, password)) {
                    isAuthSuccessful = true;
                    userId = db.getUserId(input);
                }
            } else {
                // если логина нет — пробуем интерпретировать ввод как телефон
                String normalizedPhone = normalizePhone(input);
                if (normalizedPhone != null && db.isPhoneExists(normalizedPhone)) {
                    if (db.getUserByPhone(normalizedPhone, password)) {
                        isAuthSuccessful = true;
                        userId = db.getUserIdByPhone(normalizedPhone);
                    }
                }
            }

            if (isAuthSuccessful && userId != -1) {
                Toast.makeText(AuthActivity.this, "Вход выполнен успешно", Toast.LENGTH_SHORT).show();

                SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                prefs.edit().putInt("user_id", userId).apply();

                userLogin.getText().clear();
                userPassword.getText().clear();

                Intent intent = new Intent(AuthActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(AuthActivity.this, "Неверный логин, номер телефона или пароль", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * нормализация номера телефона: приводит к виду 7xxxxxxxxxx
     */
    private String normalizePhone(String phone) {
        if (phone == null || phone.isEmpty()) return null;

        String digits = phone.replaceAll("\\D", "");

        if (digits.startsWith("8") && digits.length() == 11) {
            digits = "7" + digits.substring(1);
        } else if (digits.length() == 10) {
            digits = "7" + digits;
        } else if (digits.startsWith("7") && digits.length() == 11) {
        } else {
            return null;
        }

        return digits;
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
}