@echo off
REM insola.bat - Script to start the Isola game on Windows

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Maven is not installed or not in PATH
    echo Please install Maven to run this game
    pause
    exit /b 1
)

REM Run the Isola game using Maven exec plugin
echo Starting Isola game...
mvn exec:java

REM Pause to keep the window open after the game exits
pause