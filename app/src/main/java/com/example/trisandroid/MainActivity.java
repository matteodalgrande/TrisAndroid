package com.example.trisandroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

//    public void click(View view){
//        Button vista = (Button) view;
//        System.out.println(view);
//  //      System.out.println(view.getResources().getResourceName(view.getId()));
//        System.out.println(view.getTag());
//    }
    //git commit -m "1)eliminazione id e aggiunta di tag nei Button dell'xml della view; 2)aggiunta dell'alert se si dovesse ripremere la stessa casella"


    public void click(View view) {
        if (giocatore == "primo"){
            //turno primo giocatore
            Button vista = (Button) view;

            //andiamo a decrementare id, perchè gli array partono poi da zero
            int tag = Integer.parseInt((String)view.getTag());
            if (tabella[tag] == null){
                tabella[tag] = giocatore;
                vista.setText("X");
                giocatore = "secondo";
            } else{
                //alert--> la cella è già piena
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Mossa non concessa");
                alertDialog.setMessage("Questa casella è già stata selezionata. E' necessario selezionare una casella vuota");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();
            }

        }else if(giocatore == "secondo"){
            //turno secondo giocatore
            Button vista = (Button) view;

            //andiamo a decrementare id, perchè gli array partono poi da zero
            int tag = Integer.parseInt((String)view.getTag());
            if (tabella[tag] == null){
                tabella[tag] = giocatore;
                vista.setText("O");
                giocatore = "primo";
            } else{
                //alert--> la cella è già piena
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Mossa non concessa");
                alertDialog.setMessage("Questa casella è già stata selezionata. E' necessario selezionare una casella vuota");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }

    }

    private void controllaVittoria(){

    }
}
