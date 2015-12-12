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

void testBlink() {
  Tlc.set(2, 4095);
  Tlc.update();
  delay(300);
  Tlc.set(2, 0);
  Tlc.update();
}

void setLED(int ledNum, int red, int green, int blue) {
  int firstPinNum = (ledNum - 1) * 3;
  Tlc.set(firstPinNum, red);
  Tlc.set(firstPinNum + 1, green);
  Tlc.set(firstPinNum + 2, blue);
  Tlc.update();
}

void runProgram(int program) {

}

// Arduino -> Rasp Pi message structure 
// { "msgType": <0 for ind. LED assign, 1 for LED program, 2 for drink completion>, 
//   "prog": <program name>,
//   "led": {
//     "num": <led number>,
//     "r": <red value 0 to 4095>,
//     "g": <green value 0 to 4095>,
//     "b": <blue value 0 to 4095>
//   },
//   "response": <1 for success, 0 for failure>,
//   "error": <error message, if any>
// } 

// Processes command JSON data passed in
int processCommand(aJsonObject *command) {
  // Check to make sure command exists
  if (!command) {
    return 1;
  }

  // Check command type (single LED assignment or program)
  aJsonObject *msgType = aJson.getObjectItem(command, "msgType");
  if (msgType && (msgType->valueint == 0)) {  // ASSUMES VALUE IS INT, MIGHT NEED CHECKING
    // Set LED with given values
    aJsonObject *led = aJson.getObjectItem(command, "led");

    if (led) {
      aJsonObject *num = aJson.getObjectItem(led, "num");
      aJsonObject *r = aJson.getObjectItem(led, "r");
      aJsonObject *g = aJson.getObjectItem(led, "g");
      aJsonObject *b = aJson.getObjectItem(led, "b"); // error checking?
      setLED(num->valueint, r->valueint, g->valueint, b->valueint);
    }
  } else if (msgType && (msgType->valueint == 1)) {
    // Run program
    aJsonObject *prog = aJson.getObjectItem(command, "prog"); // error checking?
    runProgram(prog->valueint);
  }
  
  return 0;
}

void setup() {
  Tlc.init();
  delay(200);
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
  }
}
