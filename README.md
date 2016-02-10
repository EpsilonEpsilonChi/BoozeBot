# BoozeBot
Code for our house bartending robot, BoozeBot (work in progress)

## The Files/Folders
 - **android**: code for Android BoozeBot app
 - **arduinoLED**: code and libraries for Arduino that controls the PSU, LCD, button, and all LEDs
 - **arduinoPump**: code and libraries for Arduino that controls the pump relays
 - **helpers**: helper functions for pullTransactionLogs.sh
 - **iOS**: code for iOS BoozeBot app
 - **testSite**: code for BoozeBot testing website
 - **piSetup.sh**: sets up Raspberry Pi for uploading sketches to Arduinos using upload.sh
 - **pullTransactionLogs.sh**: pulls transaction info from each user and puts it into an Excel document for analysis
 - **raspPi.js**: code to run on Raspberry Pi, communication layer between Firebase and Arduinos
 - **upload.sh**: allows for easy upload of code to LED or Pump Arduino

## To-Do
1. Rewire LEDs so that power integrity issues stop being annoying
2. Make iOS/Android apps more feature rich

## Notes

### BoozeBot Rev. 2 Ideas
- Mount bottles upside down, use solenoids to release (will work fine if using same type of bottle)
- Make electronics chamber more waterproof (or mount somewhere other than the bottom)

### Setting up Raspberry Pi serial ports
In order to make sure the Arduinos are assigned to their own serial port at boot (so they don't get mixed up), magic is required. 
- Get the short ID of each Arduino by plugging each one in seperately and running: 
```sudo udevadm info --query=property --name=/dev/ttyACM0 | grep SERIAL```. Copy the ```ID_SERIAL_SHORT```, which is also required for raspPi.js.
- Once you have the short ID for each Arduino, create a new rules file with: ```sudo nano /etc/udev/rules.d/99-usbserial.rules```. Inside this file, add the following two lines and save:

```
 ACTION=="add",ENV{ID_BUS}=="usb",ENV{ID_SERIAL_SHORT}=="<SHORT SERIAL FOR LED ARDUINO HERE>",SYMLINK+="ttyLEDArduino"
 ACTION=="add",ENV{ID_BUS}=="usb",ENV{ID_SERIAL_SHORT}=="<SHORT SERIAL FOR PUMP ARDUINO HERE>",SYMLINK+="ttyPumpArduino"
```

This will allow upload.sh to upload to each Arduino correctly every time.

### Setting up for iOS modifications
Be sure to add the Firebase Pod: https://www.firebase.com/docs/ios/quickstart.html (see section 2)
