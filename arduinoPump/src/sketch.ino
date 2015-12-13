// ****** EDIT TO DISPENSE ALL INGREDIENTS AT THE SAME TIME

#include <aJSON.h>

// Pin definitions
#define BOTTLERELAY1 2
#define BOTTLERELAY2 3
#define BOTTLERELAY3 4
#define BOTTLERELAY4 5
#define BOTTLERELAY5 6
#define BOTTLERELAY6 7
#define BOTTLERELAY7 8
#define BOTTLERELAY8 9
#define BOTTLERELAY9 10
#define BOTTLERELAY10 11
#define BOTTLERELAY11 12
#define BOTTLERELAY12 13
#define BOTTLERELAY13 14
#define BOTTLERELAY14 15
#define BOTTLERELAY15 16
#define BOTTLERELAY16 17

// Allows default relay state to be changed
#define PUMPON 0
#define PUMPOFF 1

// Config variables
int serialBaud           = 115200;  // Bit rate of serial connection
int maxIngredients       = 10;      // Max num of ingredients passed from RPi in JSON object
double timeToPourOneFlOz = 350;     // Time it takes to pour 1 fl oz (in milliseconds)
int messageToPumpDelay   = 150;     // Time between sending LED set message and pumping

// Globals
int curColor   = 0;
aJsonStream serial_stream(&Serial);

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

// Selects proper pin constant based on bottle number
int selectPin(int bottleNum) {
  switch(bottleNum) {
    case 1:
      return BOTTLERELAY1;
    case 2:
      return BOTTLERELAY2;
    case 3:
      return BOTTLERELAY3;
    case 4:
      return BOTTLERELAY4;
    case 5:
      return BOTTLERELAY5;
    case 6:
      return BOTTLERELAY6;
    case 7:
      return BOTTLERELAY7;
    case 8:
      return BOTTLERELAY8;
    case 9:
      return BOTTLERELAY9;
    case 10:
      return BOTTLERELAY10;
    case 11:
      return BOTTLERELAY11;
    case 12:
      return BOTTLERELAY12;
    case 13:
      return BOTTLERELAY13;
    case 14:
      return BOTTLERELAY14;
    case 15:
      return BOTTLERELAY15;
    case 16:
      return BOTTLERELAY16;
  }
}

// Logic for determine what color the next bottle will light up when being pumped
void lightUpPumpingBottle(int bottleNum) {
  switch(curColor) {
    case 0:
      setLed(bottleNum, 4095, 0, 0);
    case 1: 
      setLed(bottleNum, 0, 4095, 0);
    case 2:
      setLed(bottleNum, 0, 0, 4095);
    case 3:
      setLed(bottleNum, 4095, 4095, 0);
    case 4:
      setLed(bottleNum, 0, 4095, 4095);
    case 5:
      setLed(bottleNum, 4095, 0, 4095);
    case 6:
      setLed(bottleNum, 4095, 4095, 4095);
      curColor = 0;
      break;
  }
}

// Turns off an individual LED
void turnOffLed(int ledNum) {
  setLed(ledNum, 0, 0, 0);
}

// Sends message to set individual LED on LED Arduino
void setLed(int ledNum, int red, int green, int blue) {
  // Create packet with LED assign type
  aJsonObject *packet = aJson.createObject();
  aJsonObject *msgType = aJson.createItem(0);
  aJson.addItemToObject(packet, "msgType", msgType);

  // Create LED object with inputted RGB values
  aJsonObject *led = aJson.createObject();

  aJsonObject *num = aJson.createItem(ledNum);
  aJsonObject *r = aJson.createItem(red);
  aJsonObject *g = aJson.createItem(green);
  aJsonObject *b = aJson.createItem(blue);

  aJson.addItemToObject(led, "num", num);
  aJson.addItemToObject(led, "r", r);
  aJson.addItemToObject(led, "g", g);  
  aJson.addItemToObject(led, "b", b);
  aJson.addItemToObject(packet, "led", led);

  // Send to Rasp Pi
  aJson.print(packet, &serial_stream);
  Serial.println();
}

// Sends message to start an LED program to LED Arduino
void setProgram(int progNum) {
  // Create packet with program set type and inputted program number
  aJsonObject *packet = aJson.createObject();
  aJsonObject *msgType = aJson.createItem(1);
  aJson.addItemToObject(packet, "msgType", msgType);
  aJsonObject *prog = aJson.createItem(1);
  aJson.addItemToObject(packet, "prog", prog);

  // Send to Rasp Pi
  aJson.print(packet, &serial_stream);
  Serial.println();
}

// Dispenses a certain amount of liquor
void dispenseLiquor(double amount, int bottleNum) {
  int liquorPin = selectPin(bottleNum);

  lightUpPumpingBottle(bottleNum);
  delay(messageToPumpDelay);

  digitalWrite(liquorPin, PUMPON);
  delay(timeToPourOneFlOz * amount);
  digitalWrite(liquorPin, PUMPOFF);

  turnOffLed(bottleNum);
}

// Processes recipe transaction JSON data passed in
int processRecipe(aJsonObject *recipe) {
  // Check to make sure recipe exists
  if (!recipe) {
    return 1;
  }

  // Read ingredients from JSON object
  for (int i = 1; i <= maxIngredients; i++) {
    char charNum[10];
    String strNum = String(i);
    strNum.toCharArray(charNum, 10);
    aJsonObject *ingredient = aJson.getObjectItem(recipe, charNum);

    if (ingredient) {      
      aJsonObject *amountIn = aJson.getObjectItem(ingredient, "a");
      aJsonObject *bottleNumIn = aJson.getObjectItem(ingredient, "b"); 

      // Check for existance
      if (amountIn && bottleNumIn) {
        // Get amount type and set value accordingly
        double amount = 0;
        if (amountIn->type == 2) {
          amount = amountIn->valueint;
        } else if (amountIn->type == 3) {
          amount = amountIn->valuefloat;
        } else if (amountIn->type == 4) {
          amount = atof(amountIn->valuestring);
        } else {
          return 2;
        }

        // Get bottleNum type and set value accordingly
        int bottleNum = 0;
        if (bottleNumIn->type == 2) {
          bottleNum = bottleNumIn->valueint;
        } else if (bottleNumIn->type == 3) {
          bottleNum = bottleNumIn->valuefloat;
        } else if (bottleNumIn->type == 4) {
          bottleNum = atoi(bottleNumIn->valuestring);
        } else {
          return 3;
        }

        dispenseLiquor(amount, bottleNum);
      }
    }
  }
  
  return 0;
}

// Creates response message to send back to Raspberry Pi
// Format: { "response": <1 for success, 0 for failure>, "error": <error message, if any> } 
aJsonObject *createResponseMessage(int responseNum) {
  int response = 0;
  char *message;
  switch(responseNum) {
    case 0:     // Success
      response = 1;
      break;
    case 1: 
      message = "No recipe data\n";
      break;
    case 2:
      message = "Invalid amount type\n";
      break;
    case 3:
      message = "Invalid bottleNum type\n";
      break;
  }

  // Create JSON object for response packet
  aJsonObject *packet = aJson.createObject();
  aJsonObject *responseItem = aJson.createItem(response);
  aJson.addItemToObject(packet, "response", responseItem);

  if (response == 1) {
    aJsonObject *nullItem = aJson.createNull();
    aJson.addItemToObject(packet, "error", nullItem);
  } else {
    aJsonObject *errorItem = aJson.createItem(message);
    aJson.addItemToObject(packet, "error", errorItem);
  }

  return packet;
}
 
void setup() {
  Serial.begin(serialBaud);

  // Set bottle relay mode and state
  pinMode(BOTTLERELAY1, OUTPUT);
  pinMode(BOTTLERELAY2, OUTPUT);
  pinMode(BOTTLERELAY3, OUTPUT);
  pinMode(BOTTLERELAY4, OUTPUT);
  pinMode(BOTTLERELAY5, OUTPUT);
  pinMode(BOTTLERELAY6, OUTPUT);
  pinMode(BOTTLERELAY7, OUTPUT);
  pinMode(BOTTLERELAY8, OUTPUT);
  pinMode(BOTTLERELAY9, OUTPUT);
  pinMode(BOTTLERELAY10, OUTPUT);
  pinMode(BOTTLERELAY11, OUTPUT);
  pinMode(BOTTLERELAY12, OUTPUT);
  pinMode(BOTTLERELAY13, OUTPUT);
  pinMode(BOTTLERELAY14, OUTPUT);
  pinMode(BOTTLERELAY15, OUTPUT);
  pinMode(BOTTLERELAY16, OUTPUT);
  digitalWrite(BOTTLERELAY1, PUMPOFF);
  digitalWrite(BOTTLERELAY2, PUMPOFF);
  digitalWrite(BOTTLERELAY3, PUMPOFF);
  digitalWrite(BOTTLERELAY4, PUMPOFF);
  digitalWrite(BOTTLERELAY5, PUMPOFF);
  digitalWrite(BOTTLERELAY6, PUMPOFF);
  digitalWrite(BOTTLERELAY7, PUMPOFF);
  digitalWrite(BOTTLERELAY8, PUMPOFF);
  digitalWrite(BOTTLERELAY9, PUMPOFF);
  digitalWrite(BOTTLERELAY10, PUMPOFF);
  digitalWrite(BOTTLERELAY11, PUMPOFF);
  digitalWrite(BOTTLERELAY12, PUMPOFF);
  digitalWrite(BOTTLERELAY13, PUMPOFF);
  digitalWrite(BOTTLERELAY14, PUMPOFF);
  digitalWrite(BOTTLERELAY15, PUMPOFF);
  digitalWrite(BOTTLERELAY16, PUMPOFF);

  serial_stream.flush();
}

void loop() {
  if (serial_stream.available()) {
    serial_stream.skip();
  }

  if (serial_stream.available()) {
    // Parse input
    aJsonObject *recipe = aJson.parse(&serial_stream);
    int returnVal = processRecipe(recipe);
    aJson.deleteItem(recipe);
    serial_stream.flush();

    // Send response
    aJsonObject *response = createResponseMessage(returnVal);
    aJson.print(response, &serial_stream);
    Serial.println();
    aJson.deleteItem(response);
  }
}
