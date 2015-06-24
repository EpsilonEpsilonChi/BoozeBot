var Firebase = require('firebase')

var ref = new Firebase('https://burning-torch-4312.firebaseio.com/queue/tasks');
for(var i = 0; i < 10; ++i){
    ref.push({"uuid": i, "order": "drink" + i.toString()});
}
