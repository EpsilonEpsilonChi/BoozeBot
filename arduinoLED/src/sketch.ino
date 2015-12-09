#include <Tlc5940.h>
#include <aJSON.h>

// Globals
aJsonStream serial_stream(&Serial);

void testSequence() {
  for (int i = 0; i < 3; ++i) {
    for (int j = 0; j < 36; j = j + 3) {
      Tlc.set(j + i, 4095);
    }
    for (int j = 36; j > 0; j = j - 3) {
      Tlc.set(j + i, 0);
    }
  }
}

void twoLights() {
  for (int i = 0; i < 3; i++) {
    Tlc.clear();
    Tlc.set(i, 4095);
    Tlc.update();
    delay(25);

    Tlc.clear();
    Tlc.set(i + 3, 4095);
    Tlc.update();
    delay(25);
  }
}

void setLED(int ledNum, int red, int green, int blue) {
  int firstPinNum = (ledNum - 1) * 3;
  Tlc.set(firstPinNum, red);
  Tlc.set(firstPinNum + 1, green);
  Tlc.set(firstPinNum + 2, blue);
}

void runProgram(int program) {

}

// LED JSON Object format
// { "cmdType": <0 for ind. LED assign, 1 for LED program>, 
//   "prog": <program name>,
//   "led": {
//     "num": <led number>,
//     "r": <red value 0 to 4095>,
//     "g": <green value 0 to 4095>,
//     "b": <blue value 0 to 4095>
//   }
// } 

// Processes command JSON data passed in
int processCommand(aJsonObject *command) {
  // Check to make sure command exists
  if (!command) {
    return 1;
  }

  // Check command type (single LED assignment or program)
  aJsonObject *cmdType = aJson.getObjectItem(command, "cmdType");
  if (cmdType && (cmdType->valueint == 0)) {  // ASSUMES VALUE IS INT, MIGHT NEED CHECKING
    aJsonObject *led = aJson.getObjectItem(command, "led");

    if (led) {
      aJsonObject *num = aJson.getObjectItem(led, "num");
      aJsonObject *r = aJson.getObjectItem(led, "r");
      aJsonObject *g = aJson.getObjectItem(led, "g");
      aJsonObject *b = aJson.getObjectItem(led, "b"); // error checking?
      setLED(num->valueint, r->valueint, g->valueint, b->valueint);
    }
  } else if (cmdType && (cmdType->valueint == 1)) {
    // Run program
    aJsonObject *prog = aJson.getObjectItem(command, "prog"); // error checking?
    runProgram(prog->valueint);
  }
  
  return 0;
}

// Creates response message to send back to Raspberry Pi
// Format: { "response": <1 for success, 0 for failure>, "error": <error message, if any> } 
aJsonObject *createResponseMessage(int responseNum) {
  // int response = 0;
  // char *message;
  // switch(responseNum) {
  //   case 0:     // Success
  //     response = 1;
  //     break;
  //   case 1: 
  //     message = "No command data\n";
  //     break;
  //   case 2:
  //     message = "Invalid amount type\n";
  //     break;
  //   case 3:
  //     message = "Invalid bottleNum type\n";
  //     break;
  // }

  // // Create JSON object for response packet
  // aJsonObject *packet = aJson.createObject();
  // aJsonObject *responseItem = aJson.createItem(response);
  // aJsonObject *uniqueIDItem = aJson.createItem(uniqueID);
  // aJson.addItemToObject(packet, "response", responseItem);
  // aJson.addItemToObject(packet, "uniqueID", uniqueIDItem);

  // if (response == 1) {
  //   aJsonObject *nullItem = aJson.createNull();
  //   aJson.addItemToObject(packet, "error", nullItem);
  // } else {
  //   aJsonObject *errorItem = aJson.createItem(message);
  //   aJson.addItemToObject(packet, "error", errorItem);
  // }

  // if (uniqueID == 99) {
  //   uniqueID = 0;
  // } else {
  //   uniqueID++;
  // }

  // return packet;
}

void setup() {
  Tlc.init();
  Serial.begin(115200);

  serial_stream.flush();
}

void loop() {
  if (serial_stream.available()) {
    serial_stream.skip();
  }

  if (serial_stream.available()) {
    // Parse input
    aJsonObject *command = aJson.parse(&serial_stream);
    int returnVal = processCommand(command);
    aJson.deleteItem(command);
    serial_stream.flush();

    // Send response
    aJsonObject *response = createResponseMessage(returnVal);
    aJson.print(response, &serial_stream);
    Serial.println();
    aJson.deleteItem(response);
  }
}
