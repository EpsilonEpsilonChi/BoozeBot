var five 	 = require("johnny-five");
var board 	 = new five.Board();
var colors   = require('colors/safe');
var Queue    = require('firebase-queue');
    Firebase = require('firebase');

// Configuration variables
var verbose 		  = true;
var timeToPourOneFlOz = 2200;

var ledstate = false;

// Will instruct Arduino to pour the liquor
function pourLiquor(bottleNum, amount) {
    if (verbose) { console.log("  Pouring " + colors.yellow(amount) + "oz of liquor in bottle #" + colors.yellow(bottleNum)); }
}

// Wait for Arduino board to be ready
board.on("ready", function() {
	var led = new five.Led(13);
	for (i = 1; i < 20; i++) {
		setTimeout(function() {
			led.on();
		}, 1000);
		setTimeout(function() {
			led.off();
		}, 1000);
	}
	
	// Waiting for new drinks sitting in queue and pours them
	var queueRef = new Firebase('https://boozebot.firebaseio.com/drinkQueue');
	var queue = new Queue(queueRef, function(data, progress, resolve, reject) {
	    if (verbose) { console.log(colors.green("BoozeBot making drink: ") + colors.green.underline(data["recipeUsed"])); }
	    
	    for (var attributeName in data) {
	        if (attributeName.substring(0, 10) == "ingredient") {
	            pourLiquor(data[attributeName]["liquorBottleNum"], data[attributeName]["amountUsed"]);
	        }
	    }
	    resolve();
	});
});