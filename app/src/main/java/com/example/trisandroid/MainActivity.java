package com.example.trisandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private String giocatore = "primo";
    private String[] tabella = new String[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void click(View view){
        Button vista = (Button) view;
        System.out.println(vista.getId());
    }
//    public void click(View view) {
//        if (giocatore == "primo"){
//            //turno primo giocatore
//            Button vista = (Button) view;
//
//            //andiamo a decrementare id, perchè gli array partono poi da zero
//            int id = view.getId().split("tabella").toInt() - 1;
//            System.out.println(view.getId());
//            if (tabella[id] == null){
//                tabella[id] = giocatore;
//                vista.setText("X");
//                giocatore = "secondo";
//            } else{
//                //alert che la cella è già piena
//
//            }
//
//        }else if(giocatore == "secondo"){
//            //turno secondo giocatore
//            Button vista = (Button) view;
//
//            //andiamo a decrementare id, perchè gli array partono poi da zero
//            int id = view.getId().split("tabella").toInt() - 1;
//            if (tabella[id] == null){
//                tabella[id] = giocatore;
//                vista.setText("O");
//                giocatore = "primo";
//            } else{
//                //alert che la cella è già piena
//            }
//        }
//
//    }

    private void controllaVittoria(){

    }
}
