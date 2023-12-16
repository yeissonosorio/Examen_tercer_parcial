package com.example.examen_tercer_parcial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.examen_tercer_parcial.Modelo.Entrevista;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ListaEntrevista extends AppCompatActivity {

    RecyclerView entrevistados;
    Adapter adp;
    FirebaseFirestore mFirestore;

    int pres=0;

    Button atras,ordenar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_entrevista);
        mFirestore = FirebaseFirestore.getInstance();
        entrevistados=findViewById(R.id.Lista);
        atras=(Button) findViewById(R.id.btnAtras);
        ordenar=(Button) findViewById(R.id.btndenar);
        entrevistados.setLayoutManager(new LinearLayoutManager(this));

        if(getIntent().getStringExtra("ord").equals("")){
        Query query = mFirestore.collection("Entrevistas").orderBy("fecha", Query.Direction.ASCENDING);;
        FirestoreRecyclerOptions<Entrevista> firestoreRecyclerOptions= new FirestoreRecyclerOptions.Builder<Entrevista>().setQuery(query,Entrevista.class).build();
        adp=new Adapter(firestoreRecyclerOptions);
        adp.notifyDataSetChanged();
        entrevistados.setAdapter(adp);
        }
        else {
            Query query = mFirestore.collection("Entrevistas").orderBy("fecha", Query.Direction.DESCENDING);

            FirestoreRecyclerOptions<Entrevista> firestoreRecyclerOptions =
                    new FirestoreRecyclerOptions.Builder<Entrevista>()
                            .setQuery(query, Entrevista.class)
                            .build();
            adp=new Adapter(firestoreRecyclerOptions);
            adp.notifyDataSetChanged();
            entrevistados.setAdapter(adp);
            ordenar.setText("Ver entrevista en orden del mas reciente");
            pres=1;
        }



        adp.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
        public void onItemClick(DocumentSnapshot documentSnapshot, Entrevista entrevista,int position) {
            String documentId = documentSnapshot.getId();

                    Intent intent = new Intent(getApplicationContext(),Datos.class);
                    intent.putExtra("Descripcion",entrevista.getDescripcion());
                    intent.putExtra("fecha",entrevista.getFecha());
                    intent.putExtra("periodista",entrevista.getPeriodista());
                    intent.putExtra("foto",entrevista.getFoto());
                    intent.putExtra("audio",entrevista.getAudio());
                    intent.putExtra("document",documentId);
                    startActivity(intent);
                    finish();

        }
    });

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        ordenar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pres==0) {
                    Intent intent = new Intent(getApplicationContext(), ListaEntrevista.class);
                    intent.putExtra("ord", "des");
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), ListaEntrevista.class);
                    intent.putExtra("ord", "");
                    startActivity(intent);
                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        adp.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adp.stopListening();
    }
}