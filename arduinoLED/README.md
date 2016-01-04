# arduinoLED
Code to be uploaded to Arduino that controls the LEDs, button, LCD, and PSU. To upload, run `./upload.sh led` in main BoozeBot directory.

## Required Libraries 
 - [aJson](https://github.com/interactive-matter/aJson): allows Arduino to read serialized JSON sent from Rasp Pi
 - [SimpleTimer](http://playground.arduino.cc/Code/SimpleTimer): simple timer library used for BoozeBot auto-shutoff feature
 - [Tlc5940](https://github.com/PaulStoffregen/Tlc5940): allows use of TLC5940 PWM LED multiplexer chips used for LED control
 
## Files/Folders
 - `lib`: contains necessary library files for compilation
 - `src`: contains sketch.ino file that is uploaded to the Arduino
 
