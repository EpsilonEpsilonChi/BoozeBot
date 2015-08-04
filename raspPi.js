var SerialPort = require("serialport").SerialPort;
var serialPort = new SerialPort("/dev/tty.usbmodem1421");
var colors     = require('colors/safe');
var Queue      = require('firebase-queue');
    Firebase   = require('firebase');

// Configuration variables
var verbose = true;

serialPort.on("open", function() {
    console.log("Serial port open");

    // Listen for data from Arduino
    serialPort.on('data', function(data) {
        console.log('data received: ' + data);
    });

    // Waiting for new drinks sitting in queue and pours them
    var queueRef = new Firebase('https://boozebot.firebaseio.com/drinkQueue');
    var queue = new Queue(queueRef, function(data, progress, resolve, reject) {
        console.log(colors.green("BoozeBot making drink: ") + colors.green.underline(data["recipeUsed"]));

        // Compress recipe into smaller JSON object
        var packet = {};
        for (var i = 1; i <= parseInt(data["ingredientCount"], 10); i++) {
            var ingredient = data["ingredient" + i]
            packet[i] = {
                "a": ingredient["amountUsed"],
                "b": ingredient["liquorBottleNum"]
            }
        }

        // Send compressed recipe to Arduino
        if (verbose) { console.log(colors.yellow("  Sending recipe data to Arduino...")); }
        var dataToWrite = JSON.stringify(packet);
        console.log("dataToWrite: " + dataToWrite);
        serialPort.write(dataToWrite, function(err, results) {
            if (verbose) { console.log(colors.cyan("  Errors: " + err)); }
            if (verbose) { console.log(colors.cyan("  Results: " + results)); }
        });

        resolve();
    });
});
