package com.examaple.myapplication;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.examaple.myapplication.ViewModels.CameraViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private CameraViewModel cameraViewModel;

    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private View clearRectangle;
    private Button captureButton;
    private ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraViewModel = new ViewModelProvider(this).get(CameraViewModel.class);

        surfaceView = findViewById(R.id.cameraPreview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        clearRectangle = findViewById(R.id.clearRectangle);
        progressBar = findViewById(R.id.progressBar);

        captureButton = findViewById(R.id.captureButton);
        captureButton.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            captureImage();
        });
    }

    private void captureImage() {
        if (camera != null) {
            camera.takePicture(null, null, pictureCallback);
        }
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            cameraViewModel.captureImage(data,getApplicationContext());
            // Restart the camera preview (if needed)
            camera.startPreview();
        }
    };

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        // Open the camera when the surface is created
        if (checkCameraPermission()) {
            camera = Camera.open();
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = getBestPreviewSize(width, height, parameters);

            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);

                // Add the following lines to set display orientation
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(0, info); // Use camera ID 0 for back camera, adjust as needed
                int rotation = getWindowManager().getDefaultDisplay().getRotation();
                int degrees = 0;

                switch (rotation) {
                    case Surface.ROTATION_0:
                        degrees = 0;
                        break;
                    case Surface.ROTATION_90:
                        degrees = 90;
                        break;
                    case Surface.ROTATION_180:
                        degrees = 180;
                        break;
                    case Surface.ROTATION_270:
                        degrees = 270;
                        break;
                }

                int result;
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    result = (info.orientation + degrees) % 360;
                    result = (360 - result) % 360;  // compensate for the mirror
                } else {  // back-facing
                    result = (info.orientation - degrees + 360) % 360;
                }

                try {
                    camera.setDisplayOrientation(result);
                    camera.setParameters(parameters);
                } catch (Exception e) {
                    Log.e("CameraActivity", "Error setting camera parameters: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            camera.startPreview();
        }
    }


    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        List<Camera.Size> supportedSizes = parameters.getSupportedPreviewSizes();
        Camera.Size bestSize = null;

        for (Camera.Size size : supportedSizes) {
            if (size.width <= width && size.height <= height) {
                if (bestSize == null || (size.width * size.height > bestSize.width * bestSize.height)) {
                    bestSize = size;
                }
            }
        }

        return bestSize;
    }


    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        // Release the camera when the surface is destroyed
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
    }

    private boolean checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                return false;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}