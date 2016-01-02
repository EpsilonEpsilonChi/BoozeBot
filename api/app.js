var express = require('express');
var app = express();

// respond with "hello world" when a GET request is made to the homepage
app.get('/', function(req, res) {
    res.send('hello world');
});

var server = app.listen(3000, function () {
//  var host = server.address().address;
  var host = '127.0.0.1'
  var port = server.address().port;

  console.log('Example app listening at http://%s:%s', host, port);
});
