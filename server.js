var Queue = require('firebase-queue');
    Firebase = require('firebase');

var queueRef = new Firebase('https://boozebot.firebaseio.com/queue');
var queue = new Queue(queueRef, function(data, progress, resolve, reject) {
    console.log(data);

    resolve();
});
