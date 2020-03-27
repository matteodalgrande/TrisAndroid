package com.example.trisandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.login_main);
        }
        public void cambiaSchermata(View view) {
            Intent myIntent = new Intent(this, Tris.class);
            EditText editText1 = (EditText) findViewById(R.id.editText1);
            EditText editText2 = (EditText) findViewById(R.id.editText2);
            System.out.println("login-------------->" + editText1.getText());
            myIntent.putExtra("giocatore1", editText1.getText().toString());//BISOGNA FARLO DIVENTARE UN STRINGA IN QUALCHE MODO!
            myIntent.putExtra("giocatore2", editText2.getText().toString());
            this.startActivity(myIntent);
        }


}
