package com.example.trisandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.renderscript.Sampler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrisMultiplayerActivity extends AppCompatActivity {

    private static String PACKAGE_NAME;

    private String playerName = "";//nome del giocatore locale
    private String playerOpponent = "Giocatore2";
    private String roomName = "";
    private String role = ""; //lo uso per deterimnare il turno
    private ArrayList<String> tabella = new ArrayList<String>();

    private ArrayList<Integer> playersPoint = new ArrayList<Integer>();

    private String turno = "host";

    private FirebaseDatabase database;
    private DatabaseReference turnoRef;
    private DatabaseReference tabellaRef;
    private DatabaseReference punteggioRef;
    private DatabaseReference roomsRef;
    private DatabaseReference playersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tris_multiplayer);

        PACKAGE_NAME = getApplicationContext().getPackageName();

        database = FirebaseDatabase.getInstance();

        //recupero il nome del giocatore locale
        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName", "");

        //estraggo gli extra dell'intent [Bundle è una classe che permette di gestire dei mapping di oggetti del tipo key value]
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            roomName = extras.getString("roomName");

            roomsRef = database.getReference("rooms/" + roomName);

            playersRef = database.getReference("rooms/" + roomName + "/players");
            punteggioRef = database.getReference("rooms/" + roomName + "/punteggio");
            tabellaRef = database.getReference("rooms/" + roomName + "/tabella");
            turnoRef = database.getReference("rooms/" + roomName + "/turno");

            if (roomName.equals(playerName)) {
                role = "host";
                changePlayerListener();
                turnoRef.setValue(role);

                svuotaTabella();
                tabellaRef.setValue(tabella);

                playersPoint.clear();
                playersPoint.add(0);
                playersPoint.add(0);


                TextView textPlayer1 = (TextView) findViewById(R.id.text_view_p1);
                TextView textPlayer2 = (TextView) findViewById(R.id.text_view_p2);
                textPlayer1.setText(playerName + ": " + playersPoint.get(0));
                textPlayer2.setText(playerOpponent + ": " + playersPoint.get(1));

                punteggioRef.child(playerName).setValue(playersPoint.get(0));

            } else {
                role = "guest";
                playerOpponent = roomName;

                svuotaTabella();
                tabellaRef.setValue(tabella);

                playersPoint.clear();
                playersPoint.add(0);
                playersPoint.add(0);


                TextView textPlayer1 = (TextView) findViewById(R.id.text_view_p1);
                TextView textPlayer2 = (TextView) findViewById(R.id.text_view_p2);

                textPlayer1.setText(playerName + ": " + playersPoint.get(1));
                textPlayer2.setText(playerOpponent + ": " + playersPoint.get(0));

                punteggioRef.child(playerName).setValue(playersPoint.get(1));
            }
        }
        changeTableListener();
        changeTurnListener();
        changePointListener();
        changePlayerListener();
    }

   private void changePlayerListener(){
        playersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(role.equals("host")) {
                        playerOpponent = dataSnapshot.child("player2").getValue(String.class);
                }
                else if(role.equals("guest")) {
                    playerOpponent = dataSnapshot.child("player1").getValue(String.class);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
   }

    //se viene cambiata la tabella online allora me la scarico
    private void changeTableListener() {
        tabellaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tabella.clear();
                int i = 0;
                for (DataSnapshot element : dataSnapshot.getChildren()) {
                    tabella.add(element.getValue(String.class));
                    System.out.println(tabella.get(i));
                    String buttonID = "button" + (i + 1);
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
                    if (tabella.get(i).equals("vuoto")) {
                        //do nothing
                        btn.setText("");
                        i++;
                    } else {
                        if (tabella.get(i).equals("host")) {
                            btn.setText("X");
                            i++;
                        } else if (tabella.get(i).equals("guest")) {
                            btn.setText("O");
                            i++;
                        }
                    }
                }
                controllaVittoria();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //errore di lettura tabella

            }
        });
    }

    private void changeTurnListener() {
        turnoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                turno = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error in changeTurnList###############");
            }
        });
    }

    private void changePointListener(){
        punteggioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                playersPoint.clear();
                if(role.equals("host")) {
                    playersPoint.add(dataSnapshot.child(playerName).getValue(Integer.class));
                    try{
                        playersPoint.add(dataSnapshot.child(playerOpponent).getValue(Integer.class));
                        setContentView(R.layout.activity_tris_multiplayer);
                        TextView textPlayer1 = (TextView) findViewById(R.id.text_view_p1);
                        TextView textPlayer2 = (TextView) findViewById(R.id.text_view_p2);
                        textPlayer1.setText(playerName + ": " + playersPoint.get(0));
                        textPlayer2.setText(playerOpponent + ": " + playersPoint.get(1));
                    }catch(NullPointerException e){
                        setContentView(R.layout.activity_tris_multiplayer);
                        TextView textPlayer1 = (TextView) findViewById(R.id.text_view_p1);
                        TextView textPlayer2 = (TextView) findViewById(R.id.text_view_p2);
                        textPlayer1.setText(playerName + ": " + playersPoint.get(0));
                        textPlayer2.setText("In attesa di un giocatore...");
                    }
                }
                else if(role.equals("guest")) {
                    playersPoint.add(dataSnapshot.child(playerOpponent).getValue(Integer.class));
                    playersPoint.add(dataSnapshot.child(playerName).getValue(Integer.class));
                    setContentView(R.layout.activity_tris_multiplayer);
                    TextView textPlayer1 = (TextView) findViewById(R.id.text_view_p1);
                    TextView textPlayer2 = (TextView) findViewById(R.id.text_view_p2);
                    textPlayer1.setText(playerName + ": " + playersPoint.get(1));
                    textPlayer2.setText(playerOpponent + ": " + playersPoint.get(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void click(View view) {//se schiaccio il bottone devo cambiare il database [solo se è il mio turno]
        if (role.equals("host")) {
            if (role.equals(turno)) {
                //Turno primo giocatore
                Button vista = (Button) view;
                //andiamo a decrementare tag, perchè gli array partono poi da zero
                int tag = Integer.parseInt((String) view.getTag()) - 1;
                if (tabella.get(tag).equals("vuoto")) {
                    tabella.set(tag, role);
                    vista.setText("X");
                    tabellaRef.setValue(tabella);
                    cambiaTurno();
                } else {
                    //alert--> la cella è già piena
                    AlertDialog alertDialog = new AlertDialog.Builder(TrisMultiplayerActivity.this).create();
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
            } else {

                AlertDialog alertDialog = new AlertDialog.Builder(TrisMultiplayerActivity.this).create();
                alertDialog.setTitle("Mossa non concessa");
                alertDialog.setMessage("Non è ancora il tuo turno");// non è ancora il tuo turno
            }//non è ancora il tuo turno
        } else if (role.equals("guest")) {
            if (role.equals(turno)) {
                //turno secondo giocatore
                Button vista = (Button) view;

                //andiamo a decrementare tag, perchè gli array partono poi da zero
                int tag = Integer.parseInt((String) view.getTag()) - 1;
                if (tabella.get(tag).equals("vuoto")) {
                    tabella.set(tag, role);
                    vista.setText("O");
                    tabellaRef.setValue(tabella);
                    cambiaTurno();
                } else {
                    //alert--> la cella è già piena
                    AlertDialog alertDialog = new AlertDialog.Builder(TrisMultiplayerActivity.this).create();
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
            } else {
                //non è ancora il tuo turno
                AlertDialog alertDialog = new AlertDialog.Builder(TrisMultiplayerActivity.this).create();
                alertDialog.setTitle("Mossa non concessa");
                alertDialog.setMessage("Non è ancora il tuo turno");
            }// non è ancora il tuo turno
        }
    }

    public void reset(View view){
        setContentView(R.layout.activity_tris_multiplayer);

        DatabaseReference room = database.getReference("rooms/" + roomName + "/punteggio");

        room.child(playerName).setValue(0);
        room.child(playerOpponent).setValue(0);

        //svuoto il punteggio dei player
        TextView textPlayer1 = (TextView) findViewById(R.id.text_view_p1);
        TextView textPlayer2 = (TextView) findViewById(R.id.text_view_p2);

        playersPoint.clear();
        playersPoint.add(0);
        playersPoint.add(0);

        if(role.equals("host")){
            punteggioRef.child(playerName).setValue(playersPoint.get(0));
            punteggioRef.child(playerOpponent).setValue(playersPoint.get(1));

            textPlayer1.setText(playerName + ": " + playersPoint.get(0));
            textPlayer2.setText(playerOpponent + ": " + playersPoint.get(1));
        }else if(role.equals("guest")){
            punteggioRef.child(playerName).setValue(playersPoint.get(1));
            punteggioRef.child(playerOpponent).setValue(playersPoint.get(0));

            textPlayer1.setText(playerName + ": " + playersPoint.get(1));
            textPlayer2.setText(playerOpponent + ": " + playersPoint.get(0));
        }

        //devo svuotarlo online

        svuotaTabella();
    }

    //questa funzione controlla la vittoria e cambia il giocatore
    private void controllaVittoria() {
        if (role.equals("host") && ((tabella.get(0).equals(tabella.get(1)) && tabella.get(0).equals(tabella.get(2)) && tabella.get(0).equals(role)) || (tabella.get(3).equals(tabella.get(4)) && tabella.get(3).equals(tabella.get(5)) && tabella.get(3).equals(role)) || (tabella.get(6).equals(tabella.get(7)) && tabella.get(6).equals(tabella.get(8)) && tabella.get(6).equals(role)) ||
                (tabella.get(0).equals(tabella.get(3)) && tabella.get(0).equals(tabella.get(6)) && tabella.get(0).equals(role)) || (tabella.get(1).equals(tabella.get(4)) && tabella.get(1).equals(tabella.get(7)) && tabella.get(1).equals(role)) || (tabella.get(2).equals(tabella.get(5)) && tabella.get(2).equals(tabella.get(8)) && tabella.get(2).equals(role)) ||
                (tabella.get(0).equals(tabella.get(4)) && tabella.get(0).equals(tabella.get(8)) && tabella.get(0).equals(role)) || (tabella.get(2).equals(tabella.get(4)) && tabella.get(2).equals(tabella.get(6)) && tabella.get(2).equals(role)))) {
            aggiungiVittoriaAPlayer(playerName);

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_container));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("This is a custom toast");

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
            return;

        }
        if (role.equals("guest") && ((tabella.get(0).equals(tabella.get(1)) && tabella.get(0).equals(tabella.get(2)) && tabella.get(0).equals(role)) || (tabella.get(3).equals(tabella.get(4)) && tabella.get(3).equals(tabella.get(5)) && tabella.get(3).equals(role)) || (tabella.get(6).equals(tabella.get(7)) && tabella.get(6).equals(tabella.get(8)) && tabella.get(6).equals(role)) ||
                (tabella.get(0).equals(tabella.get(3)) && tabella.get(0).equals(tabella.get(6)) && tabella.get(0).equals(role)) || (tabella.get(1).equals(tabella.get(4)) && tabella.get(1).equals(tabella.get(7)) && tabella.get(1).equals(role)) || (tabella.get(2).equals(tabella.get(5)) && tabella.get(2).equals(tabella.get(8)) && tabella.get(2).equals(role)) ||
                (tabella.get(0).equals(tabella.get(4)) && tabella.get(0).equals(tabella.get(8)) && tabella.get(0).equals(role)) || (tabella.get(2).equals(tabella.get(4)) && tabella.get(2).equals(tabella.get(6)) && tabella.get(2).equals(role)))) {

            aggiungiVittoriaAPlayer(role);

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_container));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("This is a custom toast");

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
            return;
        }
        //controllo le vittorie dell'avversario
        if (role.equals("host") && ((tabella.get(0).equals(tabella.get(1)) && tabella.get(0).equals(tabella.get(2)) && tabella.get(0).equals("guest")) || (tabella.get(3).equals(tabella.get(4)) && tabella.get(3).equals(tabella.get(5)) && tabella.get(3).equals("guest")) || (tabella.get(6).equals(tabella.get(7)) && tabella.get(6).equals(tabella.get(8)) && tabella.get(6).equals("guest")) ||
                (tabella.get(0).equals(tabella.get(3)) && tabella.get(0).equals(tabella.get(6)) && tabella.get(0).equals("guest")) || (tabella.get(1).equals(tabella.get(4)) && tabella.get(1).equals(tabella.get(7)) && tabella.get(1).equals("guest")) || (tabella.get(2).equals(tabella.get(5)) && tabella.get(2).equals(tabella.get(8)) && tabella.get(2).equals("guest")) ||
                (tabella.get(0).equals(tabella.get(4)) && tabella.get(0).equals(tabella.get(8)) && tabella.get(0).equals("guest")) || (tabella.get(2).equals(tabella.get(4)) && tabella.get(2).equals(tabella.get(6)) && tabella.get(2).equals("guest")))) {

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_container));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("This is a custom toast");

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
            svuotaTabella();
            return;

        }
        if (role.equals("guest") && ((tabella.get(0).equals(tabella.get(1)) && tabella.get(0).equals(tabella.get(2)) && tabella.get(0).equals("host")) || (tabella.get(3).equals(tabella.get(4)) && tabella.get(3).equals(tabella.get(5)) && tabella.get(3).equals("host")) || (tabella.get(6).equals(tabella.get(7)) && tabella.get(6).equals(tabella.get(8)) && tabella.get(6).equals("host")) ||
                (tabella.get(0).equals(tabella.get(3)) && tabella.get(0).equals(tabella.get(6)) && tabella.get(0).equals("host")) || (tabella.get(1).equals(tabella.get(4)) && tabella.get(1).equals(tabella.get(7)) && tabella.get(1).equals("host")) || (tabella.get(2).equals(tabella.get(5)) && tabella.get(2).equals(tabella.get(8)) && tabella.get(2).equals("host")) ||
                (tabella.get(0).equals(tabella.get(4)) && tabella.get(0).equals(tabella.get(8)) && tabella.get(0).equals("host")) || (tabella.get(2).equals(tabella.get(4)) && tabella.get(2).equals(tabella.get(6)) && tabella.get(2).equals("host")))) {

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_container));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("This is a custom toast");

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();

            svuotaTabella();
            return;
        }

        //se la tabella.get è piena e nessuno ha vinto allora è pareggio()
        if (!tabella.get(0).equals("vuoto") && !tabella.get(1).equals("vuoto") && !tabella.get(2).equals("vuoto") && !tabella.get(3).equals("vuoto") && !tabella.get(4).equals("vuoto") && !tabella.get(5).equals("vuoto") && !tabella.get(6).equals("vuoto") && !tabella.get(7).equals("vuoto") && !tabella.get(8).equals("vuoto")) {

            pareggio();
            return;
        }
    }

    //fatta
    private void svuotaTabella() {
        tabella.clear();
        for (int i = 0; i < 9; i++) {
            tabella.add("vuoto");
            String buttonID = "button" + (i + 1);
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
        tabellaRef.setValue(tabella);
    }

    //fatta?
    private void pareggio() {
        //svuoto la tabella
        Context context = getApplicationContext();
        CharSequence text = "Pareggio!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
        toast.show();
        svuotaTabella();
    }

    //fatta
    private void aggiungiVittoriaAPlayer(String player) {
        if(role.equals("host")){
            playersPoint.set(0, playersPoint.get(0)+1);
            punteggioRef.child(playerName).setValue(playersPoint.get(0));
        }else if(role.equals("guest")){
            playersPoint.set(1, playersPoint.get(1)+1);
            punteggioRef.child(playerName).setValue(playersPoint.get(1));
        }
    }

    //    public void reset(View view) {
//        setContentView(R.layout.activity_main);
//        /*Ogni volta che vuoi cambiare l'aspetto corrente di un'attività o quando passi da un'attività all'altra,
//         *la nuova attività deve avere un design da mostrare.
//         * Chiamiamo setContentViewonCreate() con il design desiderato come argomento.
//         *
//         * Un’Activity è un’interfaccia utente. Ogni volta che si usa un’app generalmente si interagisce con una o più
//         * “pagine” mediante le quali si consultano dati o si immettono input. Ovviamente la realizzazione di Activity
//         * è il punto di partenza di ogni corso di programmazione Android visto che è il componente con cui l’utente ha
//         * il contatto più diretto.
//         * Le Activity nel loro complesso costituiscono il flusso in cui l’utente si inoltra per sfruttare le funzionalità
//         * messe a disposizione. Questo richiede pertanto non solo la realizzazione delle interfacce in sé stesse, ma anche
//         * una corretta progettazione della navigazione tra di esse offrendo la possibilità di sfogliarne i contenuti e risalirli
//         * gerarchicamente in maniera coerente.
//         * */
//        //svuoto la tabella
//        svuotaTabella();
//
//        //svuoto il punteggio dei player
//        TextView textPlayer1 = (TextView) findViewById(R.id.text_view_p1);
//        TextView textPlayer2 = (TextView) findViewById(R.id.text_view_p2);
//        punteggioPlayer1 = 0;
//        punteggioPlayer2 = 0;
//        textPlayer1.setText(nomeGiocatore1 + ": " + punteggioPlayer1);
//        textPlayer2.setText(nomeGiocatore2 + ": " + punteggioPlayer2);
//        giocatore = "primo";
//        turnoInizio = "primo";
//    }
    private void cambiaTurno() {
        if (role.equals("host")) {
            turno = "guest";
            turnoRef.setValue("guest");
        } else if (role.equals("guest")) {
            turno = "host";
            turnoRef.setValue("host");
        }
    }
}
//
//    private static String PACKAGE_NAME;
//    private String giocatore = "primo";
//    private String turnoInizio = "primo";
//    private String[] tabella = new String[9];
//    private int punteggioPlayer1, punteggioPlayer2 = 0;
//    private String nomeGiocatore1;
//    private String nomeGiocatore2;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        Intent intent = getIntent();
//        this.nomeGiocatore1 = intent.getStringExtra("giocatore1");
//        this.nomeGiocatore2 = intent.getStringExtra("giocatore2");
//        TextView editText1 = (TextView) findViewById(R.id.text_view_p1);
//        TextView editText2 = (TextView) findViewById(R.id.text_view_p2);
//        editText1.setText(this.nomeGiocatore1 + ": " + this.punteggioPlayer1);
//        editText2.setText(this.nomeGiocatore2 + ": " + this.punteggioPlayer2);
//
//        PACKAGE_NAME = getApplicationContext().getPackageName();
//        svuotaTabella();
//    }
