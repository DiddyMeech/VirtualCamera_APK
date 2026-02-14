# ENGINEERING STANDARDS & LESSONS LEARNED

## 1. Build System (Gradle/CMake)
- **NEVER** use the local NDK on the host machine. Always rely on GitHub Actions for C++ compilation.
- When creating `CMakeLists.txt`, always include the `aosp_headers` directory to prevent `fatal error: 'system/camera.h' file not found`.
- **Do not** verify C++ builds locally using `./gradlew`. Use the `/git push` workflow to verify on the remote server.
- All repository definitions (google(), mavenCentral()) MUST be placed in settings.gradle inside a dependencyResolutionManagement block. Do NOT place them in module-level build.gradle files.
- Always create a gradle.properties file with android.useAndroidX=true for any project using modern Android libraries.

## 2. File Handling
- **NEVER** use `../` (parent directory) traversal in Python/Aider commands on Windows. It causes `pathlib` crashes.
- Always use absolute paths or copy reference files into a local `refs/` folder before reading them.
- The AndroidManifest.xml is critical. Never delete it during cleanup operations.

## 3. Xposed Module Logic
- The entry point must be defined in `assets/xposed_init`.
- `AndroidManifest.xml` must contain the `xposedmodule` meta-data tags.
- Hooking logic must handle obfuscated apps by searching for signatures (methods/strings) rather than relying on class names.

## 4. Architecture
- Keep the `VideoInjector` logic separate from the `MainHook` logic.
- Use `SurfaceTexture` interception for camera hooking.

## 5. Android Modernization
- For AGP 8.0+, NEVER define the package attribute in AndroidManifest.xml. Always define the namespace in build.gradle instead.
