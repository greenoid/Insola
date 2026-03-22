@echo off
REM insola.bat - Script to start the Isola game on Windows

REM Define the expected JAR file path
set JAR_FILE=target\IsolaGem-1.0.0.jar

REM Check if the JAR file exists
if exist "%JAR_FILE%" (
    echo Found release JAR file: %JAR_FILE%
    
    REM Check Java version
    java -version 2>&1 | findstr /i "version" > java_version.txt
    set /p JAVA_VERSION=<java_version.txt
    del java_version.txt
    
    REM Extract major version number
    for /f "tokens=3 delims=. " %%a in ("%JAVA_VERSION%") do (
        set JAVA_MAJOR=%%a
    )
    
    REM Remove any quotes from the version number
    set JAVA_MAJOR=%JAVA_MAJOR:"=%
    
    REM Check if Java version is 21 or higher
    if %JAVA_MAJOR% GEQ 21 (
        echo Java version %JAVA_MAJOR% detected - OK
        echo Starting Isola game from JAR file...
        java -jar "%JAR_FILE%"
    ) else (
        echo Java version %JAVA_MAJOR% detected - NOT COMPATIBLE
        echo This game requires Java 21 or higher
        pause
        exit /b 1
    )
) else (
    echo Release JAR file not found: %JAR_FILE%
    echo Checking if Maven is installed to build the game...
    
    REM Check if Maven is installed
    mvn -version >nul 2>&1
    if %errorlevel% neq 0 (
        echo Maven is not installed or not in PATH
        echo Please install Maven to build and run this game
        pause
        exit /b 1
    )
    
    REM Check Java version
    java -version 2>&1 | findstr /i "version" > java_version.txt
    set /p JAVA_VERSION=<java_version.txt
    del java_version.txt
    
    REM Extract major version number
    for /f "tokens=3 delims=. " %%a in ("%JAVA_VERSION%") do (
        set JAVA_MAJOR=%%a
    )
    
    REM Remove any quotes from the version number
    set JAVA_MAJOR=%JAVA_MAJOR:"=%
    
    REM Check if Java version is 21 or higher
    if %JAVA_MAJOR% GEQ 21 (
        echo Java version %JAVA_MAJOR% detected - OK
        echo Starting Isola game using Maven...
        mvn exec:java
    ) else (
        echo Java version %JAVA_MAJOR% detected - NOT COMPATIBLE
        echo This game requires Java 21 or higher
        pause
        exit /b 1
    )
)

REM Pause to keep the window open after the game exits
pause