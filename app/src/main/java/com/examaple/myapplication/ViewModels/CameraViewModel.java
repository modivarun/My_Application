package com.examaple.myapplication.ViewModels;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.examaple.myapplication.ImagePreviewActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraViewModel extends ViewModel {
    private MutableLiveData<Bitmap> capturedBitmapLiveData = new MutableLiveData<>();
    private Context context;

    public MutableLiveData<Bitmap> getCapturedBitmap() {
        return capturedBitmapLiveData;
    }

    public void captureImage(byte[] data, Context applicationContext) {
        // Convert the byte array to a Bitmap
        Bitmap capturedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        this.context = applicationContext;

        // Save the bitmap to a temporary file
        File tempFile = saveBitmapToFile(capturedBitmap);

        // Pass the file path to the ImagePreviewActivity
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra("IMAGE_PATH", tempFile.getPath());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        // Update LiveData with the captured bitmap
        capturedBitmapLiveData.setValue(capturedBitmap);
    }

    private File saveBitmapToFile(Bitmap bitmap) {
        // Get the external storage directory
        File sdcard;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        } else {
            sdcard = Environment.getExternalStorageDirectory();
        }

        // Set the directory name within the external storage
        File picturesDirectory = new File(sdcard, "Test");

        // Create necessary directories if they don't exist
        if (!picturesDirectory.exists()) {
            boolean created = picturesDirectory.mkdirs();
            Log.d("CameraViewModel", "Directory created: " + created);
        }

        // Create a unique filename for each image
        String fileName = "img_" + System.currentTimeMillis() + ".jpg";
        File tempFile = new File(picturesDirectory, fileName);

        // Correct the orientation and save the bitmap to the file
        correctOrientationAndSave(bitmap, tempFile);

        return tempFile;
    }

    private void correctOrientationAndSave(Bitmap bitmap, File file) {
        try {
            Bitmap bitmap1 = bitmap;

            if (bitmap1 != null) {
                if (bitmap1.getWidth() > bitmap1.getHeight()) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90); // Rotate by 90 degrees
                    bitmap1 = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), matrix, true);
                }

                // Save the rotated bitmap to the file (overwrite the previous content)
                FileOutputStream fosRotated = new FileOutputStream(file);
                bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, fosRotated);
                fosRotated.flush();
                fosRotated.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CameraViewModel", "Error saving rotated bitmap: " + e.getMessage());
        }
    }
}