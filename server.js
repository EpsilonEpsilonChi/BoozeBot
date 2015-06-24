var Queue = require('firebase-queue');
    Firebase = require('firebase');

var five = require("johnny-five");
var board = new five.Board();

var queueRef = new Firebase('https://burning-torch-4312.firebaseio.com/queue');
var queue = new Queue(queueRef, function(data, progress, resolve, reject) {
    console.log(data);

    board.on("ready", function() {
        var relay = new five.Relay(10);

        // Control the relay in real time
        // from the REPL by typing commands, eg.
        //
        // relay.on();
        //
        // relay.off();
        //
        this.repl.inject({
            relay: relay
        });
    });

    resolve();

});
