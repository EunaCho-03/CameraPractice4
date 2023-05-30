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
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.PendingRecording;
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
import androidx.core.util.Consumer;

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

public class MainActivity extends AppCompatActivity { // 하위버전 단말기에 실행 안되는 메소드를 지원하기 위해 AppCompatActivity를 extend함
    PreviewView previewView; // 카메라에 비치는 화면의 역할
    //private String imageFilePath;
    Button startButton;
    Button stopButton;
    Button captureButton;
    private static final int REQUEST_CODE = 1;
    Button reverseButton;
    Button recordButton;
    Button capture;
    ImageView imageView; /// 이미지를 화면에 띄우기 위해
    //String TAG = "MainActivity";
    //String mCurrentPhotoPath;
    ProcessCameraProvider processCameraProvider; // 기본적인 카메라 접근을 부여함(카메라가 핸드폰에 있는지, 카메라 정보등에 대해 물어봄)
    int cameraFacing = CameraSelector.LENS_FACING_BACK; // 디폴트: 카메라 후면
    ImageCapture imageCapture;
    VideoCapture<Recorder> videoCapture = null; //카메라가 비디오프레임을 구성하게함
    Recording recording = null; // 실제 녹화를 실행함
    Bitmap bitmap = null; //bitmap = 이미지를 표현하기 위해 사용되는 객체(픽셀)


    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        //람다 호출 방식: (매개변수, ...) -> {실행문}
        //activityResultLauncher = 활동을 시작하고 다시 결과를 받는다 (카메라 앱을 시작하고 그 결과로 캡쳐된 사진을 받을 수 있음)
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) { //그냥 if(result)만 써도됨. result는 boolean이기 때문
            //MainActivity.this = 앱의 현재 상태 또는 흘러가는 맥락 / Manifst.permission.CAMERA = 필요한 권한 명칭
            //권한을 이미 부여 받았다면 요청을 다시 하지 않는다 호출 결과: PERMISSION_GRANTED(권한 있음) 또는 PERMISSION_DENITED (권한 없음)
            Log.e("TEST", "activityResultLauncher grated, calling startCamera()...");
            startCamera(cameraFacing);
        }
    });



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // 이 앱에서 카메라를 호출하고 그 결과를 다시 앱으로 가져옥ㄹ때
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TEST", "onActivityResult");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) { // onCreate: 액티비티가 생성될때 호출되며 사용자 인터페이스(클래스가 구현해야할 행동을 지정함) 초기화에 사용
        Log.e("TEST", "TESTLOG 1 lensFacing "+cameraFacing);

        super.onCreate(savedInstanceState); // super class 호출 (activity를 구현하는데 필요한 과정)
        setContentView(R.layout.activity_main); // layout에 있는 activity_main.xml로 화면 정의

        previewView = findViewById(R.id.previewView); // findViewById = activity_main.xml에서 설정된 뷰를 가져오는 메소드
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        captureButton = findViewById(R.id.captureButton);
        reverseButton = findViewById(R.id.reverseButton);
        recordButton = findViewById(R.id.recordButton);
        imageView = findViewById(R.id.imageView);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1); // 권한 요청 코드 (1 = request code / single permission 하나)

//        try {
//            processCameraProvider = ProcessCameraProvider.getInstance(this).get();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 사용자가 클릭한 위젯이 view 매개변수 들어감
                //Log.e("TEST", "startButton onClick called");
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) { // 권한을 부여받았다면
                    //Log.e("TEST", "startButton onClick permission granted");
//                    Log.e("TEST", "startButton onclick START, calling startCamera()...");
                    Log.e("TEST", "startButton onclick startCamera finished, using processCameraProvider...");
                    processCameraProvider.unbindAll(); // 수명주기에 있는 액티비티 모두 카메라X에서 해제
                    bindPreview();
                    bindImageCapture();
                    bindVideoCapture();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processCameraProvider.unbindAll(); // 뷰와 카메라 결합 해제.
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
                if(cameraFacing == CameraSelector.LENS_FACING_BACK){ //후면이면 전면 카메라로
                    cameraFacing = CameraSelector.LENS_FACING_FRONT;
                }
                else{
                    cameraFacing = CameraSelector.LENS_FACING_BACK; // 전면이면 후면 카메라로
                }

                processCameraProvider.unbindAll(); // 아래 코드들로 변경된 방향으로 새로운 카메라 뷰 생성하기
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

//                saveImage();

                //Log.e("TEST", "captureButton onClick called");

                imageCapture.takePicture(ContextCompat.getMainExecutor(MainActivity.this),
                        new ImageCapture.OnImageCapturedCallback() {  // 이미지 캡쳐가 완료되면 콜백 (콜백:어떤 조건이 충족되면(이벤트가 발생하면) 이 코드 처리를 해라)
                            @Override
                            public void onCaptureSuccess(@NonNull ImageProxy image) { // close하는(끝내는) 콜백 (여기서 @NonNull ImageProxy image = 캡쳐된 이미지
                                //Log.e("TEST", "takePicture onCaptureSuccess");
                                @SuppressLint({"UnsafeExperimentalUsageError", "UnsafeOptInUsageError"})
                                Image mediaImage = image.getImage();
                                Bitmap[] bitmap = {ImageUtil.mediaImageToBitmap(mediaImage)};
                                Bitmap rotatedBitmap = ImageUtil.rotateBitmap(bitmap[0], image.getImageInfo().getRotationDegrees());
                                //Bitmap rotatedBitmap = ImageUtil.mediaImageToBitmap(mediaImage); 그냥 mediaImage를 이미지뷰에 넣으면 회전된 각도로 나옴
                                imageView.setImageBitmap(rotatedBitmap); // 이미지뷰에 비트맵을 로드해서 출력한다
                                saveImage();
                            }
                           // @Override
                           // public void onError(@NonNull ImageCaptureException exception) { // 에러가 발생한다면
                           //     super.onError(exception);
                           //     exception.printStackTrace(); //애러 메세지의 발생 근원지를 찾아서 단계별로 에러를 출력함
                           // }
                        }
                );

            }
        });

        recordButton.setOnClickListener(view -> {
            Log.e("TEST", "recordButton onClick");
            //권한체크

            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                activityResultLauncher.launch(Manifest.permission.CAMERA);
            } else if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                activityResultLauncher.launch(Manifest.permission.RECORD_AUDIO);
            } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                //Log.e("TEST", "recordButton onClick permission granted. Moving to function captureVideo");
                captureVideo();
            }
        });

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.CAMERA);
        } else {
            Log.e("TEST", "onCreate calling startCamera()...");
            startCamera(cameraFacing);
        }

    }

/*
    @Override
    protected void onResume() {
        super.onResume();
        if(startButton != null) {
            startButton.performClick();

        }
    }
*/
    public void captureVideo(){
        Log.e("TEST", "captureVideo called");
        if(videoCapture == null)
        {
            Log.e("TEST", "videoCapture null");
            Recorder recorder = new Recorder.Builder()
                    .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                    .build();
            videoCapture = VideoCapture.withOutput(recorder); // recorder: VideoCapture과 결합된 VideoOutput의 구현. 동영상 및 오디오 캠쳐를 실행하는데 사용됨
        }

        Recording recording1 = recording;
        if (recording1 != null) { // 만약 지금 실행되고있는 녹화가 있다면 멈추고 현재 녹화분을 내보낸다
            Log.e("TEST", "recording1 not null");
            recording1.stop();
            recording = null;
            return;
        }

        String name = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault()).format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues(); // ContentValues:이름과 값을 관리하는 객체
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
        contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video");

        //MediaStoreOutputOptions = 아웃풋(비디오)를 MediaStore에 저장하는 옵션
        MediaStoreOutputOptions options = new MediaStoreOutputOptions.Builder(getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                .setContentValues(contentValues).build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // 오디오를 사용 설정하고 녹화를 구성하는 역활
        PendingRecording prepareRecording = videoCapture.getOutput().prepareRecording(MainActivity.this, options);
        PendingRecording withAudioEnabled = prepareRecording.withAudioEnabled();

        recording = withAudioEnabled.start(ContextCompat.getMainExecutor(MainActivity.this), new Consumer<VideoRecordEvent>() {

            @Override
            public void accept(VideoRecordEvent videoRecordEvent) {
                //Log.e("TEST", "Record prepare");

                if (videoRecordEvent instanceof VideoRecordEvent.Start) {
                    recordButton.setEnabled(true);
                    //Log.e("TEST", "Record start");

                } else if (videoRecordEvent instanceof VideoRecordEvent.Finalize) {
                    if (!((VideoRecordEvent.Finalize) videoRecordEvent).hasError()) {
                        Log.e("TEST", "Record complete");
                        //에러가 없다면 성공했다는 메세지와 함께 비디오 정보 토스트로 띄우기
                        //String msg = "Video capture succeeded: " + ((VideoRecordEvent.Finalize) videoRecordEvent).getOutputResults().getOutputUri();
                        String msg = "Video capture succeeded: " + name;
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        if (recording != null)
                            recording.close();
                        recording = null;
                        String msg = "Error: " + ((VideoRecordEvent.Finalize) videoRecordEvent).getError();
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void startCamera(int cameraFacing) {
        /*
        1.CameraProvider 확인하기
        2.선택한 카메라와 프로그램의 수명 수기 결합
         */
        Log.e("TEST", "startCamera START");
        ListenableFuture<ProcessCameraProvider> future_processCameraProvider = ProcessCameraProvider.getInstance(MainActivity.this); //지금 액티비티의 ProcessCameraProvider을 회수함

        Log.e("TEST", "startCamera future added");
        future_processCameraProvider.addListener(() -> {
            Log.e("TEST", "startCamera future accessible");
            try {
                Log.e("TEST", "startCamera future try start");
                processCameraProvider = future_processCameraProvider.get(); //카메라의 생명주기를 액티비티와 같은 생명주기에 결합시킴
                Log.e("TEST", "startCamera future get complete");
                processCameraProvider.unbindAll();
/*
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                Recorder recorder = new Recorder.Builder()
                        .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                        .build();
                videoCapture = VideoCapture.withOutput(recorder);


                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing).build();

                imageCapture = new ImageCapture.Builder()
                        .build();

                processCameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture, imageCapture);
*/
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(MainActivity.this));
        Log.e("TEST", "startCamera FINISHED");
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

/*
    public byte[] bitmapToByteArray(Bitmap $bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        $bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        return stream.toByteArray()  ;
    }
*/


    void bindPreview() {
        previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER); //이미지의 가로, 세로 중 긴 쪽을 ImageView의 레이아웃에 맞춰출력함 (이미지 비율은 유지)

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(cameraFacing)
                .build();

        //cameraSelector = CameraSelector.LENS_FACING_BACK;
        Preview preview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3) //디폴트 표준 비율
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        processCameraProvider.bindToLifecycle(this, cameraSelector, preview);
        //Log.e("TEST", "bindPreview SUCC");
    }

    void bindImageCapture() {
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(cameraFacing)
                .build();
        imageCapture = new ImageCapture.Builder()
                .build();

        processCameraProvider.bindToLifecycle(this, cameraSelector, imageCapture); // 카메라 생명주기 연결
        //Log.e("TEST", "bindImageCapture SUCC");
    }

    void bindVideoCapture(){
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(cameraFacing)
                .build();
        Recorder recorder = new Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST)) // 새로운 퀄리티의 recorder 생성
                .build();
        videoCapture = VideoCapture.withOutput(recorder);

        processCameraProvider.bindToLifecycle(this, cameraSelector, videoCapture);

    }

    @Override
    protected void onPause() {
        // 액티비티가 도이상 보이지 않음 / 중지 상태 (홈 버튼 눌러서 바깥으로 잠깐 빠져나갔을때)
        // 다른 액티비티가 활성화 돼있을때((이 다른 액티비티가 소멸되면 이 액티비티가 다시 활성화), 종료가 아님)일때 실행:
        super.onPause();
        if(processCameraProvider != null)
            processCameraProvider.unbindAll();
    }
}
