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
#define BOTTLERELAY12 14
#define BOTTLERELAY13 15
#define BOTTLERELAY14 16
#define BOTTLERELAY15 17
#define BOTTLERELAY16 18

// Allows default relay state to be changed
#define PUMPON 0
#define PUMPOFF 1

// Config variables
double timeToFillBottleTube = 500;     // Time it takes to fill tube going into bottle from empty
double timeToPourOneFlOz    = 300;     // Time it takes to pour 1 fl oz (in milliseconds)
int messageToPumpDelay      = 400;     // Time between sending LED set message and pumping

// Globals
aJsonStream serial_stream(&Serial);

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

// Dispenses a certain amount of liquor
void dispenseLiquor(double amount, int bottleNum) {
  int liquorPin = selectPin(bottleNum);

  delay(messageToPumpDelay);

  digitalWrite(liquorPin, PUMPON);
  delay(timeToFillBottleTube + (timeToPourOneFlOz * amount));
  digitalWrite(liquorPin, PUMPOFF);
}

// Processes command JSON data passed in
int processIngredient(aJsonObject *command) {
  // Check to make sure command exists
  if (!command) {
    return 0;
  }

  // Check command type
  aJsonObject *msgType = aJson.getObjectItem(command, "msgType");
  if (msgType && (msgType->valueint == 2)) {
    aJsonObject *liquor = aJson.getObjectItem(command, "liquor");

    aJsonObject *amountIn = aJson.getObjectItem(liquor, "amt");
    aJsonObject *bottleNumIn = aJson.getObjectItem(liquor, "bot"); 

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
        return 0;
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
        return 0;
      }

      dispenseLiquor(amount, bottleNum);
      return 3;
    }
  } else if (msgType && (msgType->valueint == 4)) {
    aJsonObject *amountIn = aJson.getObjectItem(command, "amt");
    aJsonObject *bottleNumIn = aJson.getObjectItem(command, "bot"); 

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
        return 0;
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
        return 0;
      }

      int liquorPin = selectPin(bottleNum);

      digitalWrite(liquorPin, PUMPON);
      delay(amount);
      digitalWrite(liquorPin, PUMPOFF);
  } else {
    return 0;
  }
}

// Creates response message to send back to Raspberry Pi
aJsonObject *createResponseMessage(int responseNum) {
  // Responses:
  // 0: data error
  // 1: drink cancelled
  // 2: start making drink (drink accepted)
  // 3: done pumping ingredient
  // 99: no response message necessary

  aJsonObject *packet = aJson.createObject();
  aJsonObject *responseItem = aJson.createItem(responseNum);
  aJson.addItemToObject(packet, "response", responseItem);

  return packet;
}
 
void setup() {
  Serial.begin(9600);

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
    aJsonObject *ingredient = aJson.parse(&serial_stream);
    int returnVal = processIngredient(ingredient);
    aJson.deleteItem(ingredient);
    serial_stream.flush();

    // Send response
    aJsonObject *response = createResponseMessage(returnVal);
    aJson.print(response, &serial_stream);
    Serial.println();
    aJson.deleteItem(response);
  }
}