cmake_minimum_required(VERSION 3.22.1)
project(vcam_core)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Add the library
add_library(vcam-core SHARED
    vcam_native.cpp
)

# Find required packages
find_package(EGL REQUIRED)
find_package(GLESv2 REQUIRED)

# Link libraries
target_link_libraries(vcam-core
    android
    log
    EGL
    GLESv2
)
