package edu.ufabc.android.gorobot;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static int ENABLE_BLUETOOTH = 1;
    private static int SELECT_PAIRED_DEVICE = 2;
    private static int SELECT_DISCOVERED_DEVICE = 3;
    private static final String MyPREFERENCES = "MyPrefs" ;
    private static final String LAT = "latKey";
    private static final String LNG = "lngKey";
    private static final String TAG = "MAIN_ACTIVITY";
    private static TextView statusMessage;
    private static TextView textSpace;
    private ConnectionThread connect;

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString= new String(data);

            if(dataString.equals("---N"))
                statusMessage.setText("Ocorreu um erro durante a conexão");
            else if(dataString.equals("---S"))
                statusMessage.setText("Conectado");
            else {
                textSpace.setText(new String(data));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusMessage = (TextView) findViewById(R.id.statusMessage);
        textSpace = (TextView) findViewById(R.id.textSpace);

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            statusMessage.setText("Não foi encontrado Hardware Bluetooth ou não é funcional");
        }
        else {
            statusMessage.setText("Hardware Bluetooth funcional");
            if(!btAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH);
                statusMessage.setText("Solicitando ativação do Bluetooth...");
            }
            else {
                statusMessage.setText("Bluetooth já ativado");
            }
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ENABLE_BLUETOOTH) {
            if(resultCode == RESULT_OK) {
                statusMessage.setText("Bluetooth ativado");
            }
            else {
                statusMessage.setText("Bluetooth não ativado");
            }
        }
        else if(requestCode == SELECT_PAIRED_DEVICE || requestCode == SELECT_DISCOVERED_DEVICE) {
            if(resultCode == RESULT_OK) {
                statusMessage.setText("Você selecionou " + data.getStringExtra("btDevName") + "\n"
                        + data.getStringExtra("btDevAddress"));

                connect = new ConnectionThread(data.getStringExtra("btDevAddress"));
                connect.start();
            }
            else {
                statusMessage.setText("Nenhum dispositivo selecionado");
            }
        }
    }

    public void searchPairedDevices(View view) {
        Intent searchPairedDevicesIntent = new Intent(this, PairedDevices.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_PAIRED_DEVICE);
    }

    public void discoverDevices(View view) {
        Intent searchPairedDevicesIntent = new Intent(this, DiscoveredDevices.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_DISCOVERED_DEVICE);
    }

    public void enableVisibility(View view) {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 30);
        startActivity(discoverableIntent);
    }

//    public void waitConnection(View view) {
//
//        connect = new ConnectionThread();
//        connect.start();
//    }

    public void sendMessage(View view) {
        EditText messageBox = (EditText) findViewById(R.id.editText_MessageBox);
        String messageBoxString = messageBox.getText().toString();
        byte[] data =  messageBoxString.getBytes();
        EditText messageBox2 = (EditText) findViewById(R.id.editText_MessageBox2);
        String messageBoxString2 = messageBox2.getText().toString();
        final byte[] data2 =  messageBoxString2.getBytes();

        saveInSharedPreferences(messageBoxString, messageBoxString2);
        connect.write(data);

        //delay to make enough time to Arduino receive the strings
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    connect.write(data2);

            }
        }, 2000);
    }

    public void saveInSharedPreferences(String lat, String lng){
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefsEditor = sharedpreferences.edit();
        int inputCount = sharedpreferences.getInt("inputCount", 0);

        sharedPrefsEditor.putString(LAT+inputCount, lat);
        sharedPrefsEditor.putString(LNG+inputCount, lng);
        inputCount++;
        sharedPrefsEditor.putInt("inputCount", inputCount);
        sharedPrefsEditor.commit();
    }

    //reset command to stop the robot
    public void reset(View view){
        String resetCommand = "r";
        byte[] data =  resetCommand.getBytes();
        connect.write(data);
    }

    public void openMap(View view){
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    public void openListOfLastLocations(View view){
        Intent intent = new Intent(MainActivity.this, ListActivity.class);
        startActivity(intent);
    }

    public void onStart(){
        super.onStart();
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            Double latitude = extras.getDouble("LATITUDE");
            Double longitude = extras.getDouble("LONGITUDE");
            EditText messageBox = (EditText) findViewById(R.id.editText_MessageBox);
            EditText messageBox2 = (EditText) findViewById(R.id.editText_MessageBox2);
            messageBox.setText(latitude.toString());
            messageBox2.setText(longitude.toString());
        }
    }

}