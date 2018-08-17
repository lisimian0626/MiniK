LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_MODULE     := serialPort
LOCAL_SRC_FILES  :=  SerialPort.c


LOCAL_CFLAGS    +=   -DANDROID   \
	-D__LINUX__   
	











LOCAL_C_INCLUDES += \
 $(JNI_H_INCLUDE) \



LOCAL_LDLIBS    +=  -llog -ldl -lc -lm -lz 



include $(BUILD_SHARED_LIBRARY)

