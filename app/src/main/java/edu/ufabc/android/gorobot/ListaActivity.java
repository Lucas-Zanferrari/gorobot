package edu.ufabc.android.gorobot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;


public class ListaActivity extends AppCompatActivity {

    private static final String MyPREFERENCES = "MyPrefs" ;
    private static final String LAT = "latKey";
    private static final String LNG = "lngKey";
    private static final String TAG = "LIST_ACTIVITY";
    private static final int GET_COORDINATES = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ListView listLocais = (ListView) findViewById(R.id.listLocais);
        ArrayList<String> arrayLocais = new ArrayList<String>();

        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        int inputCount = sharedpreferences.getInt("inputCount", 0);

        for (int i = inputCount-1; i >= 0; i--) {
            arrayLocais.add("Latitude: " + sharedpreferences.getString(LAT + i, null) +
                    "\nLongitude: " + sharedpreferences.getString(LNG + i, null));
        }

        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,           // activity que contém a listView
                android.R.layout.simple_list_item_1,                              // estilo dos itens
                arrayLocais);                                                     // a lista de elementos

        listLocais.setAdapter(adaptador);

        listLocais.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // são 4 parâmetros
            // 1- o adaptador da lista que contém os elementos
            // 2- o eventual componente visual que contém esta lista
            // 3- a posicao clicada
            // 4- o id do objeto que foi clicado (geralmente segue o padrão da posicao)
            public void onItemClick(AdapterView<?> adaptador, View v, int pos, long id) {
                Toast.makeText(ListaActivity.this, "Lugar número "+pos, Toast.LENGTH_LONG).show();
            }
        });

        Log.d(TAG, "OnCreate");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_COORDINATES && resultCode == RESULT_OK){
            Intent output = new Intent(this, MainActivity.class);
            String latitude = data.getStringExtra("LATITUDE");
            String longitude = data.getStringExtra("LONGITUDE");
            output.putExtra("LATITUDE", latitude);
            output.putExtra("LONGITUDE", longitude);
            setResult(RESULT_OK, output);
            ListaActivity.this.finish();
        }
    }

    public void openMap(View view){
        Intent intent = new Intent(ListaActivity.this, MapsActivity.class);
        startActivityForResult(intent, GET_COORDINATES);
    }

    public void openBluetoothConf(View view){
        setResult(RESULT_CANCELED);
        ListaActivity.this.finish();
    }

    public void clearList(View view){
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefsEditor = sharedpreferences.edit();
        sharedPrefsEditor.clear();
        sharedPrefsEditor.apply();

        ListView listLocais = (ListView) findViewById(R.id.listLocais);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                new ArrayList<String>());

        listLocais.setAdapter(adaptador);
        Toast.makeText(ListaActivity.this, "Lista de locais apagada com sucesso.", Toast.LENGTH_LONG).show();
    }

    public void onStart(){
        super.onStart();
        Log.d(TAG, "OnStart");
    }

    public void onResume() {
        super.onResume();
        Log.d(TAG, "OnResume");
    }

    public void onPause() {
        super.onPause();
        Log.d(TAG, "OnPause");
    }

    public void onStop() {
        super.onStop();
        Log.d(TAG, "OnStop");
    }

    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "OnRestart");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "OnDestroy");
    }
}