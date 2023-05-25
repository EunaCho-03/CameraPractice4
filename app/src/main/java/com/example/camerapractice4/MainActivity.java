package com.example.camerapractice4;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.Manifest;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class MainActivity extends AppCompatActivity {
    PreviewView previewView;
    //private String imageFilePath;
    Button startButton;
    Button stopButton;
    Button captureButton;
    private static final int REQUEST_CODE = 1;
    Button reverseButton;
    Button recordButton;
    Button capture;
    ImageView imageView;
    //String TAG = "MainActivity";
    //String mCurrentPhotoPath;
    ProcessCameraProvider processCameraProvider;
    int cameraFacing = CameraSelector.LENS_FACING_BACK;
    ImageCapture imageCapture;
    VideoCapture<Recorder> videoCapture = null;
    Recording recording = null;
    Bitmap bitmap = null;

    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera(cameraFacing);
        }
    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TEST", "onActivityResult");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e("TEST", "TESTLOG 1 lensFacing "+cameraFacing);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.previewView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        captureButton = findViewById(R.id.captureButton);
        reverseButton = findViewById(R.id.reverseButton);
        recordButton = findViewById(R.id.recordButton);
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
*/
        reverseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Log.e("TEST", "TESTLOG 1 lensFacing "+lensFacing);
                if(cameraFacing == CameraSelector.LENS_FACING_BACK){
                    cameraFacing = CameraSelector.LENS_FACING_FRONT;
                }
                else{
                    cameraFacing = CameraSelector.LENS_FACING_BACK;
                }

                // Log.e("TEST", "TESTLOG 2 lensFacing "+lensFacing);
                processCameraProvider.unbindAll();
                bindPreview();
                bindImageCapture();
                //Intent intent = new Intent(getApplicationContext(), FrontActivity.class);
                //startActivity(intent);
            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String filename = "photo.JPG";
                //saveFile(filename);

                saveImage();

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
                                //String filename = "photo.JPG";
                                //saveFile(filename);
                                saveImage();

                            }
                        }
                );


            }
        });

        recordButton.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                activityResultLauncher.launch(Manifest.permission.CAMERA);
            } else if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                activityResultLauncher.launch(Manifest.permission.RECORD_AUDIO);
            } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                captureVideo();
            }
        });

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.CAMERA);
        } else {
            startCamera(cameraFacing);
        }

    }

    public void captureVideo(){

        //recordButton.setImageResource(R.drawable.round_stop_circle_24);
        Recording recording1 = recording;
        if (recording1 != null) {
            recording1.stop();
            recording = null;
            return;
        }
        String name = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault()).format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
        contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video");

        MediaStoreOutputOptions options = new MediaStoreOutputOptions.Builder(getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                .setContentValues(contentValues).build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        recording = videoCapture.getOutput().prepareRecording(MainActivity.this, options).withAudioEnabled().start(ContextCompat.getMainExecutor(MainActivity.this), videoRecordEvent -> {
            if (videoRecordEvent instanceof VideoRecordEvent.Start) {
                recordButton.setEnabled(true);
            } else if (videoRecordEvent instanceof VideoRecordEvent.Finalize) {
                if (!((VideoRecordEvent.Finalize) videoRecordEvent).hasError()) {
                    String msg = "Video capture succeeded: " + ((VideoRecordEvent.Finalize) videoRecordEvent).getOutputResults().getOutputUri();
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    recording.close();
                    recording = null;
                    String msg = "Error: " + ((VideoRecordEvent.Finalize) videoRecordEvent).getError();
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
                // recordButton.setImageResource(R.drawable.round_fiber_manual_record_24);
            }
        });
    }

    public void startCamera(int cameraFacing) {
        ListenableFuture<ProcessCameraProvider> processCameraProvider = ProcessCameraProvider.getInstance(MainActivity.this);

        processCameraProvider.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = processCameraProvider.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                Recorder recorder = new Recorder.Builder()
                        .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                        .build();
                videoCapture = VideoCapture.withOutput(recorder);

                cameraProvider.unbindAll();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing).build();

                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture);

                //   toggleFlash.setOnClickListener(view -> toggleFlash(camera));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(MainActivity.this));
    }



    private void saveImage() {
        Uri images;
        ContentResolver contentResolver = getContentResolver();
        images = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

        ContentValues contentValues = new ContentValues(0);
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis()+ ".JPG");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "images/");
        Uri uri = contentResolver.insert(images, contentValues);

        try{
            BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();

            OutputStream outputStream = contentResolver.openOutputStream(Objects.requireNonNull(uri));
            bitmap.compress(Bitmap.CompressFormat.JPEG,100, outputStream);
            Objects.requireNonNull(outputStream);
            Toast.makeText(getApplicationContext(), "갤러리에 저장 성공", Toast.LENGTH_LONG).show();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
/*
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
*/


    public byte[] bitmapToByteArray(Bitmap $bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        $bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        return stream.toByteArray()  ;
    }



    void bindPreview() {
        previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(cameraFacing)
                .build();
        Preview preview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3) //디폴트 표준 비율
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        processCameraProvider.bindToLifecycle(this, cameraSelector, preview);
    }

    void bindImageCapture() {
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(cameraFacing)
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