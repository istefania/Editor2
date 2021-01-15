package com.example.editor2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    ImageView imageView;
    private int PICK_IMAGE_REQUEST = 1;

    Bitmap bitmap;

    BottomNavigationView bottomNavigationView;

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHash;


    private TextView textViewContrast;
    private SeekBar seekBarContrast;
    private TextView textViewBrightness;
    private SeekBar seekBarBrightness;
    private TextView textViewSaturation;
    private SeekBar seekBarSaturation;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    Button apply;



    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.image_view);

        final Button saveImage = findViewById(R.id.save_image_btn);
        apply = (Button) findViewById(R.id.apply_button);

        textViewContrast=(TextView)findViewById(R.id.textView_contrast);
        seekBarContrast=(SeekBar)findViewById(R.id.seekBar_contrast);

        textViewBrightness=(TextView)findViewById(R.id.textView_brightness);
        seekBarBrightness=(SeekBar)findViewById(R.id.seekBar_brightness);

        textViewSaturation=(TextView)findViewById(R.id.textView_saturation);
        seekBarSaturation=(SeekBar)findViewById(R.id.seekBar_saturation);


        drawerLayout=findViewById(R.id.drawer);
        toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawerOpen,R.string.drawerClosed);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //get the listview
        listView = (ExpandableListView)findViewById(R.id.expandable_list);

        //preparing list data
        initData();
        listAdapter= new ExpandableListAdapter(this,listDataHeader,listHash);

        //setting list adapter
        listView.setAdapter(listAdapter);

        bottomNavigationView=(BottomNavigationView)findViewById(R.id.bottom_menu_id);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        seekBarContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarContrast.setProgress(progress);
                textViewContrast.setText(""+(progress*100)/255+"%");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });


        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarBrightness.setProgress(progress);
                textViewBrightness.setText(""+(progress*100)/255+"%");
                SetContrast(bitmap, progress);
                ImageView imageView = findViewById(R.id.image_view);
                imageView.setImageBitmap(bitmap);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        seekBarSaturation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarSaturation.setProgress(progress);
                textViewSaturation.setText(""+progress+"%");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });


       /* saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageView.getDrawable() == null) {

                    Toast.makeText(MainActivity.this, "Pick a photo", Toast.LENGTH_SHORT).show();

                } else {
                    imageView.setDrawingCacheEnabled(true);
                    Bitmap bmap = imageView.getDrawingCache();
                    saveImage(bmap,"000");


                    Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                }
    }

        });*/



        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(bitmap!=null) {

                    int contrast_value = seekBarContrast.getProgress();
                    int brightness_value = seekBarBrightness.getProgress();
                    int saturation_value = seekBarSaturation.getProgress();
                    Bitmap bitmap1 = SetContrast(bitmap, contrast_value);
                    bitmap1 = SetBrightness(bitmap1, brightness_value);
                    /*bitmap1 = SetSaturation(bitmap1, saturation_value);
                    ImageView imageView = findViewById(R.id.image_view);*/
                    imageView.setImageBitmap(bitmap1);
                    drawerLayout.closeDrawers();
                }
                else{

                    Toast.makeText(getApplicationContext(), "Choose Image!",
                            Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    private void initData() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<String,List<String>>();

        listDataHeader.add("Rotate");
        listDataHeader.add("Flip");

        List<String> flip = new ArrayList<>();
        flip.add("Horizontally");
        flip.add("Vertically");

        List<String> rotate= new ArrayList<>();
        rotate.add("Left");
        rotate.add("Right");


        listHash.put(listDataHeader.get(0),flip);
        listHash.put(listDataHeader.get(1),rotate);

    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ImageView imageView = findViewById(R.id.image_view);
                    imageView.setImageBitmap(bitmap);


                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public Bitmap SetContrast(Bitmap src, double value) {
        // src image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap with original size
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int) (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (R < 0) {
                    R = 0;
                } else if (R > 255) {
                    R = 255;
                }

                G = Color.green(pixel);
                G = (int) (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (G < 0) {
                    G = 0;
                } else if (G > 255) {
                    G = 255;
                }

                B = Color.blue(pixel);
                B = (int) (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (B < 0) {
                    B = 0;
                } else if (B > 255) {
                    B = 255;
                }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }
    public Bitmap SetBrightness(Bitmap src, int value) {

        // original image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                // increase/decrease each channel
                R += value;
                if (R > 255) {
                    R = 255;
                } else if (R < 0) {
                    R = 0;
                }

                G += value;
                if (G > 255) {
                    G = 255;
                } else if (G < 0) {
                    G = 0;
                }

                B += value;
                if (B > 255) {
                    B = 255;
                } else if (B < 0) {
                    B = 0;
                }

                // apply new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }
    @SuppressLint("Range")
    public Bitmap SetSaturation(Bitmap source, int level) {
        // get original image size
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        float[] HSV = new float[100];
        // get pixel array from source image
        source.getPixels(pixels, 0, width, 0, 0, width, height);

        int index = 0;
        // iteration through all pixels
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // convert to HSV
                Color.colorToHSV(pixels[index], HSV);
                // increase Saturation level
                HSV[1] *= level;
                HSV[1] = (float) Math.max(0.0, Math.min(HSV[1], 1.0));
                // take color back
                pixels[index] = Color.HSVToColor(HSV);
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }


    public Bitmap RotateImage(Bitmap src, float degree) {
        // create new matrix object
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(degree);
        // return new bitmap rotated using matrix
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    private void saveImage(Bitmap finalBitmap, String image_name) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + image_name+ ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.choose_photo_id:
                chooseImage();
                break;
            case R.id.save_photo_id:
                if (imageView.getDrawable() == null) {

                    Toast.makeText(MainActivity.this, "Pick a photo", Toast.LENGTH_SHORT).show();

                } else {
                    imageView.setDrawingCacheEnabled(true);
                    Bitmap bmap = imageView.getDrawingCache();
                    saveImage(bmap,"000");
                    Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.undo_id:
                break;
        }
        return false;
    }

}
