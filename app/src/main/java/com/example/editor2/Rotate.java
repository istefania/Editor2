package com.example.editor2;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class Rotate {

    public Bitmap RotateImage(Bitmap src, float degree) {
        // create new matrix object
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(degree);
        // return new bitmap rotated using matrix
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
}
