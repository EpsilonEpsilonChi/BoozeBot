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
int maxIngredients       = 10;     // Max num of ingredients passed from RPi in JSON object
double timeToPourOneFlOz = 500;    // Time it takes to pour 1 fl oz (in milliseconds)

// Globals
int uniqueID = 0;
int lastPrint = 0;
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

  digitalWrite(liquorPin, PUMPON);
  delay(timeToPourOneFlOz * amount);
  digitalWrite(liquorPin, PUMPOFF);
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
        delay(1000);                          // ***** REMOVE AFTER TESTING *****
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
  aJsonObject *uniqueIDItem = aJson.createItem(uniqueID);
  aJson.addItemToObject(packet, "response", responseItem);
  aJson.addItemToObject(packet, "uniqueID", uniqueIDItem);

  if (response == 1) {
    aJsonObject *nullItem = aJson.createNull();
    aJson.addItemToObject(packet, "error", nullItem);
  } else {
    aJsonObject *errorItem = aJson.createItem(message);
    aJson.addItemToObject(packet, "error", errorItem);
  }

  if (uniqueID == 99) {
    uniqueID = 0;
  } else {
    uniqueID++;
  }

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
