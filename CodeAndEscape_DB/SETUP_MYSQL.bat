@echo off
setlocal
cd /d "%~dp0"

echo ==============================================
echo   CODE AND ESCAPE - MYSQL JDBC SETUP
echo ==============================================

where java >nul 2>&1
if errorlevel 1 (
  echo ERROR: Java was not found in PATH.
  pause
  exit /b 1
)
where javac >nul 2>&1
if errorlevel 1 (
  echo ERROR: javac was not found in PATH.
  pause
  exit /b 1
)

if not exist lib mkdir lib
if not exist lib\mysql-connector-j-9.7.0.jar (
  echo Downloading MySQL Connector/J 9.7.0...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.7.0/mysql-connector-j-9.7.0.jar' -OutFile 'lib\mysql-connector-j-9.7.0.jar'"
  if errorlevel 1 (
    echo.
    echo Automatic download failed.
    echo Download the official MySQL Connector/J JAR and place it in lib\
    echo with the filename mysql-connector-j-9.7.0.jar
    pause
    exit /b 1
  )
)

echo.
echo JDBC driver found.
echo.
if not exist db.properties (
  copy /Y db.properties.example db.properties >nul
)

if not exist db.properties (
  echo db.user=root>db.properties
  echo db.password=CHANGE_ME>>db.properties
)

echo Compiling project...
if exist out rmdir /s /q out
mkdir out
dir /s /b src\com\escape\*.java > sources.txt
javac -encoding UTF-8 -cp "lib\mysql-connector-j-9.7.0.jar" -d out @sources.txt
if errorlevel 1 (
  del sources.txt >nul 2>&1
  echo BUILD FAILED.
  pause
  exit /b 1
)
del sources.txt >nul 2>&1
xcopy /E /I /Y src\assets out\assets >nul

echo.
echo SETUP COMPLETE.
echo Edit db.properties with your MySQL password if required.
echo Then run RUN_CODE_AND_ESCAPE.bat
pause
endlocal
