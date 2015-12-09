#!/bin/bash

# Compiles and uploads Arduino sketch

# **Requires piSetup.sh be run first for Raspberry Pi**
# **Requires Arduino IDE be installed for Mac with aJson and Tlc5940 libs imported**

# ***** STILL NEEDS PORT DETECTION FOR WHEN MULTIPLE ARDUINOS ARE CONNECTED *****

function echo_green { 
	echo -e "\033[1;32m"$@"\033[m" 
}

function echo_white { 
	echo -e "\033[1;37m"$@"\033[m" 
}

function echo_red { 
	echo -e "\033[1;31m"$@"\033[m" 
}

function echo_yellow { 
	echo -e "\033[1;33m"$@"\033[m" 
}

function echo_blue { 
	echo -e "\033[1;34m"$@"\033[m" 
}

if [ $# == 0 ]; then
	echo_red "Arguments required, run './upload.sh help' for details"
	exit 1
elif [ $# != 1 ]; then
	echo_red "Only one argument may be specified at a time"
	exit 1
elif [ $1 == "help" ]; then
	echo_yellow "upload.sh help:"
	echo_white "  ./upload.sh help --> shows this super helpful screen"
	echo_white "  ./upload.sh led  --> uploads arduinoLED/src/sketch.ino to Arduino"
	echo_white "  ./upload.sh pump --> uploads arduinoPump/src/sketch.ino to Arduino"
	exit 0
else
	arch=$(uname -m)
	if [ "$arch" == "x86_64" ]
	then
		echo_yellow "Running on computer"

		# Set variables
		APP_PATH="/Applications/Arduino.app/Contents/MacOS/Arduino"
		DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
		PUMP_SKETCH_PATH=$DIR/arduinoPump/src/sketch/sketch.ino
		LED_SKETCH_PATH=$DIR/arduinoLED/src/sketch/sketch.ino

		if [ $1 == "pump" ]; then
			# Move Arduino code into its own folder
			echo_white "  Reorganizing directories for Arduino compilation..."
			cd arduinoPump/src
			mkdir sketch 
			cp ./sketch.ino ./sketch/sketch.ino
			cd ../../

			# Compile and upload
			echo_blue "  Building and uploading arduinoPump..."
			$APP_PATH --upload $PUMP_SKETCH_PATH
			echo_green "  Done"

			# Clean up directories
			echo_white "  Cleaning directories..."
			cd arduinoPump/src
			rm -rf sketch
		elif [ $1 == "led" ]; then
			# Move Arduino code into its own folder
			echo_white "  Reorganizing directories for Arduino compilation..."
			cd arduinoLED/src
			mkdir sketch 
			cp ./sketch.ino ./sketch/sketch.ino
			cd ../../

			# Compile and upload
			echo_blue "  Building and uploading arduinoLED..."
			$APP_PATH --upload $LED_SKETCH_PATH
			echo_green "  Done uploading."

			# Clean up directories
			echo_white "  Cleaning directories..."
			cd arduinoLED/src
			rm -rf sketch
		fi
	elif [ "$arch" == "armv6l" ]
	then
		echo_yellow "Running on ARM (Raspberry Pi)"

		# Build and upload arduinoPump
		cd arduinoPump
		echo_blue "  Building arduinoPump..."
		sudo ino build
		echo_blue "  Uploading arduinoPump to board..."
		sudo ino upload
		echo_green "  Done"
		cd ..

		# Build and upload arduinoLED
		cd arduinoLED
		echo_blue "  Building arduinoLED..."
		sudo ino build
		echo_blue "  Uploading arduinoLED to board..."
		sudo ino upload
		echo_green "  Done"
		cd ..
	fi
fi

