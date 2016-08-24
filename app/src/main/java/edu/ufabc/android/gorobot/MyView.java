package edu.ufabc.android.gorobot;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by root on 28/07/16.
 */
public class MyView extends View {



        private Car car;
        private RefreshThread thread;
        AssetManager assetManager;

        public MyView(Context ctx){
            super(ctx);
            // assetManager é o elemento que aponta para o sistema de arquivos
            // /assets
            assetManager = ctx.getAssets();

            car = new Car(assetManager);
            car.setX(175);
            car.setY(175);
            thread = new RefreshThread();
            thread.start();
        }

        /*
        método callback para efetivamente realizar o desenho na interface
        android
         */
        public void onDraw(Canvas canvas){
            car.render(canvas);

        }

        public void update(){
            car.update();

        }
        /*
        Esta thread é responsável por exclusivamente mandar renovar a
        interface a cada 50ms
        ela tem apenas um loop infinito que invoca a invalidação da interface
        forçando a chamada do método onDraw
         */
        private class RefreshThread extends Thread{


            public void run(){
                try{
                    while(true){
                        MyView.this.update();
                        postInvalidate();
                        Thread.sleep(500);
                    }

                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

