var async      = require("async");
var Firebase   = require("firebase");
var SerialPort = require("serialport");
var colors     = require("colors/safe");
var Queue      = require("firebase-queue");
var exec       = require('child_process').exec;

// Configuration variables
var verbose                 = true;
var serialBaud              = 9600;
var timeoutAfterLedOff      = 750;
var ledArduinoSerialNum     = "75437303830351917221";
var pumpArduinoSerialNum    = "754373038303515150C1";

// Serial ports
var serialPortLed;
var serialPortPump;

// Keep track of previous bottle lighting color
var colorNum = 1;

// Rasp Pi -> Arduino message structure { 
//     "msgType": <0: drink on queue, 1: set individual LED color, 2: dispense liquor, 3: done making drink>
//         --> if 0: 
//             --> "drinkName": <name of drink on queue>
//             --> "userName" : <name of user who ordered drink>
//         --> if 1:
//             --> "led": {
//                     "r": <value between 0 and 4095>,
//                     "g": <value between 0 and 4095>,
//                     "b": <value between 0 and 4095>
//             }
//         --> if 2:
//             --> "liquor": {
//                     "amt": <amount to pump in fl oz>,
//                     "bot": <bottle number to pump amt from>
//             }
//         --> if 3: <no further info needed>
//         --> if 4:
//                  "ms": <time to pump for in milliseconds>,
//                  "bot": <bottle number to pump amt from>
// } 

// Arduino -> Rasp Pi response message structure {
//     "response": <0: data error, 1: drink cancelled, 2: start making drink, 3: done pumping ingredient>
// }

// Find and open serial ports to Arduinos
var promises = [];
SerialPort.list(function(err, ports) {
    for (var i = 0; i < ports.length; ++i) {
        var port = ports[i];
        if (port.serialNumber != undefined) {
            serialNum = port.serialNumber;
            serialNum = serialNum.replace("Arduino__www.arduino.cc__0043_", "");

            if (serialNum == pumpArduinoSerialNum) {
                var comName = port.comName;
                comName = comName.replace("cu", "tty");

                // Open serial port to LED Arduino
                serialPortPump = new SerialPort.SerialPort(comName, {
                    baudrate: serialBaud,
                    parser: SerialPort.parsers.readline("\n")
                });

                serialPortPump.on("open", function() {
                    console.log(colors.blue.bgWhite("Serial port to Pump Arduino open"));
                    
                    // Condense ingredient data for Arduino
                    var condensedIngredientPacket = {
                        "msgType": 4,
                        "ms": 2000,
                        "bot": 12
                    };
                    var ingredientString = JSON.stringify(condensedIngredientPacket);

                    // Write to Pump Arduino
                    if (verbose) { console.log(colors.white("    Ingredient string: " + ingredientString)); }
                    serialPortPump.write(ingredientString, function(ingredientWriteErr, ingredientWriteResults) {
                        if (ingredientWriteErr != null) { console.log(colors.red("    Write errors: " + ingredientWriteErr)); }
                    });
                });
            }   
        }
    }
});

