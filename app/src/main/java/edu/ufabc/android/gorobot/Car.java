package edu.ufabc.android.gorobot;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;


/**
 * Created by davi on 23/08/16.
 */

public class Car {
    private Bitmap figuras[];

    private int x;
    private int y;
    private int width;
    private int height;

    private int currentImage=0;

    public Car(AssetManager assets){
        figuras = new Bitmap[4];
        try {
            figuras[0] = BitmapFactory.decodeStream(assets.open("robot0.png"));
            figuras[1] = BitmapFactory.decodeStream(assets.open("robot1.png"));
            figuras[2] = BitmapFactory.decodeStream(assets.open("robot2.png"));
            figuras[3] = BitmapFactory.decodeStream(assets.open("robot3.png"));
            setWidth(figuras[0].getWidth());
            setHeight(figuras[0].getHeight());
            currentImage = 0;
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void update(){
        currentImage = (currentImage+1)%figuras.length;
    }

    public void render(Canvas canvas){
        canvas.drawBitmap(figuras[currentImage], getX(), getY(),null);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}