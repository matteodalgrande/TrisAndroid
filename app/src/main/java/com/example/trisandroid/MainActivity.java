package com.example.trisandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private String giocatore = "primo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view) {
        if (giocatore=="primo"){
            giocatore = "secondo";
            Button vista = (Button) view;
            vista.setText("X");
        }else if(giocatore == "secondo"){
            //turno secondo giocatore
            giocatore = "primo";
            Button vista = (Button) findViewById(view.getId());
            vista.setText("O");
        }
    }
}
