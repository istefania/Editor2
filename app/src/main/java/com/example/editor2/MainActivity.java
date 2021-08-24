package com.example.editor2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import androidx.fragment.app.FragmentTransaction;

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

    Rotate rotate=new Rotate();
    Flip flip= new Flip();
    Contrast contrast=new Contrast();
    Brightness brightness=new Brightness();
    Saturation saturation=new Saturation();

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

    Button imageShare;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addShareImageFragment();

        imageView = (ImageView) findViewById(R.id.image_view);

        apply = (Button) findViewById(R.id.apply_button);
        imageShare=(Button) findViewById(R.id.fb_share_button);

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
                       bitmap=flip.FlipImage(bitmap,1);
                       imageView.setImageBitmap(bitmap);
                       drawerLayout.closeDrawers();
                   }
                   else
                   {
                       bitmap=flip.FlipImage(bitmap,2);
                       imageView.setImageBitmap(bitmap);
                       drawerLayout.closeDrawers();
                   }
               }

               if(groupPosition==1){
                   if(childPosition==0)
                   {
                       bitmap=rotate.RotateImage(bitmap,90);
                       imageView.setImageBitmap(bitmap);
                       drawerLayout.closeDrawers();
                   }
                   else{
                       bitmap= rotate.RotateImage(bitmap,-90);
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
                brightness.SetBrightness(bitmap, progress);
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
                    Bitmap bitmap1 = contrast.SetContrast(bitmap, contrast_value);
                    bitmap1 = brightness.SetBrightness(bitmap1, brightness_value);
                    bitmap1 = saturation.SetSaturation(bitmap1, saturation_value);
                    ImageView imageView = findViewById(R.id.image_view);
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

    private void addShareImageFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.facebook_share_button, new ShareImageFragment());
        fragmentTransaction.commit();
    }


    void saveInFirebase(){
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
