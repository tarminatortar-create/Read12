@echo off
setlocal
set ROOT=%~dp0..\
if "%ANDROID_HOME%"=="" set ANDROID_HOME=C:\Users\ahsuh\AppData\Local\Android\Sdk
if "%ANDROID_SDK_ROOT%"=="" set ANDROID_SDK_ROOT=%ANDROID_HOME%
call "%ROOT%gradlew.bat" -p "%ROOT%" --no-daemon :app:assembleDebug --console=plain
copy /Y "%ROOT%app\build\outputs\apk\debug\app-debug.apk" "%ROOT%Readora-debug.apk"
echo APK: %ROOT%Readora-debug.apk
endlocal
