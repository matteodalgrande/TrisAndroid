package com.example.trisandroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static String PACKAGE_NAME;
    private String giocatore = "primo";
    private String turnoInizio = "primo";
    private String[] tabella = new String[9];
    private int punteggioPlayer1, punteggioPlayer2 = 0;
//    private String namePlayer1, getNamePlayer2;
    private String nomeGiocatore1;
    private String nomeGiocatore2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        this.nomeGiocatore1 = intent.getStringExtra("giocatore1");
        this.nomeGiocatore2 = intent.getStringExtra("giocatore2");
        System.out.println("main-------------->" + intent.getStringExtra("giocatore1"));
        TextView editText1 = (TextView) findViewById(R.id.text_view_p1);
        TextView editText2 = (TextView) findViewById(R.id.text_view_p2);
        editText1.setText(this.nomeGiocatore1 + ": " + this.punteggioPlayer1);
        editText2.setText(this.nomeGiocatore2 + ": " + this.punteggioPlayer2);

        PACKAGE_NAME = getApplicationContext().getPackageName();
        svuotaTabella();
    }

    public void click(View view) {
        if (giocatore == "primo"){
            //turno primo giocatore
            Button vista = (Button) view;

            //andiamo a decrementare tag, perchè gli array partono poi da zero
            int tag = Integer.parseInt((String)view.getTag()) - 1;
            if (tabella[tag] == "vuoto"){
                tabella[tag] = giocatore;
                vista.setText("X");
                controllaVittoria(giocatore);
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

            //andiamo a decrementare tag, perchè gli array partono poi da zero
            int tag = Integer.parseInt((String)view.getTag()) - 1;
            if (tabella[tag] == "vuoto"){
                tabella[tag] = giocatore;
                vista.setText("O");
                controllaVittoria(giocatore);
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

    public void reset(View view) {
        setContentView(R.layout.activity_main);
        /*Ogni volta che vuoi cambiare l'aspetto corrente di un'attività o quando passi da un'attività all'altra,
         *la nuova attività deve avere un design da mostrare.
         * Chiamiamo setContentViewonCreate() con il design desiderato come argomento.
         *
         * Un’Activity è un’interfaccia utente. Ogni volta che si usa un’app generalmente si interagisce con una o più
         * “pagine” mediante le quali si consultano dati o si immettono input. Ovviamente la realizzazione di Activity
         * è il punto di partenza di ogni corso di programmazione Android visto che è il componente con cui l’utente ha
         * il contatto più diretto.
         * Le Activity nel loro complesso costituiscono il flusso in cui l’utente si inoltra per sfruttare le funzionalità
         * messe a disposizione. Questo richiede pertanto non solo la realizzazione delle interfacce in sé stesse, ma anche
         * una corretta progettazione della navigazione tra di esse offrendo la possibilità di sfogliarne i contenuti e risalirli
         * gerarchicamente in maniera coerente.
         * */
        //svuoto la tabella
        svuotaTabella();

        //svuoto il punteggio dei player
        TextView textPlayer1 = (TextView) findViewById(R.id.text_view_p1);
        TextView textPlayer2 = (TextView) findViewById(R.id.text_view_p2);
        punteggioPlayer1 = 0;
        punteggioPlayer2 = 0;
        textPlayer1.setText(nomeGiocatore1 + ": " + punteggioPlayer1);
        textPlayer2.setText(nomeGiocatore2 + ": " + punteggioPlayer2);
        giocatore = "primo";
        turnoInizio = "primo";

    }

    //questa funzione controlla la vittoria e cambia il giocatore
    private void controllaVittoria(String giocatore) {
        if( giocatore == "primo" && ((tabella[0] == tabella[1] && tabella[0] == tabella[2] && tabella[0] == giocatore) || (tabella[3] == tabella[4] && tabella[3] == tabella[5] && tabella[3] == giocatore) || (tabella[6] == tabella[7] && tabella[6] == tabella[8] && tabella[6] == giocatore) ||
                                     (tabella[0] == tabella[3] && tabella[0] == tabella[6] && tabella[0] == giocatore) || (tabella[1] == tabella[4] && tabella[1] == tabella[7] && tabella[1] == giocatore) || (tabella[2] == tabella[5] && tabella[2] == tabella[8] && tabella[2] == giocatore) ||
                                     (tabella[0] == tabella[4] && tabella[0] == tabella[8] && tabella[0] == giocatore) || (tabella[2] == tabella[4] && tabella[2] == tabella[6] && tabella[2] == giocatore))){
            svuotaTabella();
            aggiungiVittoriaAPlayer(giocatore);
            cambiaGiocatoreInizio(turnoInizio);
            return;
        } else if( giocatore=="secondo" && ((tabella[0] == tabella[1] && tabella[0] == tabella[2] && tabella[0] == giocatore) || (tabella[3] == tabella[4] && tabella[3] == tabella[5] && tabella[3] == giocatore) || (tabella[6] == tabella[7] && tabella[6] == tabella[8] && tabella[6] == giocatore) ||
                                            (tabella[0] == tabella[3] && tabella[0] == tabella[6] && tabella[0] == giocatore) || (tabella[1] == tabella[4] && tabella[1] == tabella[7] && tabella[1] == giocatore) || (tabella[2] == tabella[5] && tabella[2] == tabella[8] && tabella[2] == giocatore) ||
                                            (tabella[0] == tabella[4] && tabella[0] == tabella[8] && tabella[0] == giocatore) || (tabella[2] == tabella[4] && tabella[2] == tabella[6] && tabella[2] == giocatore))){
            svuotaTabella();
            aggiungiVittoriaAPlayer(giocatore);
            cambiaGiocatoreInizio(turnoInizio);
            return;
        }
        //controllo se qualcuno ha vinto
        //controllo tutte le righe

        //se la tabella è piena e nessuno ha vinto allora è pareggio()
        if(tabella[0]!="vuoto"&&tabella[1]!="vuoto"&&tabella[2]!="vuoto"&&tabella[3]!="vuoto"&&tabella[4]!="vuoto"&&tabella[5]!="vuoto"&&tabella[6]!="vuoto"&&tabella[7]!="vuoto"&&tabella[8]!="vuoto"){
            pareggio();
            return;
        }

        cambioTurno(giocatore);
    }

    private void pareggio(){
        //svuoto la tabella
        svuotaTabella();
        cambiaGiocatoreInizio(turnoInizio);
    }

    private void aggiungiVittoriaAPlayer(String player){
        if (player=="primo"){
            punteggioPlayer1+=1;
            TextView textPlayer1 = (TextView) findViewById(R.id.text_view_p1);
            textPlayer1.setText(nomeGiocatore1 + ": " + punteggioPlayer1);
        } else if (player=="secondo"){
            punteggioPlayer2+=1;
            TextView textPlayer2 = (TextView) findViewById(R.id.text_view_p2);
            textPlayer2.setText(nomeGiocatore2 + ": " + punteggioPlayer2);
        }
    }

    private void svuotaTabella(){
        for ( int i = 0; i < 9; i++) {
            tabella[i] = "vuoto";
            String buttonID = "button" + (i+1);
            int resID = getResources().getIdentifier(buttonID, "id", PACKAGE_NAME);
            /*Resources è una classe per l'accesso alle risorse di un app
             *
             * In genere è possibile acquisire l' Resources istanza associata alla propria applicazione con getResources().
             *
             * getIdentifier(String name, String defType, String defPackage)
             * Restituisce un identificatore di risorsa per il nome di risorsa specificato.
             *
             * PACKAGE_NAME = getPackageName()
             * */
            Button btn = (Button) findViewById(resID);
            btn.setText("");
        }
    }

    private void cambiaGiocatoreInizio(String turno){
        if(turno=="primo"){
            this.turnoInizio="secondo";
            this.giocatore="secondo";
        }else if (turno=="secondo"){
            this.turnoInizio="primo";
            this.giocatore="primo";
        }
    }

    private void cambioTurno(String giocatore){
        if (giocatore=="primo"){
            this.giocatore="secondo";
        }else if(giocatore=="secondo"){
            this.giocatore="primo";
        }
    }
    //serve per cambaire la view da visualizzare nell'activity
    //setContentView(R.layout.activity_main);


}
