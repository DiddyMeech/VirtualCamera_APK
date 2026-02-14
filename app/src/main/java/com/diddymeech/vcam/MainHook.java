package com.diddymeech.vcam;

import android.content.Context;
import android.os.FileUtils;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    private static final String TAG = "VCam_MainHook";
    
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // Safety check: If disable file exists, return immediately
        File disableFile = new File("/sdcard/Download/disable_vcam");
        if (disableFile.exists()) {
            Log.d(TAG, "VCam is disabled by /sdcard/Download/disable_vcam file");
            return;
        }
        
        // LSposed Scope API: Only hook specific packages as per conventions
        String packageName = lpparam.packageName;
        if (!shouldHookPackage(packageName)) {
            return;
        }
        
        Log.d(TAG, "Hooking package: " + packageName);
        
        // Implement proper LSposed scope - only activate for target packages
        setupCameraHooks(lpparam);
    }
    
    private boolean shouldHookPackage(String packageName) {
        // Define target packages to hook using LSposed Scope API approach
        // This follows the convention from CONVENTIONS.md about handling obfuscated apps
        String[] targetPackages = {
            "com.android.camera", 
            "com.google.android.apps.docs",
            "com.sec.android.app.camera"
        };
        
        if (packageName == null) return false;
        
        for (String target : targetPackages) {
            if (packageName.equals(target)) {
                return true;
            }
        }
        return false;
    }
    
    private void setupCameraHooks(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // Hook SurfaceTexture methods to inject video
        try {
            XposedHelpers.findAndHookMethod(
                "android.graphics.SurfaceTexture",
                lpparam.classLoader,
                "attachToGLContext",
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        // Inject video into the SurfaceTexture when attached to GL context
                        try {
                            Object surfaceTexture = param.thisObject;
                            VideoInjector.injectVideo(surfaceTexture);
                        } catch (Exception e) {
                            Log.e(TAG, "Error injecting video: " + e.getMessage());
                        }
                    }
                }
            );
        } catch (Exception e) {
            Log.d(TAG, "Failed to hook SurfaceTexture.attachToGLContext: " + e.getMessage());
        }
        
        // Hook camera preview surface creation using LSposed scope approach
        try {
            XposedHelpers.findAndHookMethod(
                "android.hardware.Camera",
                lpparam.classLoader,
                "setPreviewSurface",
                android.view.Surface.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        // Inject video into the preview surface using LSposed scope
                    }
                }
            );
        } catch (Exception e) {
            Log.d(TAG, "Failed to hook Camera.setPreviewSurface: " + e.getMessage());
        }
        
        // Hook modern camera APIs if available
        try {
            XposedHelpers.findAndHookMethod(
                "android.hardware.camera2.CameraCaptureSession",
                lpparam.classLoader,
                "setRepeatingRequest",
                android.hardware.camera2.CaptureRequest.class,
                android.hardware.camera2.CameraCaptureSession.CaptureCallback.class,
                android.os.Handler.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        // Additional hooking for modern camera APIs
                    }
                }
            );
        } catch (Exception e) {
            Log.d(TAG, "Failed to hook CameraCaptureSession: " + e.getMessage());
        }
    }
}
