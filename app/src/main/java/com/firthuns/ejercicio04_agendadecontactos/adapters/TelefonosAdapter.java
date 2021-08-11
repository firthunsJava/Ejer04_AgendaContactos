package com.firthuns.ejercicio04_agendadecontactos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firthuns.ejercicio04_agendadecontactos.modelos.Telefono;

import java.util.List;

public class TelefonosAdapter extends ArrayAdapter<Telefono> {

    private Context context;
    private List<Telefono> objects;
    private int resource;

    public TelefonosAdapter(@NonNull Context context, int resource, @NonNull List<Telefono> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.resource = resource;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View fila = LayoutInflater.from(context).inflate(resource, null);

        TextView txtNombre = fila.findViewById(android.R.id.text1);
        TextView txtNumTel = fila.findViewById(android.R.id.text2);

        txtNombre.setText(objects.get(position).getNombre());
        txtNumTel.setText(objects.get(position).getTelefono());

        return fila;
    }
}
