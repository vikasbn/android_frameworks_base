LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

# although we use BOARD_USES_ECLAIR_LIBCAMERA (so we make it static), we also need
# the shared libcamera_client and libsurfaceflinger_client for libvendorOMX_ti_omx...

LOCAL_SRC_FILES:= \
	Camera.cpp \
	CameraParameters.cpp \
	ICamera.cpp \
	ICameraClient.cpp \
	ICameraService.cpp

LOCAL_MODULE:= libcamera_client

LOCAL_SHARED_LIBRARIES := \
	libcutils \
	libutils \
	libbinder \
	libhardware \
	libsurfaceflinger_client \
	libui

ifeq ($(TARGET_SIMULATOR),true)
    LOCAL_LDLIBS += -lpthread
endif

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_SRC_FILES:= \
	Camera.cpp \
	CameraParameters.cpp \
	ICamera.cpp \
	ICameraClient.cpp \
	ICameraService.cpp

LOCAL_MODULE:= libcamera_client

include $(BUILD_STATIC_LIBRARY)

