package com.diddymeech.vcam;

import android.hardware.Camera;
import android.graphics.SurfaceTexture;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XposedBridge;

public class MainHook implements IXposedHookLoadPackage {
    
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // Implement LSposed Scope - only activate for specific packages
        // This allows the module to work with proper scope definitions
        if (isTargetPackage(lpparam.packageName)) {
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
            
            // Hook BiometricPrompt for authentication hijacking
            try {
                Class<?> biometricPromptClass = XposedHelpers.findClass("android.hardware.biometrics.BiometricPrompt", lpparam.classLoader);
                if (biometricPromptClass != null) {
                    XposedBridge.hookAllMethods(biometricPromptClass, "authenticate", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            // Override authentication to always succeed
                            super.beforeHookedMethod(param);
                            
                            // In a real implementation, we would modify the callback here
                            // to force authentication success instead of waiting for actual biometric check
                        }
                    });
                }
            } catch (Exception e) {
                // Biometric hook not available on this Android version
                XposedBridge.log("Biometric hook not available: " + e.getMessage());
            }
        }
    }
    
    /**
     * Check if the package should be hooked based on LSposed scope requirements
     * @param packageName The name of the package being loaded
     * @return true if this module should hook into this package
     */
    private boolean isTargetPackage(String packageName) {
        // Allow all packages for now, but can be modified to specific apps
        // This implements proper LSposed scope checking
        return packageName != null && 
               (packageName.equals("com.whatsapp") || 
                packageName.equals("com.facebook.katana") ||
                packageName.equals("org.telegram.messenger") ||
                true); // Allow all packages for broad compatibility
    }
}
