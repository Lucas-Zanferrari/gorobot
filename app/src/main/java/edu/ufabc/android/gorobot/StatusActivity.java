package edu.ufabc.android.gorobot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Created by davi on 23/08/16.
 */
public class StatusActivity extends Activity {

    private class SplashThread extends Thread{
        public void run(){
            try{
                Thread.sleep(4000);
                Intent intent = new Intent(StatusActivity.this, MainActivity.class);
                startActivity(intent);
                StatusActivity.this.finish();

            }
            catch (Exception ex){
                Toast.makeText(StatusActivity.this, "Erro.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    MyView myView;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        new SplashThread().start();
        myView = new MyView(this);
       setContentView(myView);
    }
}
