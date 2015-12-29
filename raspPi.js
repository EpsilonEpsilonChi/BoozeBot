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
var queueRef                = new Firebase('https://boozebot.firebaseio.com/drinkQueue');

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
// } 

// Arduino -> Rasp Pi response message structure {
//     "response": <0: data error, 1: drink cancelled, 2: start making drink, 3: done pumping ingredient>
// }

// Chooses color to light bottle for when next liquor pours
var pickLightingColor = function(bottleNum) {
    var colorRGB;
    switch(colorNum) {
        case 0:
            colorRGB = {"r": 4095, "g": 0, "b": 0};
            break;
        case 1:
            colorRGB = {"r": 0, "g": 4095, "b": 0};
            break;
        case 2:
            colorRGB = {"r": 0, "g": 0, "b": 4095};
            break;
        case 3:
            colorRGB = {"r": 4095, "g": 4095, "b": 0};
            break;
        case 4:
            colorRGB = {"r": 0, "g": 4095, "b": 4095};
            break;
        case 5:
            colorRGB = {"r": 4095, "g": 0, "b": 4095};
            break;
        case 6:
            colorRGB = {"r": 4095, "g": 4095, "b": 4095};
            break;
    }

    // Increment state
    if (colorNum == 6) {
        colorNum = 0;
    } else {
        colorNum++;
    }

    colorRGB["num"] = parseInt(bottleNum);
    return colorRGB;
}

// Light up bottle
var lightUpBottle = function(callback, ingredient) { 
    console.log(colors.cyan("  Lighting up bottle ") + colors.cyan.underline(ingredient["liquorBottleNum"]));

    // Create data packet to send to LED Arduino
    var lightingPacket = {
        "msgType": 1,
        "led": pickLightingColor(ingredient["liquorBottleNum"])
    };
    var lightingPacketString = JSON.stringify(lightingPacket);

    // Write to LED Arduino
    if (verbose) { console.log(colors.white("    Lighting packet string: " + lightingPacketString)); }
    serialPortLed.write(lightingPacketString, function(lightingWriteErr, lightingWriteResults) {
        if (lightingWriteErr != null) { console.log(colors.red("    Write errors: " + lightingWriteErr)); }
        callback(lightingWriteErr, "one");
    });
}

// Dispense ingredient 
var dispenseIngredient = function(callback, ingredient) {
    console.log(colors.green("  Dispensing ") + colors.green.underline(ingredient["amountUsed"]) + colors.green(" fl oz of bottle ") + colors.green.underline(ingredient["liquorBottleNum"]));

    // Condense ingredient data for Arduino
    var condensedIngredientPacket = {
        "msgType": 2,
        "liquor": {
            "amt": ingredient["amountUsed"],
            "bot": ingredient["liquorBottleNum"]
        }
    };
    var ingredientString = JSON.stringify(condensedIngredientPacket);

    // Write to Pump Arduino
    if (verbose) { console.log(colors.white("    Ingredient string: " + ingredientString)); }
    serialPortPump.write(ingredientString, function(ingredientWriteErr, ingredientWriteResults) {
        if (ingredientWriteErr != null) { console.log(colors.red("    Write errors: " + ingredientWriteErr)); }

        // Wait for response from Pump Arduino
        if (verbose) { console.log(colors.magenta("    Waiting for response...")); }
        serialPortPump.on('data', function(ingredientResponseData) {
            if (verbose) { console.log(colors.white("    Response packet string: " + ingredientResponseData)); }

            // Close and reopen serial port around parsing
            serialPortPump.close(function(ingredientCloseErr) {
                if (ingredientCloseErr != null) { console.log(colors.red("    Port close error: " + ingredientCloseErr)); }

                var ingredientResponseObj = JSON.parse(ingredientResponseData);
                serialPortPump.open(function(ingredientOpenErr) {
                    if (ingredientOpenErr != null) { console.log(colors.red("    Port reopen error: " + ingredientOpenErr)); }

                    if (ingredientResponseObj["response"] == 3) {
                        if (verbose) { console.log(colors.green("    Done pumping ingredient")); }
                        callback(ingredientWriteErr, "two");
                    }
                });
            });
        });
    });
}

// Turn off corresponding bottle LED
var turnOffLed = function(callback, ingredient) {
    if (verbose) { console.log(colors.cyan("  Turning off LED on bottle ") + colors.cyan.underline(ingredient["liquorBottleNum"])); }

    // Create data packet to send to LED Arduino
    var lightingOff = {
        "msgType": 1,
        "led": {"num": parseInt(ingredient["liquorBottleNum"]), "r": 0, "g": 0, "b": 0}
    };
    var lightingOffString = JSON.stringify(lightingOff);

    // Write to LED Arduino
    if (verbose) { console.log(colors.white("    Lighting off string: " + lightingOffString)); }
    serialPortLed.write(lightingOffString, function(lightingOffErr, lightingOffResults) {
        if (lightingOffErr != null) { console.log(colors.red("    Write errors: " + lightingOffErr)); }

        // Pause to let serial port to LED Arduino clear out
        setTimeout(function() {
            callback(lightingOffErr, "three");
        }, timeoutAfterLedOff);
    });
}

// Listens to Firebase queue for new data
var firebaseListener = function(data, progress, resolve, reject) {
    // New drink on queue, send to LCD attached to LED Arduino
    console.log(colors.white("New drink on queue: ") + colors.white.underline(data["recipeUsed"]));
    var drinkPacket = {
        "msgType": 0,
        "drinkName": data["recipeUsed"]
    };
    var drinkPacketString = JSON.stringify(drinkPacket);

    console.log(colors.yellow("Sending drink on queue to LED Arduino..."));
    if (verbose) { console.log(colors.white("  Drink packet string: " + drinkPacketString)); }
    serialPortLed.write(drinkPacketString, function(err, results) {
        if (err != null) { console.log(colors.red("  Write errors: " + err)); }

        // Wait for response from LED Arduino
        if (verbose) { console.log(colors.magenta("  Waiting for response...")); }
        serialPortLed.on('data', function(drinkResponse) {
            if (verbose) { console.log(colors.white("  Response packet string: " + drinkResponse)); }

            // Close and reopen serial port around parsing
            serialPortLed.close(function(closeErr) {
                if (closeErr != null) { console.log(colors.red("  Port close error: " + closeErr)); }

                var responseObj = JSON.parse(drinkResponse);
                serialPortLed.open(function(openErr) {
                    if (openErr != null) { console.log(colors.red("  Port reopen error: " + openErr)); }

                    if (responseObj["response"] == 0) {         // Data error
                        console.log(colors.red("LED Arduino says there was an error with its received data"));
                        reject("Data error");
                    } else if (responseObj["response"] == 1) {  // Drink cancelled
                        console.log(colors.blue("Drink cancelled by user"));
                        reject("Drink cancelled by user");

                        // **** TIMEOUT AND CLEAR SCREEN AND LED



                    } else if (responseObj["response"] == 2) {  // Continue making drink on queue
                        console.log(colors.green("BoozeBot making drink: ") + colors.green.underline(data["recipeUsed"]));

                        // Compress/reformat ingredient list
                        var ingredientList = {};
                        for (var i = 1; i <= parseInt(data["ingredientCount"], 10); i++) {
                            ingredientList[i] = data["ingredient" + i];
                        }

                        // Pump each liquor in recipe and light corresponding bottle LED
                        async.eachSeries(ingredientList, function(ingredient, loopCallback) {
                            async.series([
                                function(innerCallback) {   // Light up corresponding bottle LED
                                    lightUpBottle(innerCallback, ingredient);
                                },
                                function(innerCallback) {   // Dispense ingredient
                                    dispenseIngredient(innerCallback, ingredient);
                                },
                                function(innerCallback) {   // Turn off corresponding bottle LED
                                    turnOffLed(innerCallback, ingredient);
                                }
                            ], loopCallback);
                        }, function(err) {
                            console.log(colors.green("BoozeBot finished making drink: ") + colors.green.underline(data["recipeUsed"]));

                            // Create completion data packet to send to LED Arduino
                            var completionPacket = {
                                "msgType": 3
                            };
                            var completionString = JSON.stringify(completionPacket);

                            // Write to LED Arduino
                            if (verbose) { console.log(colors.white("  Completion string: " + completionString)); }
                            serialPortLed.write(completionString, function(completionErr, completionResult) {
                                if (completionErr != null) { console.log(colors.red("  Write errors: " + completionErr)); }

                                resolve();
                            });
                        });
                    }
                });
            });
        });
    });
}

// Opens serial port to LED Arduino
var ledPortOpen = function() {
    return new Promise(function(resolve, reject) {
        serialPortLed.on("open", function() {
            console.log(colors.blue.bgWhite("Serial port to LED Arduino open"));
            resolve();
        });
    });
}

// Opens serial port to Pump Arduino
var pumpPortOpen = function() {
    return new Promise(function(resolve, reject) {
        serialPortPump.on("open", function() {
            console.log(colors.blue.bgWhite("Serial port to Pump Arduino open"));
            resolve();
        });
    });
}

// Find and open serial ports to Arduinos
var promises = [];
SerialPort.list(function(err, ports) {
    for (var i = 0; i < ports.length; ++i) {
        var port = ports[i];
        if (port.serialNumber != undefined) {
            serialNum = port.serialNumber;
            serialNum = serialNum.replace("Arduino__www.arduino.cc__0043_", "");

            if (serialNum == ledArduinoSerialNum) {
                var comName = port.comName;
                comName = comName.replace("cu", "tty");

                // Open serial port to LED Arduino
                serialPortLed = new SerialPort.SerialPort(comName, {
                    baudrate: serialBaud,
                    parser: SerialPort.parsers.readline("\n")
                });
                promises.push(ledPortOpen());
            } else if (serialNum == pumpArduinoSerialNum) {
                var comName = port.comName;
                comName = comName.replace("cu", "tty");

                // Open serial port to LED Arduino
                serialPortPump = new SerialPort.SerialPort(comName, {
                    baudrate: serialBaud,
                    parser: SerialPort.parsers.readline("\n")
                });
                promises.push(pumpPortOpen());
            }   
        }
    }

    // Requires that both Arduinos be connected, exits otherwise
    if (serialPortLed == null) {
        console.log(colors.red("LED Arduino not connected"));
        process.exit(1);
    } else if (serialPortPump == null) {
        console.log(colors.red("Pump Arduino not connected"));
        process.exit(1);
    }
});

// Waits for both serial ports to open, then starts up the Firebase queue listener
Promise.all(promises).then(function() {
    var queue = new Queue(queueRef, firebaseListener);
});