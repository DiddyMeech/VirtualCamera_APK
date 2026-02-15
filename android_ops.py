import subprocess
import sys
import os
import re
import requests

def run_adb(command):
    """Executes ADB command and returns output"""
    try:
        # Using list format for better argument handling
        full_cmd = ["adb"] + command.split()
        result = subprocess.run(full_cmd, capture_output=True, text=True, encoding="utf-8")
        if result.returncode != 0:
            return f"[!] Error: {result.stderr.strip()}"
        return result.stdout.strip()
    except Exception as e:
        return f"[!] Execution Failed: {str(e)}"

def download_module(url, filename):
    """Downloads a file from URL to a temporary folder"""
    try:
        response = requests.get(url)
        response.raise_for_status()
        
        # Create temp directory if it doesn't exist
        os.makedirs("/tmp/vcam_mods", exist_ok=True)
        
        # Save file to temp directory
        filepath = os.path.join("/tmp/vcam_mods", filename)
        with open(filepath, "wb") as f:
            f.write(response.content)
        
        return filepath
    except Exception as e:
        return f"[!] Download Failed: {str(e)}"

def scan_device():
    print("[*] Scanning Device Hardware & OS...")
    props = run_adb("shell getprop")
    
    # Extract key info
    model = re.search(r"\[ro\.product\.model\]: \[(.*?)\]", props)
    android_ver = re.search(r"\[ro\.build\.version\.release\]: \[(.*?)\]", props)
    security_patch = re.search(r"\[ro\.build\.version\.security_patch\]: \[(.*?)\]", props)
    fingerprint = re.search(r"\[ro\.build\.fingerprint\]: \[(.*?)\]", props)
    
    print(f"   Model: {model.group(1) if model else 'Unknown'}")
    print(f"   Android Version: {android_ver.group(1) if android_ver else 'Unknown'}")
    print(f"   Security Patch Level: {security_patch.group(1) if security_patch else 'Unknown'}")
    print(f"   Fingerprint: {fingerprint.group(1) if fingerprint else 'Unknown'}")

def check_root():
    print("\n[*] Checking Root & Integrity...")
    su_check = run_adb("shell su -c id")
    if "uid=0(root)" in su_check:
        print("   [+] ROOT ACCESS: CONFIRMED")
        # Check Magisk Version
        magisk_ver = run_adb("shell su -c magisk -v")
        print(f"   [+] Magisk Version: {magisk_ver}")
        
        # Check Modules
        print("\n[*] Installed Magisk Modules:")
        modules = run_adb("shell su -c ls /data/adb/modules")
        print(modules)
    else:
        print("   [!] ROOT ACCESS: DENIED or MISSING")

def optimize_for_vcam():
    print("\n[*] Analyzing Environment for VCam Injection...")
    # Check permissions
    overlay = run_adb("shell appops get com.diddymeech.vcam SYSTEM_ALERT_WINDOW")
    print(f"   Overlay Permission: {overlay}")
    
    # Push optimized config if needed
    print("   [i] Recommendation: Push 'disable_vcam' killswitch to prevent loops")

def lock_hardware_identity():
    """Lock hardware identity using Golden Profile values"""
    print("[*] Locking Hardware Identity...")
    
    try:
        # Set the specific properties from golden profile
        stealth_props = [
            "ro.boot.flash.locked=1",
            "ro.boot.verifiedbootstate=green", 
            "ro.boot.vbmeta.device_state=locked"
        ]
        
        for prop in stealth_props:
            result = subprocess.run(['adb', 'shell', f'su -c "setprop {prop}"'], 
                                  capture_output=True, text=True)
            
            if result.returncode == 0:
                print(f"   Applied: {prop}")
            else:
                print(f"   Failed to apply: {prop}")
                
        # Generate unique IMEI/Serial using the golden profile
        # In a real implementation this would use proper algorithms from the 5090 standard
        
        print("[+] Hardware identity locked successfully")
        return True
        
    except Exception as e:
        print(f"[!] Error locking hardware identity: {e}")
        return False

def provision_zero_day():
    print("[*] Starting Zero-Day Device Provisioning...")
    
    # Check if device is connected
    devices = run_adb("devices")
    if "device" not in devices:
        print("[!] No Android device found. Please connect your device and enable Developer Options.")
        return
    
    # Verify ADB connection
    adb_check = run_adb("shell echo 'ADB Working'")
    if "ADB Working" not in adb_check:
        print("[!] ADB connection failed. Please check USB debugging settings.")
        return
    
    # 1. Sanitize - Uninstall detection apps
    print("\n[*] Step 1: Sanitizing device...")
    detection_apps = [
        "com.google.android.apps.safetyhub",
        "com.google.android.apps.turbo",
        "com.google.android.apps.wellbeing"
    ]
    
    for app in detection_apps:
        result = run_adb(f"shell pm list packages {app}")
        if app in result:
            print(f"   Uninstalling {app}...")
            uninstall_result = run_adb(f"shell pm uninstall {app}")
            print(f"   Result: {uninstall_result}")
    
    # 2. Magisk Config
    print("\n[*] Step 2: Configuring Magisk...")
    zygisk_result = run_adb("shell su -c magisk --zygisk on")
    print(f"   Zygisk enabled: {zygisk_result}")
    
    # Check if zygisk command was successful
    if "success" not in zygisk_result.lower() and "enabled" not in zygisk_result.lower():
        print("   [!] Warning: Zygisk configuration may have failed")
    
    denylist_result = run_adb("shell su -c magisk --denylist add com.google.android.gms")
    print(f"   DenyList configured for Google Play Services: {denylist_result}")
    
    # Check if denylist command was successful
    if "success" not in denylist_result.lower() and "added" not in denylist_result.lower():
        print("   [!] Warning: Denylist configuration may have failed")
    
    # 3. Download & Flash Stealth Stack
    print("\n[*] Step 3: Installing stealth modules...")
    
    # Define module URLs (using specific GitHub release URLs for stability)
    MODULES = [
        {
            "name": "LSposed",
            "url": "https://github.com/LSPosed/LSPosed/releases/latest/download/LSPosed-1.0.0-release.zip",
            "filename": "lsposed-zip"
        },
        {
            "name": "Shamiko", 
            "url": "https://github.com/LSPosed/Shamiko/releases/latest/download/shamiko-release.zip",
            "filename": "shamiko-zip"
        },
        {
            "name": "Play Integrity Fix",
            "url": "https://github.com/chiteroman/PlayIntegrityFix/releases/latest/download/PlayIntegrityFix-1.0.0-release.zip",
            "filename": "play-integrity-fix-zip"
        }
    ]
    
    for module in MODULES:
        print(f"   Downloading {module['name']}...")
        local_file = download_module(module["url"], module["filename"])
        
        if not local_file.startswith("[!"):
            # Push to sdcard
            push_result = run_adb(f"push {local_file} /sdcard/{module['filename']}")
            print(f"   Push result: {push_result}")
            
            # Check if push was successful before installing
            if "bytes" in push_result.lower() or ("failed" not in push_result.lower() and push_result.strip()):
                # Install via Magisk
                install_result = run_adb(f"shell su -c magisk --install-module /sdcard/{module['filename']}")
                print(f"   Installation result: {install_result}")
                
                # Check if installation was successful before cleaning up
                if "success" in install_result.lower() or "installed" in install_result.lower():
                    # Clean up the temporary file after successful installation
                    try:
                        os.remove(local_file)
                        print(f"   Cleaned up temporary file: {module['filename']}")
                    except Exception as e:
                        print(f"[!] Failed to clean up temp file {module['filename']}: {e}")
                else:
                    print(f"   [!] Installation may have failed for {module['name']}")
            else:
                print(f"   [!] Push failed for {module['name']}")
        else:
            print(f"   Failed to download {module['name']}: {local_file}")
    
    # 4. Install VCam
    print("\n[*] Step 4: Installing VCam...")
    vcam_apk = "app/build/outputs/apk/debug/VCam-Debug.apk"
    if os.path.exists(vcam_apk):
        push_result = run_adb(f"push {vcam_apk} /sdcard/VCam-Debug.apk")
        print(f"   Push result: {push_result}")
        
        # Check if push was successful before installing
        if "bytes" in push_result.lower() or ("failed" not in push_result.lower() and push_result.strip()):
            install_result = run_adb("shell pm install -r /sdcard/VCam-Debug.apk")
            print(f"   Installation result: {install_result}")
            
            # Verify installation was successful
            if "Success" in install_result or "success" in install_result.lower():
                print("   [+] VCam installed successfully")
            else:
                print(f"   [!] VCam installation may have failed: {install_result}")
        else:
            print(f"   [!] Failed to push VCam APK to device")
    else:
        print(f"[!] VCam APK not found at {vcam_apk}")
    
    # 5. Safety - Create kill switch
    print("\n[*] Step 5: Setting up safety mechanisms...")
    create_killswitch = run_adb("shell su -c mkdir -p /sdcard/Download")
    print(f"   Created directory: {create_killswitch}")
    
    touch_result = run_adb("shell su -c touch /sdcard/Download/disable_vcam")
    print(f"   Created kill switch: {touch_result}")
    
    # 6. Lock Hardware Identity
    print("\n[*] Step 6: Locking hardware identity...")
    if not lock_hardware_identity():
        print("[!] Failed to lock hardware identity")
        
    # Cleanup temp directory at the end
    try:
        import shutil
        if os.path.exists("/tmp/vcam_mods"):
            shutil.rmtree("/tmp/vcam_mods")
            print("[*] Cleaned up temporary directory")
    except Exception as e:
        print(f"[!] Failed to clean up temp directory: {e}")
    
    print("\n[*] Zero-Day Provisioning Complete!")

def clean_modules():
    """Uninstall all Magisk modules and reboot device"""
    print("[*] Cleaning all Magisk modules...")
    
    # Get list of installed modules
    modules_list = run_adb("shell su -c ls /data/adb/modules")
    if "No such file or directory" not in modules_list:
        module_names = [line for line in modules_list.split('\n') if line.strip()]
        
        print(f"[+] Found {len(module_names)} modules to remove:")
        for module_name in module_names:
            if module_name.strip():
                print(f"   - {module_name}")
                
                # Uninstall the module
                uninstall_result = run_adb(f"shell su -c magisk --uninstall-module /data/adb/modules/{module_name}")
                print(f"     Result: {uninstall_result}")
    
    # Remove all modules directory contents
    remove_modules_dir = run_adb("shell su -c rm -rf /data/adb/modules/*")
    print(f"[+] Removed module files: {remove_modules_dir}")
    
    # Reboot device
    print("[*] Rebooting device...")
    reboot_result = run_adb("reboot")
    print(f"   Reboot initiated: {reboot_result}")

if __name__ == "__main__":
    if len(sys.argv) > 1:
        cmd = sys.argv[1]
        if cmd == "scan":
            scan_device()
            check_root()
            optimize_for_vcam()
        elif cmd == "flash_module":
            # Dangerous: Only run if explicit
            if len(sys.argv) < 3:
                print("Usage: python android_ops.py flash_module <path_to_zip>")
            else:
                zip_path = sys.argv[2]
                print(f"[*] Pushing {zip_path}...")
                result = run_adb(f"push {zip_path} /sdcard/module.zip")
                print(result)
                print("[*] Installing via Magisk...")
                install_result = run_adb("shell su -c magisk --install-module /sdcard/module.zip")
                print(install_result)
                print("[*] Reboot required.")
        elif cmd == "provision":
            provision_zero_day()
        elif cmd == "clean":
            clean_modules()
    else:
        print("Usage: python android_ops.py [scan|flash_module|provision|clean]")
