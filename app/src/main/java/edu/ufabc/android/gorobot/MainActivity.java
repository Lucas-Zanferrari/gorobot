package edu.ufabc.android.gorobot;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static int ENABLE_BLUETOOTH = 1;
    public static int SELECT_PAIRED_DEVICE = 2;
    public static int SELECT_DISCOVERED_DEVICE = 3;
    private static final String TAG="MAIN_ACTIVITY";
    static TextView statusMessage;
    static TextView textSpace;
    ConnectionThread connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusMessage = (TextView) findViewById(R.id.statusMessage);
        textSpace = (TextView) findViewById(R.id.textSpace);

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            statusMessage.setText("Não foi encontrado Hardware Bluetooth ou não é funcional");
        } else {
            statusMessage.setText("Harware Bluetooth funcional");
            if(!btAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH);
                statusMessage.setText("Solicitando ativação do Bluetooth...");
            } else {
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

        connect.write(data);

        //delay to make enough time to Arduino receive the strings
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    connect.write(data2);

            }
        }, 2000);

    }
    //reset command to stop the robot
    public void reset(View view){
        String resetCommand = "r";
        byte[] data =  resetCommand.getBytes();
        connect.write(data);
    }

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

    public void openMap(View view){
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
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
        Log.d(TAG, "onStart");
    }

    public void onResume(){
        super.onResume();
        Log.d(TAG, "OnResume");
    }

    public void onPause(){
        super.onPause();
        Log.d(TAG, "OnPause");
    }

    public void onStop(){
        super.onStop();
        Log.d(TAG, "OnStop");
    }

    public void onRestart(){
        super.onRestart();
        Log.d(TAG, "OnRestart");
    }

    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "OnDestroy");
    }
}