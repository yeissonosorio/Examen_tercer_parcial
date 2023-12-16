package com.example.examen_tercer_parcial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

public class Datos extends AppCompatActivity {

    ImageView imagen;
    String audio,foto,descripcion,fecha,periodista,documento;

    Button reproducir,editar,volver;
    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer;

    int toke=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos);
        imagen=(ImageView) findViewById(R.id.imageView2);
        volver=(Button)findViewById(R.id.btnvolver);
        audio=getIntent().getStringExtra("audio");
        foto=getIntent().getStringExtra("foto");
        descripcion=getIntent().getStringExtra("Descripcion");
        fecha=getIntent().getStringExtra("fecha");
        periodista=getIntent().getStringExtra("periodista");
        documento=getIntent().getStringExtra("document");
        byte[] bytes = Base64.decode(foto, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imagen.setImageBitmap(bitmap);


        editar=(Button) findViewById(R.id.btneditar);
        reproducir=(Button) findViewById(R.id.playButton);

        reproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if(toke<1) {
                        reproducirAudioDesdeBase64();
                        toke=1;
                    }


            }
        });

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Editar.class);
                intent.putExtra("des",descripcion);
                intent.putExtra("pe",periodista);
                intent.putExtra("fe",fecha);
                intent.putExtra("foto",foto);
                intent.putExtra("au",audio);
                intent.putExtra("doc",documento);
                startActivity(intent);
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ListaEntrevista.class);
                intent.putExtra("ord","");
                startActivity(intent);
            }
        });

    }

    private void reproducirAudioDesdeBase64() {
        try {
            byte[] audioData = Base64.decode(audio, Base64.DEFAULT);

            // Almacenar el archivo temporalmente para reproducirlo
            String tempFileName = "temp_audio.3gp";
            FileOutputStream fos = openFileOutput(tempFileName, MODE_PRIVATE);
            fos.write(audioData);
            fos.close();

            // Reproducir el archivo de audio decodificado
            reproducirAudio(tempFileName);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al reproducir audio", Toast.LENGTH_SHORT).show();
        }
    }
    private void reproducirAudio(String fileName) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getApplicationContext().getFilesDir().getPath() + "/" + fileName);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    toke=0;
                }
            });
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al reproducir audio", Toast.LENGTH_SHORT).show();
        }
    }
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}