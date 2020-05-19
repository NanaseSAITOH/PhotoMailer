package com.opengl.jackn.mailandphoto;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private File cameraFile;
    private Uri cameraUri;
    private String filePath;
    private final static int RESULT_CAMERA = 1001;
    private final static int REQUEST_PERMISSION = 1002;

    ArrayList<Uri> uris = new ArrayList<Uri>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button next= (Button) findViewById(R.id.next);
        Button cont= (Button) findViewById(R.id.cont);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);

                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                intent.setType("*/*");
                startActivity(intent);

            }
        });
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraIntent();
            }
        });
        if (savedInstanceState != null){
            cameraUri = savedInstanceState.getParcelable("CaptureUri");
        }
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission();
        }
        else {
            cameraIntent();
        }

    }


    private void cameraIntent(){
        File cameraFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"EmailAndPhoto");
        cameraFolder.mkdir();
        filePath = cameraFolder.getPath()+"/" + System.currentTimeMillis() +".jpg";

        cameraFile = new File(filePath);

        cameraUri = FileProvider.getUriForFile(MainActivity.this, getApplicationContext().getPackageName() + ".provider", cameraFile);
        File fileIn = new File(filePath);
        Uri u = Uri.fromFile(fileIn);
        uris.add(u);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        registerDatabase(filePath);
        startActivityForResult(intent, RESULT_CAMERA);
    }


    protected void onSaveInstanceState(Bundle outState){
        outState.putParcelable("CaptureUri", cameraUri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==RESULT_CAMERA){


        }
    }

    private void registerDatabase(String filePath) {
        ContentValues contentvalues = new ContentValues();
        ContentResolver contentResolver = MainActivity.this.getContentResolver();
        contentvalues.put(MediaStore.Images.Media.MIME_TYPE,"image/jpg");
        contentvalues.put("_data",filePath);
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentvalues);
    }


    private void checkPermission(){
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            cameraIntent();
        }
        // 拒否していた場合
        else{
            requestLocationPermission();
        }
    }


    private void requestLocationPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }else{
            Toast toast = Toast.makeText(this, "許可されないとアプリが実行できません", Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,}, REQUEST_PERMISSION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraIntent();
                return;

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this, "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

}
