var Queue = require('firebase-queue');
    Firebase = require('firebase');

var queueRef = new Firebase('https://burning-torch-4312.firebaseio.com/queue');
var queue = new Queue(queueRef, function(data, progress, resolve, reject) {
    console.log(data);

    resolve();

});
