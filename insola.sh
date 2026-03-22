#!/bin/bash

# insola.sh - Script to start the Isola game on Linux

# Define the expected JAR file path
JAR_FILE="target/IsolaGem-1.0.0.jar"

# Function to check Java version
check_java_version() {
    if ! command -v java &> /dev/null; then
        echo "Java is not installed or not in PATH"
        echo "Please install Java 21 or higher to run this game"
        exit 1
    fi
    
    # Get Java version
    JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2)
    
    # Extract major version number
    JAVA_MAJOR=$(echo "$JAVA_VERSION" | cut -d'.' -f1)
    
    # Check if Java version is 21 or higher
    if [ "$JAVA_MAJOR" -lt 21 ]; then
        echo "Java version $JAVA_MAJOR detected - NOT COMPATIBLE"
        echo "This game requires Java 21 or higher"
        exit 1
    else
        echo "Java version $JAVA_MAJOR detected - OK"
    fi
}

# Check if the JAR file exists
if [ -f "$JAR_FILE" ]; then
    echo "Found release JAR file: $JAR_FILE"
    
    # Check Java version
    check_java_version
    
    # Start the game using the JAR file
    echo "Starting Isola game from JAR file..."
    java -jar "$JAR_FILE"
else
    echo "Release JAR file not found: $JAR_FILE"
    echo "Checking if Maven is installed to build the game..."
    
    # Check if Maven is installed
    if ! command -v mvn &> /dev/null; then
        echo "Maven is not installed or not in PATH"
        echo "Please install Maven to build and run this game"
        exit 1
    fi
    
    # Check Java version
    check_java_version
    
    # Start the game using Maven
    echo "Starting Isola game using Maven..."
    mvn exec:java
fi