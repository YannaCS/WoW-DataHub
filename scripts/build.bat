@echo off
REM WoW DataHub Build Script for Windows
REM This script compiles Java files and creates deployable WAR file

echo ===============================================
echo WoW DataHub Build Script
echo ===============================================

REM Set variables
set PROJECT_ROOT=%~dp0..
set SRC_DIR=%PROJECT_ROOT%\src\main\java
set BUILD_DIR=%PROJECT_ROOT%\build
set WEBAPP_DIR=%PROJECT_ROOT%\webapp
set LIB_DIR=%WEBAPP_DIR%\WEB-INF\lib
set WAR_FILE=%PROJECT_ROOT%\wow-datahub.war

echo Project Root: %PROJECT_ROOT%
echo Build Directory: %BUILD_DIR%
echo.

REM Create build directory
echo Creating build directory...
if not exist "%BUILD_DIR%" mkdir "%BUILD_DIR%"

REM Check if Java is available
echo Checking Java installation...
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or higher
    pause
    exit /b 1
)

REM Check if required JARs exist
echo Checking required dependencies...
if not exist "%LIB_DIR%\mysql-connector-j-8.4.0.jar" (
    echo ERROR: Missing mysql-connector-j-8.4.0.jar
    echo Please ensure all required JARs are in webapp\WEB-INF\lib\
    pause
    exit /b 1
)

if not exist "%LIB_DIR%\jackson-databind-2.15.2.jar" (
    echo ERROR: Missing jackson-databind-2.15.2.jar
    echo Please download all required JARs as specified in the setup guide
    pause
    exit /b 1
)

REM Compile Java files
echo.
echo Compiling Java source files...
javac -cp "%LIB_DIR%\*" -d "%BUILD_DIR%" "%SRC_DIR%\game\**\*.java"

if errorlevel 1 (
    echo ERROR: Compilation failed
    echo Please check the error messages above
    pause
    exit /b 1
)

echo Compilation successful!

REM Create WAR file
echo.
echo Creating WAR file...
if exist "%WAR_FILE%" del "%WAR_FILE%"

REM Change to webapp directory to create proper WAR structure
cd /d "%WEBAPP_DIR%"
jar -cvf "%WAR_FILE%" . -C "%BUILD_DIR%" .

if errorlevel 1 (
    echo ERROR: WAR file creation failed
    pause
    exit /b 1
)

echo WAR file created successfully: %WAR_FILE%

REM Display build information
echo.
echo ===============================================
echo Build Summary
echo ===============================================
echo Source Directory: %SRC_DIR%
echo Build Directory: %BUILD_DIR%
echo WAR File: %WAR_FILE%
echo.

REM Check WAR file size
for %%I in ("%WAR_FILE%") do echo WAR File Size: %%~zI bytes

echo.
echo Build completed successfully!
echo.
echo Next steps:
echo 1. Deploy WAR file to Tomcat webapps directory
echo 2. Start Tomcat server
echo 3. Access application at http://localhost:8080/wow-datahub
echo.

pause