package com.example.beto.notas.activities;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.beto.notas.R;
import com.example.beto.notas.adapters.NoteAdapter;
import com.example.beto.notas.models.Board;
import com.example.beto.notas.models.Note;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

public class NoteActivity extends AppCompatActivity implements RealmChangeListener<Board>{

    /** Creado por:
     // Alberto Ángel Cruz Alvarado
     // Aguilar Hernández María Teresa
     // Rojas Trujillo David**/
    private ListView listView;
    private FloatingActionButton fab;

    private NoteAdapter adapter;
    private RealmList<Note> notes;
    private Realm realm;

    private int boardId;
    private Board board;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Toast.makeText(getApplicationContext(), "Creado por Alberto Ángel Cruz Alvarado", Toast.LENGTH_LONG).show();


        realm = Realm.getDefaultInstance();

        if (getIntent().getExtras() != null)
            boardId = getIntent().getExtras().getInt("id");

        board = realm.where(Board.class).equalTo("id", boardId).findFirst();
        board.addChangeListener(this);
        notes = board.getNotes();


        this.setTitle(board.getTitle());

        fab = (FloatingActionButton) findViewById(R.id.fabAddNote);
        listView = (ListView) findViewById(R.id.listViewNote);
        adapter= new NoteAdapter(this, notes, R.layout.list_view_note_item);

        listView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertForCreatingNote("Agregando nota", "Ingresa una nota para " + board.getTitle());

            }
        });

    }

    //**Dialogs**//
    private void showAlertForCreatingNote(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_note, null);
        builder.setView(viewInflated);

        final EditText input = (EditText) viewInflated.findViewById(R.id.editTextNewNote);

        builder.setPositiveButton("Agregar nota", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String note = input.getText().toString().trim();

                if (note.length() > 0)
                    createNewNote(note);
                else
                    Toast.makeText(getApplicationContext(), "¡¡EL NOMBRE DE LA NOTA NO PUDE SER VACÍO!!", Toast.LENGTH_LONG).show();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();



    }

    //CRUD
    private void createNewNote(String note){

        realm.beginTransaction();

        Note _note = new Note(note);
        realm.copyToRealm(_note);
        board.getNotes().add(_note);

        realm.commitTransaction();

    }

    @Override
    public void onChange(Board element) {
        adapter.notifyDataSetChanged();
    }
}
