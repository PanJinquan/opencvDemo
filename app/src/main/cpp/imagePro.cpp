//
// Created by panjq1 on 2017/10/22.
//
#include <string>
#include <android/log.h>
#include "opencv2/opencv.hpp"
// 宏定义类似java 层的定义,不同级别的Log LOGI, LOGD, LOGW, LOGE, LOGF。 对就Java中的 Log.i log.d
#define LOG_TAG    "---JNILOG---" // 这个是自定义的LOG的标识
//#undef LOG // 取消默认的LOG
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG, __VA_ARGS__)
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG, __VA_ARGS__)
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG, __VA_ARGS__)
#define LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG_TAG, __VA_ARGS__)
#include "imagePro.h"
#include "com_panjq_opencv_alg_ImagePro.h"
using namespace cv;

extern "C"
JNIEXPORT jintArray  JNICALL Java_com_panjq_opencv_alg_ImagePro_ImageBlur
        (JNIEnv *env, jobject obj, jintArray buf, jint w , jint h){
   LOGD("ImageBlur: called JNI start...");
    //读取int数组并转为Mat类型
    jint *cbuf = env->GetIntArrayElements(buf,JNI_FALSE);
    if (NULL == cbuf)
    {
        return 0;
    }
    Mat imgData(h,w,CV_8UC4,(unsigned char*) cbuf);
    cv::cvtColor(imgData,imgData,CV_BGRA2BGR);
    //这里进行图像相关操作
    blur(imgData,imgData,Size(20,20));


    //对图像相关操作完毕
    cv::cvtColor(imgData,imgData,CV_BGR2BGRA);
    //这里传回int数组。
    uchar *ptr = imgData.data;
    //int size = imgData.rows * imgData.cols;
    int size = w * h;
    jintArray result = env->NewIntArray(size);
//    env->SetIntArrayRegion(result, 0, size, cbuf);
    env->SetIntArrayRegion(result, 0, size, (const jint *) ptr);
    env->ReleaseIntArrayElements(buf, cbuf, 0);
    LOGD("ImageBlur: called JNI end...");
    return result;
}


extern "C"
JNIEXPORT jobject JNICALL Java_com_panjq_opencv_alg_ImagePro_ImageProJNI
        (JNIEnv *env, jobject obj, jobject image_obj){
    //获取Java中的实例类
    // jclass jcInfo = env->FindClass("com/panjq/opencv/alg/ImageData");
    jclass jcInfo = env->GetObjectClass(image_obj);

    //获得类属性
    jfieldID jf_w = env->GetFieldID(jcInfo, "w", "I");//ImageData类中属性w
    int w = env->GetIntField(image_obj, jf_w);
    jfieldID jf_h = env->GetFieldID(jcInfo, "h", "I");//ImageData类中属性h
    int h = env->GetIntField(image_obj, jf_h);

    //ImageData类中属性pixels
    jfieldID jf_pixels = env->GetFieldID(jcInfo, "pixels", "[I");
    //获得对象的pixels数据，并保存在pixels数组中
    jintArray pixels = (jintArray)env->GetObjectField(image_obj, jf_pixels);
    jint *ptr_pixels = env->GetIntArrayElements(pixels, 0);//获得pixels数组的首地址
    Mat imgData(h,w,CV_8UC4,(unsigned char*) ptr_pixels);
    cv::cvtColor(imgData,imgData,CV_BGRA2BGR);
    LOGE("ImageProJNI: input image size=[%d,%d]",imgData.cols,imgData.rows);
    //释放内存空间
    env->ReleaseIntArrayElements(pixels, ptr_pixels, 0);
    //imwrite("/storage/emulated/0/OpencvDemo/input_imgData.jpg",imgData);
    //****************** here to Opencv image relevant processing*****************
    /**
     *
     * 进行OpenCV的图像处理....
     *
     */
    blur(imgData,imgData,Size(20,20));//图像模糊
    resize(imgData,imgData,Size(imgData.cols/4,imgData.rows/4),INTER_LINEAR);//图像缩小4倍
    /**
     *
     *
     */
    //*********************************** end ************************************
    jobject obj_result = env->AllocObject(jcInfo);
    cv::cvtColor(imgData,imgData,CV_BGR2BGRA);
    //imwrite("/storage/emulated/0/OpencvDemo/out_imgData.jpg",imgData);
    uchar *ptr = imgData.data;
    int size = imgData.rows* imgData.cols;
    jintArray resultPixel = env->NewIntArray(size);
    jint *ptr_resultPixel = env->GetIntArrayElements(resultPixel, 0);//获得数组的首地址
    env->SetIntArrayRegion(resultPixel, 0, size, (const jint *) ptr);
    env->SetObjectField(obj_result, jf_pixels, resultPixel);
    h=imgData.rows;
    w=imgData.cols;
    LOGE("ImageProJNI: ouput image size=[%d,%d]",w,h);
    env->SetIntField(obj_result, jf_w, w);
    env->SetIntField(obj_result, jf_h, h);
    env->ReleaseIntArrayElements(resultPixel, ptr_resultPixel, 0);
    return  obj_result;
}