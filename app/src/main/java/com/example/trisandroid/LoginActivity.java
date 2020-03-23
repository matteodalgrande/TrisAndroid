package com.example.trisandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.login_main);
        }
        public void cambiaSchermata(View view){
            Intent myIntent = new Intent(this, MainActivity.class);
            this.startActivity(myIntent);
        }
}
