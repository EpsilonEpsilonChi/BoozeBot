var Firebase = require('firebase')

var queueRef = new Firebase('https://boozebot.firebaseio.com/queue/tasks');

var ref = new Firebase('https://boozebot.firebaseio.com');
var usersRef = ref.child("Users")

usersRef.on("child_added", function(snapshot) {
	for(var i = 0; i < 10; ++i){
	    queueRef.push({"uuid": i, "order": "drink" + i.toString()});
	}
});