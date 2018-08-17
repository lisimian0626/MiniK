#include "NdkJniUtil.h"
#include <android/log.h>
#include <jni.h>
#include <vector>
#include "Score.h"

#define LOG_TAG    "jnilog" // 这个是自定义的LOG的标识，可用来定位
#undef LOG // 取消默认的LOG
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__) // 定义LOG类型
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__) // 定义LOG类型
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__) // 定义LOG类型
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__) // 定义LOG类型
#define LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG_TAG,__VA_ARGS__) // 定义LOG类型

Score score_obj;
/*
 * Class:     NdkJniUtil
 * Signature: ()Ljava/lang/String;
 */

JNIEXPORT jint JNICALL Java_com_beidousat_score_NdkJniUtil_setNotes
        (JNIEnv *env, jobject obj, jobject noteList){
    // 读入输入的note对象数组
    jclass clsNoteInfo = env->FindClass("com/beidousat/score/NoteInfo"); // 获取NoteInfo类型指针
    jclass cls_arraylist = env->GetObjectClass(noteList); // 获取ArrayList类型指针

    jmethodID arraylist_get = env->GetMethodID(cls_arraylist,"get","(I)Ljava/lang/Object;"); // 获取ArrayList对象的get方法ID
    jmethodID arraylist_size = env->GetMethodID(cls_arraylist,"size","()I"); // 获取ArrayList对象的size方法ID
    jint len_note_list = env->CallIntMethod(noteList, arraylist_size); // 调用ArrayList对象的size方法

    jfieldID posID = env->GetFieldID(clsNoteInfo, "startPos", "F"); // 获取NoteInfo对象的pos变量ID，浮点类型
    jfieldID lenID = env->GetFieldID(clsNoteInfo, "len", "F"); // 获取NoteInfo对象的len变量ID，浮点类型
    jfieldID keyID = env->GetFieldID(clsNoteInfo, "key", "F"); // 获取NoteInfo对象的key变量ID，浮点类型

    vector<Note> notes;
    for(int i=0;i<len_note_list;i++){
        jobject obj_note = env->CallObjectMethod(noteList, arraylist_get, i); // 调用noteList的get方法，获取列表中第i个NoteInfo对象

        jfloat pos = env->GetFloatField(obj_note, posID); // 获取NoteInfo对象的pos值
        jfloat len = env->GetFloatField(obj_note, lenID); // 获取NoteInfo对象的len值
        jfloat key = env->GetFloatField(obj_note, keyID); // 获取NoteInfo对象的key值

        Note n;
        n.pos = pos;
        n.len = len;
        n.key = key;
        n.head = pos;
        n.tail = pos + len;
        notes.push_back(n);
        env->DeleteLocalRef(obj_note);
        //LOGI("got note (%d/%d): pos %f, len %f, key %f", i, len_note_list, pos, len, key);
    }

    score_obj.set_notes(notes);
    // LOGI("got %d notes[3]", score_obj.get_notes().size());
    return 0;
}

#define DATA_LEN 4096
#define FRAMES_PER_EPOCH 512 // 44100Hz的采样率下
#define ANAYLY_EPOCH 8 // 分析次数
// 512 * 8 = 4096

JNIEXPORT jobjectArray JNICALL Java_com_beidousat_score_NdkJniUtil_getAnalyzeResult
        (JNIEnv *env, jobject obj, jdoubleArray wavArray, jlong current_pos, jint data_len){

    // 读取输入的wav数组
    jdouble *pWavArray;
    pWavArray = env->GetDoubleArrayElements(wavArray, 0);//获取数组 wavArray 的首地址（指针）
    int len_wavArray = env->GetArrayLength(wavArray);

    // LOGI("len_wavArray %d,  current_pos %d, data_len %d", len_wavArray, current_pos, data_len);
    /////////////////////////////////////////////////////////////////////////////////////
    vector<double> red_time, red_keys;
    score_obj.clear_visual_data();
    float mil_sec = current_pos / 1000.0; //ms to s
    float interval_time = 0.0058; // 44100 * 16bit * 2 * 1sec / (8bit) = 1秒数据量, 除以8192得到约每秒21个数据包
    // 4096字节的数据，分成8个数据包输入到分析器，每次FRAMES_PER_EPOCH(512)个字节
    for (int i=0;i<ANAYLY_EPOCH;i++) // 8
    {
        float from_sec = mil_sec + i * interval_time; // 当前数据包的近似起始时间//current_pos + i * FRAMES_PER_EPOCH; // 512
        float to_sec = mil_sec + (i + 1) * interval_time;// 当前数据包的近似结束时间 // current_pos + (i + 1) * FRAMES_PER_EPOCH; // 512
        int from_pos = i * FRAMES_PER_EPOCH;
        int to_pos = (i + 1) * FRAMES_PER_EPOCH;
        score_obj.input_frames(pWavArray, from_pos, to_pos, from_sec, to_sec);// 输入音频数据包
    }
    env->ReleaseDoubleArrayElements(wavArray, pWavArray, 0);

    score_obj.get_visual_data(red_time, red_keys); // 获取分析结果
    /////////////////////////////////////////////////////////////////////////////////////
//    // 打开这段注释，会输出评分结果日志
//	for (size_t i=0;i<red_time.size();i++){
//	    LOGI("time %f, key %f", red_time[i], red_keys[i]);
//	}

    // 返回值
    jclass clsKeyInfo = env->FindClass("com/beidousat/score/KeyInfo"); // 获取KeyInfo类型指针
    jobjectArray infos = env->NewObjectArray(red_time.size(), clsKeyInfo, NULL);  // jobjectArray 为指针类型
    jmethodID consID = env->GetMethodID(clsKeyInfo, "<init>", "()V"); // 获取KeyInfo类的初始化方法 javap输出的结果里的static {}就是<clinit>()V。只是把名字“美化”了一下而已。
    jfieldID timeID = env->GetFieldID(clsKeyInfo, "time", "F"); // 获取KeyInfo类的time变量ID，浮点数类型 // 字符串类型Ljava/lang/String
    jfieldID keyID = env->GetFieldID(clsKeyInfo, "key", "F"); // 获取KeyInfo类的key变量ID，浮点数类型 // 整数类型I
    for (size_t i=0;i<red_time.size();i++){
        obj = env->NewObject(clsKeyInfo, consID); // 生成一个对象
        env->SetFloatField(obj, timeID, (jfloat)red_time[i]); // 设置对象值域1 // env->SetObjectField(obj, timeID, env->NewStringUTF("disk")); // 设置对象值域1
        env->SetFloatField(obj, keyID, (jfloat)red_keys[i]); // 设置对象值域2 // env->SetIntField(obj, keyID, (jint)i); // 设置对象值域2
        env->SetObjectArrayElement(infos, i, obj); // 填充对象到返回array
        // LOGI("SetObjectArrayElement time %f, key %f", red_time[i], red_keys[i]);
    }

    return infos;
}

JNIEXPORT jobjectArray JNICALL Java_com_beidousat_score_NdkJniUtil_getAnalyzeResultEasy
        (JNIEnv *env, jobject obj, jdoubleArray wavArray, jlong current_pos, jint data_len){

    // 读取输入的wav数组
    jdouble *pWavArray;
    pWavArray = env->GetDoubleArrayElements(wavArray, 0);//获取数组 wavArray 的首地址（指针）
    int len_wavArray = env->GetArrayLength(wavArray);

    //LOGI("len_wavArray %d,  current_pos %d, data_len %d", len_wavArray, current_pos, data_len);
    /////////////////////////////////////////////////////////////////////////////////////
    vector<double> red_time, red_keys;
    score_obj.clear_visual_data();
    float mil_sec = current_pos / 1000.0; //ms to s
    float interval_time = 0.0058; // 44100 * 16bit * 2 * 1sec / (8bit) = 1秒数据量, 除以8192得到约每秒21个数据包
    // 4096字节的数据，分成8个数据包输入到分析器，每次FRAMES_PER_EPOCH(512)个字节
    for (int i=0;i<ANAYLY_EPOCH;i++) // 8
    {
        float from_sec = mil_sec + i * interval_time; // 当前数据包的近似起始时间//current_pos + i * FRAMES_PER_EPOCH; // 512
        float to_sec = mil_sec + (i + 1) * interval_time;// 当前数据包的近似结束时间 // current_pos + (i + 1) * FRAMES_PER_EPOCH; // 512
        int from_pos = i * FRAMES_PER_EPOCH;
        int to_pos = (i + 1) * FRAMES_PER_EPOCH;
        score_obj.input_frames_easy(pWavArray, from_pos, to_pos, from_sec, to_sec);// 输入音频数据包
    }
    env->ReleaseDoubleArrayElements(wavArray, pWavArray, 0);

    score_obj.get_visual_data(red_time, red_keys); // 获取分析结果
    /////////////////////////////////////////////////////////////////////////////////////
    // 打开这段注释，会输出评分结果日志
//	for (size_t i=0;i<red_time.size();i++){
//	    LOGI("time %f, key %f", red_time[i], red_keys[i]);
//	}

    // 返回值
    jclass clsKeyInfo = env->FindClass("com/beidousat/score/KeyInfo"); // 获取KeyInfo类型指针
    jobjectArray infos = env->NewObjectArray(red_time.size(), clsKeyInfo, NULL);  // jobjectArray 为指针类型
    jmethodID consID = env->GetMethodID(clsKeyInfo, "<init>", "()V"); // 获取KeyInfo类的初始化方法 javap输出的结果里的static {}就是<clinit>()V。只是把名字“美化”了一下而已。
    jfieldID timeID = env->GetFieldID(clsKeyInfo, "time", "F"); // 获取KeyInfo类的time变量ID，浮点数类型 // 字符串类型Ljava/lang/String
    jfieldID keyID = env->GetFieldID(clsKeyInfo, "key", "F"); // 获取KeyInfo类的key变量ID，浮点数类型 // 整数类型I
    for (size_t i=0;i<red_time.size();i++){
        obj = env->NewObject(clsKeyInfo, consID); // 生成一个对象
        env->SetFloatField(obj, timeID, (jfloat)red_time[i]); // 设置对象值域1 // env->SetObjectField(obj, timeID, env->NewStringUTF("disk")); // 设置对象值域1
        env->SetFloatField(obj, keyID, (jfloat)red_keys[i]); // 设置对象值域2 // env->SetIntField(obj, keyID, (jint)i); // 设置对象值域2
        env->SetObjectArrayElement(infos, i, obj); // 填充对象到返回array
    }

    return infos;
}

JNIEXPORT jfloat JNICALL Java_com_beidousat_score_NdkJniUtil_getScore
        (JNIEnv *env, jobject obj){
    return score_obj.get_score();
}