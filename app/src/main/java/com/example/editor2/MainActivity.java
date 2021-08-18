package com.example.editor2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    ImageView imageView;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri imgUri;
    Bitmap bitmap;
    ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();

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

    FirebaseStorage storage;
    StorageReference storageReference;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.image_view);

        apply = (Button) findViewById(R.id.apply_button);

        textViewContrast=(TextView)findViewById(R.id.textView_contrast);
        seekBarContrast=(SeekBar)findViewById(R.id.seekBar_contrast);

        textViewBrightness=(TextView)findViewById(R.id.textView_brightness);
        seekBarBrightness=(SeekBar)findViewById(R.id.seekBar_brightness);

        textViewSaturation=(TextView)findViewById(R.id.textView_saturation);
        seekBarSaturation=(SeekBar)findViewById(R.id.seekBar_saturation);

        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

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

        listView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                final String selected = (String) listAdapter.getChild(
                        groupPosition, childPosition);
               if(groupPosition==0) {

                   if(childPosition==0)
                   {
                       bitmap=FlipImage(bitmap,1);
                       imageView.setImageBitmap(bitmap);
                       drawerLayout.closeDrawers();
                   }
                   else
                   {
                       bitmap=FlipImage(bitmap,2);
                       imageView.setImageBitmap(bitmap);
                       drawerLayout.closeDrawers();
                   }
               }

               if(groupPosition==1){
                   if(childPosition==0)
                   {
                       bitmap=RotateImage(bitmap,90);
                       imageView.setImageBitmap(bitmap);
                       drawerLayout.closeDrawers();
                   }
                   else{
                       bitmap= RotateImage(bitmap,-90);
                       imageView.setImageBitmap(bitmap);
                       drawerLayout.closeDrawers();
                   }
               }

                   Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG)
                           .show();

                return true;

            }
        });

        bottomNavigationView=(BottomNavigationView)findViewById(R.id.bottom_menu_id);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

            }
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

            }
        }



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
                    bitmapArray.add(bitmap1);
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

    private void saveInFirebase(){
        if (imageView.getDrawable()!=null) {

            StorageReference reference=storageReference.child("Picture/"+ UUID.randomUUID().toString());

            try{
            reference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this, "Saved successfully",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Upload failed",Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imgUri = data.getData();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                    ImageView imageView = findViewById(R.id.image_view);
                    imageView.setImageBitmap(bitmap);
                    bitmapArray.add(bitmap);


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

    public Bitmap RotateImage(Bitmap src, float degree) {
        // create new matrix object
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(degree);
        // return new bitmap rotated using matrix
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
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
                    BitmapDrawable draw = (BitmapDrawable) imageView.getDrawable();
                    Bitmap bitmap = draw.getBitmap();

                    FileOutputStream outStream = null;
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdCard.getAbsolutePath() + "/YourFolderName");
                    dir.mkdirs();
                    String fileName = String.format("%d.jpg", System.currentTimeMillis());
                    File outFile = new File(dir, fileName);
                    try {
                        outStream = new FileOutputStream(outFile);

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        outStream.flush();
                        outStream.close();
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.setData(Uri.fromFile(outFile));
                        sendBroadcast(intent);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.upload_id:
                saveInFirebase();
                break;
            case R.id.undo_id:
                int size = bitmapArray.size();
                bitmapArray.remove(size);
                bitmap= bitmapArray.get(size-1);
                imageView.setImageBitmap(bitmap);

                break;
        }
        return false;
    }

}
