#!/bin/bash

# insola.sh - Script to start the Isola game on Linux

# Check if Maven is installed
if ! command -v mvn &> /dev/null
then
    echo "Maven is not installed or not in PATH"
    echo "Please install Maven to run this game"
    exit 1
fi

# Run the Isola game using Maven exec plugin
echo "Starting Isola game..."
mvn exec:java