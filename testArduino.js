var SerialPort = require("serialport").SerialPort;
var serialPort = new SerialPort("/dev/tty.usbmodem1421");
var colors     = require('colors/safe');
var Queue      = require('firebase-queue');
    Firebase   = require('firebase');

// Configuration variables
var verbose 		  = true;
var timeToPourOneFlOz = 2200;

var ledstate = false;

// Will instruct Arduino to pour the liquor
function pourLiquor(bottleNum, amount) {
    if (verbose) { console.log("  Pouring " + colors.yellow(amount) + "oz of liquor in bottle #" + colors.yellow(bottleNum)); }
}

serialPort.on("open", function() {
	console.log("Serial port open");

	// Waiting for new drinks sitting in queue and pours them
	var queueRef = new Firebase('https://boozebot.firebaseio.com/drinkQueue');
	var queue = new Queue(queueRef, function(data, progress, resolve, reject) {
	    if (verbose) { console.log(colors.green("BoozeBot making drink: ") + colors.green.underline(data["recipeUsed"])); }

	    serialPort.write("hello", function(err, results) {
	    	console.log("Error: " + err);
	    	console.log("Results: " + results);
	    });
	    
	    for (var attributeName in data) {
	        if (attributeName.substring(0, 10) == "ingredient") {
	            pourLiquor(data[attributeName]["liquorBottleNum"], data[attributeName]["amountUsed"]);
	        }
	    }
	    resolve();
	});
});
