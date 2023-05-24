package com.example.camerapractice4;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;



public class MainActivity extends AppCompatActivity {
    PreviewView previewView;
    private String imageFilePath;
    Button startButton;
    Button stopButton;
    Button captureButton;

    Button reverseButton;
    ImageView imageView;
    String TAG = "MainActivity";
    String mCurrentPhotoPath;
    ProcessCameraProvider processCameraProvider;
    //int lensFacing = CameraSelector.LENS_FACING_FRONT;
    int lensFacing = CameraSelector.LENS_FACING_BACK;
    ImageCapture imageCapture;
    Bitmap bitmap = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.previewView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        captureButton = findViewById(R.id.captureButton);
        reverseButton = findViewById(R.id.reverseButton);
        imageView = findViewById(R.id.imageView);


        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);

        try {
            processCameraProvider = ProcessCameraProvider.getInstance(this).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    bindPreview();
                    bindImageCapture();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processCameraProvider.unbindAll();
            }
        });
/*
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filename = "photo.JPG";
                saveFile(filename);

            }
        });

        reverseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), FrontActivity.class);
                startActivity(intent);
            }
        });

*/
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = "photo.JPG";
                saveFile(filename);

                imageCapture.takePicture(ContextCompat.getMainExecutor(MainActivity.this),
                        new ImageCapture.OnImageCapturedCallback() {
                            @Override
                            public void onCaptureSuccess(@NonNull ImageProxy image) {

                                @SuppressLint({"UnsafeExperimentalUsageError", "UnsafeOptInUsageError"})
                                Image mediaImage = image.getImage();
                                Bitmap[] bitmap = {ImageUtil.mediaImageToBitmap(mediaImage)};

                                Log.d("MainActivity", Integer.toString(bitmap[0].getWidth())); //4128
                                Log.d("MainActivity", Integer.toString(bitmap[0].getHeight())); //3096

                                Bitmap rotatedBitmap = ImageUtil.rotateBitmap(bitmap[0], image.getImageInfo().getRotationDegrees());

                                Log.d("MainActivity", Integer.toString(rotatedBitmap.getWidth())); //3096
                                Log.d("MainActivity", Integer.toString(rotatedBitmap.getHeight())); //4128
                                Log.d("MainAtivity", Integer.toString(image.getImageInfo().getRotationDegrees())); //90 //0, 90, 180, 90 //이미지를 바르게 하기위해 시계 방향으로 회전해야할 각도


                                imageView.setImageBitmap(rotatedBitmap);

                                super.onCaptureSuccess(image);
                                String filename = "photo.JPG";
                                saveFile(filename);


                            }
                        }
                );
            }
        });
    }



    private void saveFile(String filename)
    {

        if(bitmap == null)
        {
            Toast.makeText(getApplicationContext(), "먼저 촬영을 하세요", Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(
                MediaStore.Images.Media.DISPLAY_NAME,
                filename);
        values.put(
                MediaStore.Images.Media.MIME_TYPE,
                "image/*");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(
                    MediaStore.Images.Media.IS_PENDING,
                    1);
        }

        ContentResolver contentResolver = getContentResolver();
        Uri item = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);

        try {
            ParcelFileDescriptor pdf =
                    contentResolver.openFileDescriptor(
                            item,
                            "w",
                            null);

            if (pdf == null) {
                Toast.makeText(getApplicationContext(), "파일 디스크립션 생성에 실패하였습니다.", Toast.LENGTH_LONG).show();
                return;
            }
            byte[] strToByte = bitmapToByteArray(bitmap);
            FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());
            fos.write(strToByte);
            fos.close();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear();
                values.put(
                        MediaStore.Images.Media.IS_PENDING,
                        0);
                contentResolver.update(item, values, null, null);
            }
            Toast.makeText(getApplicationContext(), "갤러리에 저장", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    public byte[] bitmapToByteArray(Bitmap $bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        $bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        return stream.toByteArray()  ;
    }



    void bindPreview() {
        previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();
        Preview preview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3) //디폴트 표준 비율
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        processCameraProvider.bindToLifecycle(this, cameraSelector, preview);
    }

    void bindImageCapture() {
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();
        imageCapture = new ImageCapture.Builder()
                .build();

        processCameraProvider.bindToLifecycle(this, cameraSelector, imageCapture);
    }

    @Override
    protected void onPause() {
        super.onPause();
        processCameraProvider.unbindAll();
    }
}