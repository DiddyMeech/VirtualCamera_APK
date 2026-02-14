package com.diddymeech.vcam;

/**
 * Native interface for VCam core functionality.
 * This class provides access to C++ implementations for graphics processing and anti-debugging.
 */
public class VCamNative {
    
    static {
        // Load the native library
        System.loadLibrary("vcam-core");
    }
    
    /**
     * Initialize OpenGL context and shaders for anti-forensics processing
     */
    public static native void initOpenGL();
    
    /**
     * Apply noise to video frames (1-2% Gaussian noise)
     * @param intensity Noise intensity level (0.0f - 1.0f)
     */
    public static native void applyNoise(float intensity);
    
    /**
     * Set brightness adjustment for processed frames
     * @param brightness Brightness value (-1.0f to 1.0f)
     */
    public static native void setBrightness(float brightness);
    
    /**
     * Set contrast adjustment for processed frames  
     * @param contrast Contrast value (0.0f to 2.0f)
     */
    public static native void setContrast(float contrast);
}
