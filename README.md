# BoozeBot
Code for our house bartending robot, BoozeBot (work in progress)

## The Files/Folders
 - **android**: code for Android BoozeBot app
 - **api**: HTTP API for BoozeBot
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
1. Fix power integrity issues
2. Write iOS/Android apps

### Notes
- **IDEA**: make raspberry pi automatically archive a transaction history every day
 

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

## Notes for setting up for iOS modifications
Be sure to add the Firebase Pod: https://www.firebase.com/docs/ios/quickstart.html (see section 2)
