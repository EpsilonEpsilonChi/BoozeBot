var SerialPort = require("serialport");
var colors     = require("colors/safe");
var serialBaud = 115200;
var readline = require('readline');

var rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
});

// Callback for initial serial port opening
var serialPortOpenCallback = function() {
    setTimeout(function(){  // Wait for Arduino to boot
        console.log(colors.blue.bgWhite("Serial port open"));

        var ledObj = {
            "num": 2,
            "r": 4095,
            "g": 0,
            "b": 4095
        };

        var packet = {};
        packet["cmdType"] = 0;
        packet["led"] = ledObj;

        console.log(colors.yellow("Sending data to Arduino..."));
        var dataToWrite = JSON.stringify(packet);
        console.log(colors.white("  Recipe data packet: " + dataToWrite));
        serialPort.write(dataToWrite, function(err, results) {
            if (err != null) { console.log(colors.red("  Write errors: " + err)); }
            console.log("Written");
        }); 
    }, 4000);
};

// Find and open serial port to Arduino
var serialPort;
var serialComName = "";
SerialPort.list(function(err, ports) {
    ports.forEach(function(port) {
        if (port.manufacturer == "Arduino (www.arduino.cc)") {
            serialComName = port.comName;
            serialComName = serialComName.replace("cu", "tty");
            serialPort = new SerialPort.SerialPort(serialComName, {
                baudrate: serialBaud,
                parser: SerialPort.parsers.readline("\n")
            });

           serialPort.on("open", serialPortOpenCallback);
        }
    });
});


