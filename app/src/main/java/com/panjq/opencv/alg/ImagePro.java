package com.panjq.opencv.alg;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by panjq1 on 2017/10/22.
 */

public class ImagePro {
    private static final String    TAG = "ImagePro:";
    static {
        System.loadLibrary("imagePro-lib");
    }
    public native int[] ImageBlur(int[] pixels,int w,int h);
    public native ImageData ImageProJNI(ImageData image_data);

    /**
     * 调用JNI的ImageBlur(int[] pixels,int w,int h)接口实现图像模糊
     */
    public Bitmap doImageBlur(Bitmap origImage) {
        int w = origImage.getWidth();
        int h = origImage.getHeight();
        int[] pixels = new int[w * h];
        origImage.getPixels(pixels, 0, w, 0, 0, w, h);
        int[] image=ImageBlur(pixels,w,h);
        Log.i(TAG, "ImageBlur called successfully");
        //最后将返回的int数组转为bitmap类型。
        Bitmap desImage=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        //faceall为返回的int数组
        desImage.setPixels(image,0,w,0,0,w,h);
        return desImage;
    }

    /**
     * 调用JNI的ImageProJNI(ImageData image_data)接口实现图像模糊
     */
    public Bitmap ImageProcess(Bitmap origImage) {
       ImageData imageData=new ImageData(origImage);
//        ImageData imageData=new ImageData();
//        imageData.getImageData(origImage);
        Log.i(TAG, "input image size:"+imageData.w+","+imageData.h);
        ImageData out_image=ImageProJNI(imageData);
        Log.i(TAG, "return image size:"+out_image.w+","+out_image.h);
        Bitmap desImage=out_image.getBitmap();
        saveImage(desImage,"desImage.jpg");
        Log.i(TAG, "ImageProJNI called successfully");
        return desImage;
    }

    public static void saveImage(Bitmap bmp,String name) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "OpencvDemo");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = name;
        File file = new File(appDir, fileName);
//        if (file.exists()) {
//            file.delete();
//        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Log.e(TAG, "图片保存成功...");
        } catch (FileNotFoundException e) {
            Log.e(TAG, "图片保存失败...");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
