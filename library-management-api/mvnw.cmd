@echo off
setlocal
set "PROJECT_DIR=%~dp0"
set "MAVEN_VERSION=3.9.9"
set "MAVEN_DIST=%PROJECT_DIR%.mvn\apache-maven-%MAVEN_VERSION%"

if not exist "%MAVEN_DIST%\bin\mvn.cmd" (
  echo Downloading Apache Maven %MAVEN_VERSION%...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $zip='%PROJECT_DIR%.mvn\maven.zip'; New-Item -ItemType Directory -Force '%PROJECT_DIR%.mvn'; Invoke-WebRequest 'https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip' -OutFile $zip; Expand-Archive -Force $zip '%PROJECT_DIR%.mvn'; Remove-Item $zip"
  if errorlevel 1 exit /b 1
)

call "%MAVEN_DIST%\bin\mvn.cmd" %*
set "MAVEN_EXIT=%ERRORLEVEL%"
endlocal & exit /b %MAVEN_EXIT%
