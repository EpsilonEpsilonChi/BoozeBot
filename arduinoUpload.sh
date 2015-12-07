#!/bin/bash

# Compiles and uploads Arduino sketch

# **Requires piSetup.sh be run first for Raspberry Pi**
# **Requires Arduino IDE be installed for Mac**

function echo_bright_green {
    echo -e "\033[1;32m"$@"\033[m"
}

arch=$(uname -m)
if [ "$arch" == "x86_64" ]
then
	echo "Running on computer"

	# Move Arduino code into its own folder
	echo_bright_green "Reorganizing directories for Arduino compilation..."
	cd arduinoCode/src
	mkdir sketch 
	cp ./sketch.ino ./sketch/sketch.ino
	cd ../../

	APP_PATH="/Applications/Arduino.app/Contents/MacOS/Arduino"
	DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
	SKETCH_PATH=$DIR/arduinoCode/src/sketch/sketch.ino

	echo_bright_green "Building and uploading Arduino sketch..."
	$APP_PATH --upload $SKETCH_PATH
	echo_bright_green "Done"

	# Clean up directories
	echo_bright_green "Cleaning directories..."
	cd arduinoCode/src
	rm -rf sketch
elif [ "$arch" == "armv6l" ]
then
	echo "Running on ARM (Raspberry Pi)"

	cd arduinoCode
	echo_bright_green "Building Arduino sketch..."
	sudo ino build
	echo_bright_green "Uploading sketch to board..."
	sudo ino upload
	echo_bright_green "Done"
	cd ..
fi
