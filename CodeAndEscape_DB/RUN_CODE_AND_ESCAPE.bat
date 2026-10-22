@echo off
setlocal
cd /d "%~dp0"

echo ==============================================
echo      CODE AND ESCAPE - MYSQL + JDBC
 echo ==============================================

where javac >nul 2>&1
if errorlevel 1 (
  echo ERROR: javac was not found. Install/use JDK 26 and ensure Java is in PATH.
  pause
  exit /b 1
)

if not exist lib\mysql-connector-j-9.7.0.jar (
  echo JDBC driver not found. Run SETUP_MYSQL.bat first.
  pause
  exit /b 1
)

if not exist db.properties (
  echo db.user=root>db.properties
  echo db.password=CHANGE_ME>>db.properties
)

if exist out rmdir /s /q out
mkdir out

echo Compiling Java source files...
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
echo Starting Code and Escape...
echo MySQL JDBC will create/use the codeandescape database.
echo.
java -cp "out;lib\mysql-connector-j-9.7.0.jar" com.escape.MainApp

endlocal
