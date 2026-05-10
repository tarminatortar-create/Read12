@echo off
setlocal
set ROOT=%~dp0..\
if "%ANDROID_HOME%"=="" set ANDROID_HOME=C:\Users\ahsuh\AppData\Local\Android\Sdk
if "%ANDROID_SDK_ROOT%"=="" set ANDROID_SDK_ROOT=%ANDROID_HOME%
if not exist "%ROOT%readora-dev-release.jks" (
  keytool -genkeypair -v -keystore "%ROOT%readora-dev-release.jks" -storepass readora-dev-pass -keypass readora-dev-pass -alias readora -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Readora Dev,O=Readora,C=US"
)
call "%ROOT%gradlew.bat" -p "%ROOT%" --no-daemon :app:assembleRelease --console=plain -PREADORA_STORE_FILE="%ROOT%readora-dev-release.jks" -PREADORA_STORE_PASSWORD=readora-dev-pass -PREADORA_KEY_ALIAS=readora -PREADORA_KEY_PASSWORD=readora-dev-pass
copy /Y "%ROOT%app\build\outputs\apk\release\app-release.apk" "%ROOT%Readora-release.apk"
echo APK: %ROOT%Readora-release.apk
endlocal
