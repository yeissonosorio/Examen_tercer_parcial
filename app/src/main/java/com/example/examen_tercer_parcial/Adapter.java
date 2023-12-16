package com.example.examen_tercer_parcial;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.examen_tercer_parcial.Modelo.Entrevista;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class Adapter extends FirestoreRecyclerAdapter<Entrevista,Adapter.ViewHolder>{

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public Adapter(@NonNull FirestoreRecyclerOptions<Entrevista> options) {
        super(options);
    }

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, Entrevista entrevista, int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Entrevista model) {
        // Tu lógica de vinculación de datos

        holder.Descripcion.setText(model.getDescripcion());
        holder.periodista.setText(model.getPeriodista());
        holder.fecha.setText(model.getFecha());
        byte[] bytes = Base64.decode(model.getFoto(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        holder.imagen.setImageBitmap(bitmap);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(getSnapshots().getSnapshot(position), model, position);
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item,parent,false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Descripcion,fecha,periodista;
        ImageView imagen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Descripcion=itemView.findViewById(R.id.textView1);
            fecha=itemView.findViewById(R.id.textView2);
            periodista=itemView.findViewById(R.id.textView3);
            imagen=itemView.findViewById(R.id.imageView1);

        }
    }
}
