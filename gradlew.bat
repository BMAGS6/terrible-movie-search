@echo off
setlocal
set "APP_HOME=%~dp0"
set "WRAPPER_JAR=%APP_HOME%gradle\wrapper\gradle-wrapper.jar"
if not exist "%WRAPPER_JAR%" (
    echo Downloading the official Gradle 8.10.2 wrapper JAR...
    powershell.exe -NoProfile -ExecutionPolicy Bypass -Command "$ProgressPreference='SilentlyContinue'; $u='https://services.gradle.org/distributions/gradle-8.10.2-wrapper.jar'; $p='%WRAPPER_JAR%'; Invoke-WebRequest -Uri $u -OutFile $p; if ((Get-FileHash -Path $p -Algorithm SHA256).Hash.ToLowerInvariant() -ne '2db75c40782f5e8ba1fc278a5574bab070adccb2d21ca5a6e5ed840888448046') { Remove-Item $p; exit 1 }"
    if errorlevel 1 exit /b 1
)
if defined JAVA_HOME (
    set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
) else (
    set "JAVA_EXE=java"
)
"%JAVA_EXE%" -Dorg.gradle.appname=gradlew -classpath "%WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
exit /b %ERRORLEVEL%
