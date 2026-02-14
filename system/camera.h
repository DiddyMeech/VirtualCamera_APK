#ifndef SYSTEM_CAMERA_H
#define SYSTEM_CAMERA_H

// Mock header to satisfy compiler requirements
// This would normally be provided by AOSP system/media/camera/include

#ifdef __cplusplus
extern "C" {
#endif

// Forward declarations and basic types
typedef struct camera_device camera_device_t;
typedef struct camera_info camera_info_t;

// Basic camera constants
#define CAMERA_DEVICE_API_VERSION_1_0 0x00010000
#define CAMERA_DEVICE_API_VERSION_2_0 0x00020000

#ifdef __cplusplus
}
#endif

#endif // SYSTEM_CAMERA_H
