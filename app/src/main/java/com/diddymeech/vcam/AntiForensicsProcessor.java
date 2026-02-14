package com.diddymeech.vcam;

import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * Anti-forensics processor that applies noise and lighting adjustments to video streams.
 */
public class AntiForensicsProcessor {
    
    // Shader program handles
    private int mProgram = 0;
    private int mPositionHandle = -1;
    private int mTextureCoordHandle = -1;
    private int mSamplerHandle = -1;
    private int mNoiseIntensityHandle = -1;
    private int mBrightnessHandle = -1;
    private int mContrastHandle = -1;
    
    // Matrix for transformations
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    
    // Shader source code
    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;\n" +
            "attribute vec4 vPosition;\n" +
            "attribute vec2 aTextureCoord;\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main() {\n" +
            "    gl_Position = uMVPMatrix * vPosition;\n" +
            "    vTextureCoord = aTextureCoord;\n" +
            "}\n";
    
    private static final String FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying vec2 vTextureCoord;\n" +
            "uniform samplerExternalOES sTexture;\n" +
            "uniform float uNoiseIntensity;\n" +
            "uniform float uBrightness;\n" +
            "uniform float uContrast;\n" +
            "\n" +
            "// Simple noise function\n" +
            "float random(vec2 st) {\n" +
            "    return fract(sin(dot(st.xy, vec2(12.9898,78.233))) * 43758.5453123);\n" +
            "}\n" +
            "\n" +
            "// Apply noise to pixel\n" +
            "vec3 applyNoise(vec3 color) {\n" +
            "    vec2 st = vTextureCoord;\n" +
            "    float noise = random(st * 10.0);\n" +
            "    return color + (noise - 0.5) * uNoiseIntensity;\n" +
            "}\n" +
            "\n" +
            "void main() {\n" +
            "    vec4 texColor = texture2D(sTexture, vTextureCoord);\n" +
            "    vec3 processedColor = texColor.rgb;\n" +
            "\n" +
            "    // Apply noise\n" +
            "    processedColor = applyNoise(processedColor);\n" +
            "\n" +
            "    // Apply brightness and contrast adjustments\n" +
            "    processedColor = (processedColor - 0.5) * uContrast + 0.5 + uBrightness;\n" +
            "\n" +
            "    gl_FragColor = vec4(processedColor, texColor.a);\n" +
            "}\n";
    
    public AntiForensicsProcessor() {
        // Initialize OpenGL context and shaders
        initShaders();
    }
    
    private void initShaders() {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
        
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
        
        // Get handles to attributes and uniforms
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        mSamplerHandle = GLES20.glGetUniformLocation(mProgram, "sTexture");
        mNoiseIntensityHandle = GLES20.glGetUniformLocation(mProgram, "uNoiseIntensity");
        mBrightnessHandle = GLES20.glGetUniformLocation(mProgram, "uBrightness");
        mContrastHandle = GLES20.glGetUniformLocation(mProgram, "uContrast");
        
        // Set up matrices
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }
    
    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
    
    /**
     * Apply anti-forensics processing to a frame
     */
    public void processFrame(float noiseIntensity, float brightness, float contrast) {
        if (mProgram != 0) {
            GLES20.glUseProgram(mProgram);
            
            // Set uniform values for noise and lighting adjustments
            GLES20.glUniform1f(mNoiseIntensityHandle, noiseIntensity);
            GLES20.glUniform1f(mBrightnessHandle, brightness);
            GLES20.glUniform1f(mContrastHandle, contrast);
        }
    }
    
    /**
     * Clean up OpenGL resources
     */
    public void release() {
        if (mProgram != 0) {
            GLES20.glDeleteProgram(mProgram);
            mProgram = 0;
        }
    }
}
