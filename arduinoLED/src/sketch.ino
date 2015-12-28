#include <aJSON.h>
#include <Tlc5940.h>
#include <SoftwareSerial.h>

#define LCD_TX_PIN 2
#define PSU_POWER_PIN 12
#define BUTTON_PIN 4
#define BUTTON_LED_NUM 13
#define TOP_LED_STRIP 5
#define BOTTOM_LED_STRIP 6

// TO DO: Turn off power supply on timer
// NOTE: aJson stream acts weird when serialBaud is not 9600 for some reason...

// Config variables
int serialBaud           = 9600;    // Bit rate of serial connection to Rasp Pi
int lcdBaud              = 9600;    // Bit rate of serial connection to LCD
int psuTurnOnTime        = 1000;    // Time it takes for the PSU to turn on (in ms)

// Initialize LCD display
SoftwareSerial lcd = SoftwareSerial(15, LCD_TX_PIN);     // Change RX pin?

// Globals
aJsonStream serial_stream(&Serial);

// Clear the LCD and return cursor to top left corner
void clearLCD() {
  lcd.write(0xFE);
  lcd.write(0x58);
  lcd.write(0xFE);
  lcd.write(0x48);
  delay(10);
}

// Set :CD backlight RGB values based on inputs
void setLCDBacklight(uint8_t red, uint8_t green, uint8_t blue) {
  lcd.write(0xFE);
  lcd.write(0xD0);
  lcd.write(red);
  lcd.write(green);
  lcd.write(blue);
  delay(10);
}

// Initializes LCD Display
void initLCD() {
  lcd.begin(lcdBaud);

  // Set the size of the display
  lcd.write(0xFE);
  lcd.write(0xD1);
  lcd.write(16);    // 16 columns
  lcd.write(2);     // 2 rows
  delay(10); 

  // Set the contrast
  lcd.write(0xFE);
  lcd.write(0x50);
  lcd.write(200);
  delay(10);   

  // Set the brightness
  lcd.write(0xFE);
  lcd.write(0x99);
  lcd.write(255);
  delay(10);  

  // Turn off cursors
  lcd.write(0xFE);
  lcd.write(0x4B);
  lcd.write(0xFE);
  lcd.write(0x54);
  delay(10);

  // Splash screen
  setLCDBacklight(0xFF, 0x0, 0x0);
  lcd.print("BoozeBot by:    ");
  lcd.print("EEX House");
  delay(500);
  setLCDBacklight(0x0, 0xFF, 0x0);
  delay(500);
  setLCDBacklight(0x0, 0x0, 0xFF);
  delay(500);

  // Clear screen and return to top left corner
  clearLCD();
}

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

// Sets an individual LED to the inputted RGB values
void setLED(int ledNum, int red, int green, int blue) {
  int firstPinNum = (ledNum - 1) * 3;
  Tlc.set(firstPinNum, red);
  Tlc.set(firstPinNum + 1, green);
  Tlc.set(firstPinNum + 2, blue);
  Tlc.update();
}

// Fades on ambient (white) LED strips
void fadeOnAmbient() {
  for (int i = 0; i <= 255; i++) {
    analogWrite(TOP_LED_STRIP, i);
    analogWrite(BOTTOM_LED_STRIP, i);
    delay(3);
  }
}

// Processes command JSON data passed in
int processCommand(aJsonObject *command) {
  // Check to make sure command exists
  if (!command) {
    return 0;
  }

  // Check command type
  aJsonObject *msgType = aJson.getObjectItem(command, "msgType");
  if (msgType && (msgType->valueint == 0)) {          // New drink on queue
    // Display drink name on LCD
    aJsonObject *drinkName = aJson.getObjectItem(command, "drinkName");

    clearLCD();
    setLCDBacklight(0xFF, 0xFF, 0xFF);
    lcd.print("Next drink:     ");
    lcd.print(drinkName->valuestring);                // To do: Handle wrap around if > 16 characters

    // Blink dispense button green until action received 
    // To do: make LED fade pulse instead of blink
    int blinkRate = 400;
    int refreshRate = 5;

    bool pressed = false;
    bool blinkState = true;
    int holdCount = 0;
    int statusValue = 0;
    int blinkCounter = 0;
    while (statusValue == 0) {
      if (digitalRead(BUTTON_PIN)) {                  // Check if button is pressed
        if (!pressed) {                               // Indicate press with white LED
          setLED(BUTTON_LED_NUM, 4095, 4095, 4095);
        }
        pressed = true;
        blinkCounter = 0;
        holdCount += refreshRate;                     // Count how long button is held
      } else {
        if (holdCount > 0) {                          // Short press, exit loop
          setLED(BUTTON_LED_NUM, 0, 0, 4095);
          statusValue = 2;
        }
        pressed = false;
        holdCount = 0;

        if (blinkCounter >= blinkRate) {              // Check if time to change LED (for blink)
          if (blinkState) {
            setLED(BUTTON_LED_NUM, 0, 4095, 0);
            blinkState = false;
          } else {
            setLED(BUTTON_LED_NUM, 0, 0, 0);
            blinkState = true;
          }
          blinkCounter = 0;
        }

        blinkCounter += refreshRate;  
      }

      if (holdCount >= 3000) {                        // Long press if held for 3 seconds, exit loop
        setLED(BUTTON_LED_NUM, 4095, 0, 0);
        statusValue = 1;
      }
      
      delay(refreshRate);
    }

    if (statusValue == 1) {                           // Drink cancelled
      clearLCD();
      setLCDBacklight(0xFF, 0x0, 0x0);
      lcd.print("Drink cancelled ");

      return 1;
    } else if (statusValue == 2) {                    // Start making drink
      clearLCD();
      setLCDBacklight(0x0, 0x0, 0xFF);
      lcd.print("Making drink:     ");
      lcd.print(drinkName->valuestring);              // To do: Handle wrap around if > 16 characters
      digitalWrite(PSU_POWER_PIN, HIGH);              // Turn on power supply
      delay(500);
      fadeOnAmbient();

      return 2;
    }

    // turn off button LED?
  } else if (msgType && (msgType->valueint == 1)) {   // Set LED with given values
    aJsonObject *led = aJson.getObjectItem(command, "led");

    if (led) {
      aJsonObject *num = aJson.getObjectItem(led, "num");
      aJsonObject *r = aJson.getObjectItem(led, "r");
      aJsonObject *g = aJson.getObjectItem(led, "g");
      aJsonObject *b = aJson.getObjectItem(led, "b");
      setLED(num->valueint, r->valueint, g->valueint, b->valueint);

      return 99;
    }
  } else if (msgType && (msgType->valueint == 3)) {   // Drink completed
    setLED(BUTTON_LED_NUM, 0, 0, 0);
    setLCDBacklight(0xFF, 0xFF, 0xFF);
    clearLCD();
    lcd.print("No queued drinks");
    fadeOnAmbient();
  }
}

// Creates response message to send back to Raspberry Pi
aJsonObject *createResponseMessage(int response) {
  // Responses:
  // 0: data error
  // 1: drink cancelled
  // 2: start making drink (drink accepted)
  // 3: done pumping ingredient
  // 99: no response message necessary
  
  aJsonObject *packet = aJson.createObject();
  aJsonObject *responseItem = aJson.createItem(response);
  aJson.addItemToObject(packet, "response", responseItem);

  return packet;
}

void setup() {
  Tlc.init();
  Serial.begin(serialBaud);
  pinMode(TOP_LED_STRIP, OUTPUT);
  pinMode(BOTTOM_LED_STRIP, OUTPUT);
  pinMode(BUTTON_PIN, INPUT);
  pinMode(PSU_POWER_PIN, OUTPUT);
  digitalWrite(PSU_POWER_PIN, LOW);

  initLCD();
  setLCDBacklight(0xFF, 0xFF, 0xFF);
  clearLCD();
  lcd.print("No queued drinks");

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
    if (returnVal != 99) {
      aJsonObject *response = createResponseMessage(returnVal);
      aJson.print(response, &serial_stream);
      Serial.println();
      aJson.deleteItem(response);
    }
  }
}
