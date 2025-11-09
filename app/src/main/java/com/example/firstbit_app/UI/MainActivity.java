package com.example.firstbit_app.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firstbit_app.R;

/**
 * основная активность приложения
 */
public class MainActivity extends AppCompatActivity {

    /**
     * вызывается при первом создании активности
     *
     * @param savedInstanceState Если активность перезапускается после предыдущего завершения,
     *                           этот Bundle содержит данные, которые она недавно предоставила в onSaveInstanceState(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonLogin = findViewById(R.id.button_login);
        Button buttonJoin = findViewById(R.id.button_join);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            /**
             * обрабатывает событие клика по кнопке для перехода к экрану регистрации
             *
             * @param v Виджет, по которому был выполнен клик
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegActivity.class);
                startActivity(intent);
            }
        });

        buttonJoin.setOnClickListener(new View.OnClickListener() {
            /**
             * обрабатывает событие клика по кнопке для перехода к экрану авторизации
             *
             * @param v Виджет, по которому был выполнен клик
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                startActivity(intent);
            }
        });
    }
}