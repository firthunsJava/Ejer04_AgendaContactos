package com.firthuns.ejercicio04_agendadecontactos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.firthuns.ejercicio04_agendadecontactos.adapters.ContactosAdapter;
import com.firthuns.ejercicio04_agendadecontactos.configuraciones.Configuracion;
import com.firthuns.ejercicio04_agendadecontactos.modelos.Contacto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private final int EDIT_CONTACTO = 12;
    private final int ADD_CONTACTO = 11;
    // Mostar la lista de contactos
    // 1. ListView y Adapter
    private ListView contenedor;
    private ContactosAdapter contactosAdapter;
    // 2. Colección de Objetos
    private ArrayList<Contacto> contactos;
    // 3. Elemento a replicar
    private int resource;
// guardar datos 'persistencia'
//    private SharedPreferences sharedPreferences;
//    private Gson parser;

    // integrando FIREBASE

    private FirebaseDatabase database;
    private DatabaseReference refContactos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    //    sharedPreferences = getSharedPreferences(Configuracion.SP_LISTA, MODE_PRIVATE);
        database = FirebaseDatabase.getInstance();

        contenedor = findViewById(R.id.contendorContactosMain);
        resource = R.layout.contacto_fila;



// -----Zona donde controlo la persistencia de los datos ***********
  //      String listaCodificada = sharedPreferences.getString(Configuracion.L_contactos, null);
//        if (listaCodificada == null)   contactos = new ArrayList<>();
//        else{
//            parser = new Gson();
//            contactos = parser.fromJson(listaCodificada, new TypeToken< ArrayList<Contacto> >(){}.getType());
//        }

        if (contactos == null) {
          contactos = new ArrayList<>();
        }

        refContactos = database.getReference(Configuracion.REF_CONTACTOS);
        refContactos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    GenericTypeIndicator<ArrayList<Contacto>> gtiContacto = new GenericTypeIndicator<ArrayList<Contacto>>() {};

                    contactos.clear();
                    contactos.addAll(snapshot.getValue(gtiContacto));
                    contactosAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        contactosAdapter = new ContactosAdapter(this, resource, contactos);
        contenedor.setAdapter(contactosAdapter); // NOTIFICO LOS CAMBIOS REALIZADOS.

//  -----Zona FIN donde controlo la persistencia de los datos ***********

        contenedor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt("POS", position);
                bundle.putParcelable("CONTACTO", contactos.get(position));
                Intent intent = new Intent(MainActivity.this, EditContactoActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, EDIT_CONTACTO);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult
                        (new Intent(MainActivity.this, AddContactoActivity.class),
                                ADD_CONTACTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 1º CONTROLAMOS QUE resultCode == RESULT_OK
        if (resultCode == RESULT_OK){

            switch (requestCode){
                case ADD_CONTACTO:
                    if (data!= null && data.getExtras() != null){
                        Contacto contacto = data.getExtras().getParcelable("CONTACTO");
                        if (contacto != null){
// Metodo que me devuelva una posicion y se lo asigne al contacto, y asi a cada ocasion se me va ordenando.
                            int posicion = ordenaArray(contacto);
                              contactos.add(posicion, contacto);
//                            ordenaArray2();
//                            contactos.add(contacto);

                            contactosAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case EDIT_CONTACTO:
                    if (data != null && data.getExtras() != null){
                        int posicion = data.getExtras().getInt("POS");
                        Contacto contacto = data.getExtras().getParcelable("CONTACTO");
                        if (contacto == null){
                            contactos.remove(posicion);
                        }
                        else{
                            contactos.set(posicion, contacto);
                        }
                        contactosAdapter.notifyDataSetChanged();
                    }
                    break;
            }
// GUARDAR LOS DATOS 'PERSISTENCIA'
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            String listaCodificada = new Gson().toJson(contactos);
//            editor.putString(Configuracion.L_contactos, listaCodificada);
//            editor.apply();
            refContactos.setValue(contactos);


        }
    }

    private int ordenaArray(Contacto contacto) {
        int posicion;
        for(posicion = 0; posicion < contactos.size(); posicion++){
            if (contactos.get(posicion).getNombre().compareToIgnoreCase(contacto.getNombre()) > 0)
                return posicion;
        }
        return posicion;
    }

//    private void ordenaArray2(){
//
//        Collections.sort(contactos);
//            }
}