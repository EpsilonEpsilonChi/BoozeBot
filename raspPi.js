var colors   = require('colors/safe');
var five	 = require('johnny-five');
var Queue    = require('firebase-queue');
    Firebase = require('firebase');

var board = new five.Board();

// Configuration variables
var verbose 		  = true;
var timeToPourOneFlOz = 2.2;

// Will instruct Arduino to pour the liquor
function pourLiquor(bottleNum, amount) {
    if (verbose) { console.log("  Pouring " + colors.yellow(amount) + "oz of liquor in bottle #" + colors.yellow(bottleNum)); }
}

// Board initialization
board.on("ready", function() {
    // Create relay instances and inject into REPL
    var bottle1 = new five.Relay(2);
    var bottle2 = new five.Relay(3);
    var bottle3 = new five.Relay(4);
    var bottle4 = new five.Relay(5);
    var bottle5 = new five.Relay(6);
    var bottle6 = new five.Relay(7);
    var bottle7 = new five.Relay(8);
    var bottle8 = new five.Relay(9);
    var bottle9 = new five.Relay(10);
    var bottle10 = new five.Relay(11);
    var bottle11 = new five.Relay(12);
    var bottle12 = new five.Relay(13);
    var bottle13 = new five.Relay(14);
    var bottle14 = new five.Relay(15);
    var bottle15 = new five.Relay(16);

    board.repl.inject({
    	relay1: relay, 
    	relay2: relay,
    	relay3: relay,
    	relay4: relay,
    	relay5: relay,
    	relay6: relay,
    	relay7: relay,
    	relay8: relay,
    	relay9: relay,
    	relay10: relay,
    	relay11: relay,
    	relay12: relay,
    	relay13: relay,
    	relay14: relay,
    	relay15: relay,
    });

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


