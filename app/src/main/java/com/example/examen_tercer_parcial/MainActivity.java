package com.example.examen_tercer_parcial;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Bitmap image;

    private FirebaseFirestore mfirebase;
    static final int peticion_acceso_camara = 101;
    static final int peticion_toma_fotografica = 102;
    String imagenconver="",fecha;

    ImageView imageView;
    int years,mes,dia;

    Button tomar,grabar,reproducir,Guardar,Lista;
    EditText Descripcion,Pediorista,Fecha;

    int toke=0;

    private static final int REQUEST_PERMISSION_CODE = 100;
    private MediaRecorder mediaRecorder;
    private String outputFile;
    private boolean isRecording = false;
    private MediaPlayer mediaPlayer;
    private String audioBase64;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mfirebase = FirebaseFirestore.getInstance();
        imageView= (ImageView) findViewById(R.id.imageView);

        tomar=(Button) findViewById(R.id.btnAgregar);
        Guardar=(Button)findViewById(R.id.btnGuardar);
        grabar=(Button) findViewById(R.id.btnGrabaraudio);
        reproducir=(Button) findViewById(R.id.btnReproducir);
        Descripcion=(EditText) findViewById(R.id.txtdescripcion);
        Pediorista=(EditText) findViewById(R.id.txtPediorista);
        Fecha=(EditText) findViewById(R.id.txtfecha);
        Lista=(Button) findViewById(R.id.btnLista);

        LocalDate fech = LocalDate.now();
        years = fech.getYear();
        mes = fech.getMonthValue();
        dia = fech.getDayOfMonth();
        fecha=String.format(dia + "/" + (mes) + "/" + years);


        Fecha.setText(fecha);
        Fecha.setEnabled(false);

        tomar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permisos();
            }
        });

        grabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    checkPermissionsAndStartRecording();
                } else {
                    stopRecording();
                }
            }
        });

        outputFile = getExternalFilesDir(null).getAbsolutePath() + "/audio_record.3gp";

        reproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(audioBase64==null){
                    Toast.makeText(getApplicationContext(),"Por favor Grabe un audio",Toast.LENGTH_SHORT).show();
                }else {
                    if(toke<1) {
                        reproducirAudioDesdeBase64();
                        toke=1;
                    }
                }
            }
        });

        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(veri()==true) {
                    postEntrevista();
                }

            }
        });

        Lista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),ListaEntrevista.class);
                intent.putExtra("ord","");
                startActivity(intent);
            }
        });
    }

    private void postEntrevista() {
        Map<String, Object>map=new HashMap<>();
        map.put("periodista",Pediorista.getText().toString());
        map.put("descripcion",Descripcion.getText().toString());
        map.put("fecha",Fecha.getText().toString());
        map.put("foto",imagenconver);
        map.put("audio",audioBase64);

        mfirebase.collection("Entrevistas").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(),"Entrevista Guardada",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Error al ingresar ",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void permisos() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},peticion_acceso_camara);
        }
        else
        {
            TomarFoto();
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == peticion_acceso_camara)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                TomarFoto();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Permiso denegado", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void TomarFoto()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!= null)
        {
            startActivityForResult(intent, peticion_toma_fotografica);
        }
    }

    protected void onActivityResult(int requescode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requescode, resultCode, data);

        if (requescode==peticion_toma_fotografica && resultCode== RESULT_OK){
            Bundle extras = data.getExtras();
            image = (Bitmap) extras.get("data");
            imageView.setImageBitmap(image);
        } else if (resultCode==RESULT_OK) {
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imagearray = byteArrayOutputStream.toByteArray();
        imagenconver = Base64.encodeToString(imagearray, Base64.DEFAULT);
    }

    private void checkPermissionsAndStartRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED|| ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
            startRecording();
        } else {
            startRecording();

        }
    }
    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(outputFile);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
        Toast.makeText(this, "Grabación iniciada", Toast.LENGTH_SHORT).show();
        grabar.setText("Detener Grabación");
        isRecording = true;
    }

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        grabar.setText("Iniciar Grabación");
        isRecording = false;
        convertAudioToBase64();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaRecorder != null) {
            stopRecording();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void convertAudioToBase64() {
        try {
            File audioFile = new File(outputFile);
            FileInputStream inputStream = new FileInputStream(audioFile);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            byte[] audioBytes = outputStream.toByteArray();
            audioBase64 = Base64.encodeToString(audioBytes, Base64.DEFAULT);

            // Aquí puedes usar la cadena audioBase64 como desees (por ejemplo, enviarla a un servidor)
            Toast.makeText(this, "Audio Guardado", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al convertir audio a Base64", Toast.LENGTH_SHORT).show();
        }
    }

    private void reproducirAudioDesdeBase64() {
        try {
            byte[] audioData = Base64.decode(audioBase64, Base64.DEFAULT);

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
    private boolean veri() {
        boolean valor=false;
        String des = Descripcion.getText().toString().replaceAll("\\s","");
        String ped =Pediorista.getText().toString().replaceAll("\\s","");
        String fec = Fecha.getText().toString().replaceAll("\\s","");

        if(des.isEmpty()&&ped.isEmpty()&&fec.isEmpty()&&imagenconver.isEmpty()&&audioBase64==null){
            Toast.makeText(getApplicationContext(),"LLene todos los campos",Toast.LENGTH_LONG).show();
        } else if (des.isEmpty()) {
            Descripcion.setError("Debe llenar este campo");
        } else if (ped.isEmpty()) {
            Pediorista.setError("Debe llenar este campo");
        }
        else if (fec.isEmpty()) {
            Fecha.setError("Debe llenar este campo");
        }
        else if (imagenconver.isEmpty()) {
            Toast.makeText(getApplicationContext(),"Tome una foto",Toast.LENGTH_LONG).show();
        }
        else if (audioBase64==null) {
            Toast.makeText(getApplicationContext(),"Grave el audio",Toast.LENGTH_LONG).show();
        }
        else {
            valor = true;
        }
        return valor;
    }
}