package com.panjq.opencv.opencvdemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.panjq.opencv.alg.ImagePro;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String    TAG = "MainActivity";
    private ImageView imageview;
    private Bitmap bmp,bmp_out;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    static {
        File appDir = new File(Environment.getExternalStorageDirectory(), "OpencvDemo");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(MainActivity.this);
        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        imageview = (ImageView) findViewById(R.id.image_view);
        //将girl图像加载程序中并进行显示
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.girl);
        imageview.setImageBitmap(bmp);
        TextView tv2 = (TextView) findViewById(R.id.tv2);
        Button buttonLoadImage = (Button) findViewById(R.id.buttonLoadPicture);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.i(TAG, "onClick...");
                ImagePro img=new ImagePro();
               // Bitmap bmp_out =img.doImageBlur(bmp);
                bmp_out =img.ImageProcess(bmp);
                imageview.setImageBitmap(bmp_out);
                Log.i(TAG, "setImageBitmap");
            }
        });

    }


    /**
     * 添加文件读写权限
     */
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
