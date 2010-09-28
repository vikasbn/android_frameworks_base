LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

# although we use BOARD_USES_ECLAIR_LIBCAMERA (so we make it static), we also need
# the shared libcamera_client and libsurfaceflinger_client for libvendorOMX_ti_omx...

LOCAL_SRC_FILES:= \
	ISurfaceComposer.cpp \
	ISurface.cpp \
	ISurfaceFlingerClient.cpp \
	LayerState.cpp \
	SharedBufferStack.cpp \
	Surface.cpp \
	SurfaceComposerClient.cpp

LOCAL_MODULE:= libsurfaceflinger_client

LOCAL_SHARED_LIBRARIES := \
	libcutils \
	libutils \
	libbinder \
	libhardware \
	libui

ifeq ($(TARGET_SIMULATOR),true)
    LOCAL_LDLIBS += -lpthread
endif

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_SRC_FILES:= \
	ISurfaceComposer.cpp \
	ISurface.cpp \
	ISurfaceFlingerClient.cpp \
	LayerState.cpp \
	SharedBufferStack.cpp \
	Surface.cpp \
	SurfaceComposerClient.cpp

LOCAL_MODULE:= libsurfaceflinger_client

include $(BUILD_STATIC_LIBRARY)

