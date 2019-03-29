package com.burhanrashid52.imageeditor;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Gif extends AppCompatActivity {

    private Button btn;
    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;
    private GridView gvGallery;
    public ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
    private GalleryAdapter galleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);
        btn = findViewById(R.id.btn);
        gvGallery = (GridView)findViewById(R.id.gv);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mArrayUri.clear();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);

            }
        });

        findViewById(R.id.neww).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                try {
                    AnimationDrawable animation = new AnimationDrawable();
                    for(int i=0;i<mArrayUri.size();i++) {
                        InputStream inputStream = getContentResolver().openInputStream(mArrayUri.get(i));
                        animation.addFrame(Drawable.createFromStream(inputStream, mArrayUri.get(i).toString()), 300);
                    }
                 /* InputStream inputStream = getContentResolver().openInputStream(mArrayUri.get(0));
            animation.addFrame(Drawable.createFromStream(inputStream, mArrayUri.get(0).toString()), 300);
            animation.addFrame(getResources().getDrawable(R.drawable.saveetha), 300);
            animation.addFrame(getResources().getDrawable(R.drawable.icon), 300);*/
                    animation.setOneShot(false);
                    ImageView imageAnim =  (ImageView) findViewById(R.id.myimg);
                    imageAnim.setImageDrawable(animation);
                    animation.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

/*
        animation.addFrame(getResources().getDrawable(R.drawable.saveetha), 300);
        animation.addFrame(getResources().getDrawable(R.drawable.icon), 300);
        animation.addFrame(getResources().getDrawable(R.drawable.saveetha), 300);
*/




    }




    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){

                    Uri mImageUri=data.getData();

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded  = cursor.getString(columnIndex);
                    cursor.close();
                    mArrayUri.add(mImageUri);
                    galleryAdapter = new GalleryAdapter(getApplicationContext(),mArrayUri);
                    gvGallery.setAdapter(galleryAdapter);
                    gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                            .getLayoutParams();
                    mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded  = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();

                            galleryAdapter = new GalleryAdapter(getApplicationContext(),mArrayUri);
                            gvGallery.setAdapter(galleryAdapter);
                            gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                                    .getLayoutParams();
                            mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        Toast.makeText(Gif.this, "Selected Successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


}

