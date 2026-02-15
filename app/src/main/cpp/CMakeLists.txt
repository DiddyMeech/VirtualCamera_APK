cmake_minimum_required(VERSION 3.22.1)

project("vcam-core")

# Create the library
add_library(
        vcam-core
        SHARED
        vcam_native.cpp)

# Search for the log library (standard NDK)
find_library(
        log-lib
        log)

# Link libraries.
# NOTE: For Android, EGL and GLESv2 are linked directly, NOT via find_package.
target_link_libraries(
        vcam-core
        ${log-lib}
        android
        EGL
        GLESv2)
