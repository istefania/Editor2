package com.example.editor2;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class Flip {

    public Bitmap FlipImage(Bitmap src, int type) {
        // create new matrix for transformation
        Matrix matrix = new Matrix();
        // if vertical
        if (type == 1) {
            // y = y * -1
            matrix.preScale(1.0f, -1.0f);
        }
        // if horizonal
        else if (type == 2) {
            // x = x * -1
            matrix.preScale(-1.0f, 1.0f);
            // unknown type
        }else {
            return null;
        }

        // return transformed image
        Bitmap flippedBitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return flippedBitmap;
    }
}
