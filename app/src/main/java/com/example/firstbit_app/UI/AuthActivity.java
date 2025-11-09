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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        button.setOnClickListener(new View.OnClickListener() {
            /**
             * обрабатывает событие клика по кнопке аутентификации
             */
            @Override
            public void onClick(View v) {
                String login = userLogin.getText().toString().trim();
                String password = userPassword.getText().toString().trim();

                if (login.isEmpty() || password.isEmpty()) {
                    Toast.makeText(AuthActivity.this, "Не все поля заполенны", Toast.LENGTH_SHORT).show();
                }
                else {
                    DbHelper db = new DbHelper(AuthActivity.this, null);
                    boolean userExists = db.isLoginExists(login);

                    if (!userExists) {
                        Toast.makeText(AuthActivity.this, "Пользователь " + login + " не зарегистрирован", Toast.LENGTH_SHORT).show();
                        userLogin.getText().clear();
                        userPassword.getText().clear();
                    } else {
                        boolean isAuth = db.getUser(login, password);

                        if (isAuth) {
                            Toast.makeText(AuthActivity.this, "Пользователь " + login + " авторизован", Toast.LENGTH_SHORT).show();
                            userLogin.getText().clear();
                            userPassword.getText().clear();

                            int userId = db.getUserId(login);
                            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                            prefs.edit().putInt("user_id", userId).apply();

                            Intent intent = new Intent(AuthActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }

                        else {
                            Toast.makeText(AuthActivity.this, "Пользователь " + login + " НЕ авторизован", Toast.LENGTH_SHORT).show();
                            Toast.makeText(AuthActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
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
}