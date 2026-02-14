package com.diddymeech.vcam;

import android.hardware.Camera;
import android.graphics.SurfaceTexture;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // Hook for the target package - currently allowing all packages
        // You can modify this condition to filter for specific packages like "com.whatsapp"
        if (lpparam.packageName.equals("com.whatsapp") || true) {  // Allow all for now
            // Hook android.hardware.Camera.setPreviewTexture method
            XposedBridge.hookMethod(
                Camera.class.getMethod("setPreviewTexture", SurfaceTexture.class),
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        // Get the SurfaceTexture that was passed to setPreviewTexture
                        SurfaceTexture surfaceTexture = (SurfaceTexture) param.args[0];
                        
                        // Pass it to our VideoInjector class to start rendering fake video
                        VideoInjector.injectVideo(surfaceTexture);
                    }
                }
            );
        }
    }
}
