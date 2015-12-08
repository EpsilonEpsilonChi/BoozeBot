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

	# Set variables
	APP_PATH="/Applications/Arduino.app/Contents/MacOS/Arduino"
	DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
	PUMP_SKETCH_PATH=$DIR/arduinoPump/src/sketch/sketch.ino
	LED_SKETCH_PATH=$DIR/arduinoLED/src/sketch/sketch.ino

	# Move Arduino code into its own folder
	echo_bright_green "Reorganizing directories for Arduino compilation..."

	echo "  arduinoPump..."
	cd arduinoPump/src
	mkdir sketch 
	cp ./sketch.ino ./sketch/sketch.ino
	cd ../../

	echo "  arduinoLED..."
	cd arduinoLED/src
	mkdir sketch 
	cp ./sketch.ino ./sketch/sketch.ino
	cd ../../

	echo "  Done"

	# Compile and upload
	echo_bright_green "Building and uploading arduinoPump..."
	$APP_PATH --upload $PUMP_SKETCH_PATH
	echo_bright_green "Done"

	echo_bright_green "Building and uploading arduinoLED..."
	$APP_PATH --upload $LED_SKETCH_PATH
	echo_bright_green "Done"

	# Clean up directories
	echo_bright_green "Cleaning directories..."

	echo "  arduinoPump..."
	cd arduinoPump/src
	rm -rf sketch

	echo "  arduinoLED..."
	cd arduinoLED/src
	rm -rf sketch
elif [ "$arch" == "armv6l" ]
then
	echo "Running on ARM (Raspberry Pi)"

	# Build and upload arduinoPump
	cd arduinoPump
	echo_bright_green "Building arduinoPump..."
	sudo ino build
	echo_bright_green "Uploading arduinoPump to board..."
	sudo ino upload
	echo_bright_green "Done"
	cd ..

	# Build and upload arduinoLED
	cd arduinoLED
	echo_bright_green "Building arduinoLED..."
	sudo ino build
	echo_bright_green "Uploading arduinoLED to board..."
	sudo ino upload
	echo_bright_green "Done"
	cd ..
fi
