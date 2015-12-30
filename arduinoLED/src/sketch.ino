#include <aJSON.h>
#include <Tlc5940.h>
#include <SimpleTimer.h>
#include <SoftwareSerial.h>

#define LCD_TX_PIN 2
#define PSU_POWER_PIN 12
#define BUTTON_PIN 8
#define BUTTON_LED_NUM 13
#define TOP_LED_STRIP 5
#define BOTTOM_LED_STRIP 6

// Config variables
int psuTurnOnTime   = 1000; // Time it takes for the PSU to turn on (in ms)
int autoShutoffTime = 1;    // Time BoozeBot stays on before shutting down (in secs)

// Globals
int timerId;
bool powerStatus   = false; // False = off, True = on
int powerHoldCount = 0;

SimpleTimer autoOffTimer;
aJsonStream serial_stream(&Serial);
SoftwareSerial lcd = SoftwareSerial(15, LCD_TX_PIN);

// Clear the LCD and return cursor to top left corner
void clearLCD() {
  lcd.write(0xFE);
  lcd.write(0x58);
  lcd.write(0xFE);
  lcd.write(0x48);
  delay(50);
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
  lcd.begin(9600);

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
  setLCDBacklight(0x00, 0x0, 0x0);
  delay(100);

  // Clear screen and return to top left corner
  clearLCD();
}

// Sets an individual LED to the inputted RGB values
void setLED(int ledNum, int red, int green, int blue) {
  int firstPinNum = (ledNum - 1) * 3;
  Tlc.set(firstPinNum, red);
  Tlc.set(firstPinNum + 1, green);
  Tlc.set(firstPinNum + 2, blue);
  Tlc.update();
}

// Power on power supply and run power on animation
void powerOn() {
  int bottleFadeDelay = 1;
  int stripFadeDelay  = 5;
  int bounceDelay     = 120;
  int blinkDelay      = 200;

  // Display boot up indicator
  setLCDBacklight(0x0, 0xFF, 0x0);
  clearLCD();
  lcd.print("Powering on...  ");

  // Blink button four times
  for (int count = 0; count < 4; count++) {
    setLED(BUTTON_LED_NUM, 0, 4095, 0);
    delay(blinkDelay);
    setLED(BUTTON_LED_NUM, 0, 0, 0);
    delay(blinkDelay);
  }

  // Turn on power supply
  powerStatus = true;
  digitalWrite(PSU_POWER_PIN, HIGH);
  delay(psuTurnOnTime);

  // Animation
  setLED(1, 0, 4095, 0);
  delay(bounceDelay);
  setLED(2, 0, 4095, 0);
  delay(bounceDelay);
  setLED(3, 0, 4095, 0);
  delay(bounceDelay);
  setLED(4, 0, 4095, 0);
  delay(bounceDelay);
  setLED(8, 0, 4095, 0);
  delay(bounceDelay);
  setLED(12, 0, 4095, 0);
  delay(bounceDelay);
  setLED(11, 0, 4095, 0);
  delay(bounceDelay);
  setLED(10, 0, 4095, 0);
  delay(bounceDelay);
  setLED(9, 0, 4095, 0);
  delay(bounceDelay);
  setLED(5, 0, 4095, 0);
  delay(bounceDelay);
  setLED(6, 0, 4095, 0);
  delay(bounceDelay);
  setLED(7, 0, 4095, 0);
  delay(2 * bounceDelay);

  for (int i = 4095; i >= 0; i-=5) { // Fade out green bottles
    for (int j = 1; j <= 12; j++) {
      setLED(j, 0, i, 0);
    }
    delay(bottleFadeDelay);
  }

  for (int i = 0; i <= 255; i++) {  // Fade in top strip
    analogWrite(TOP_LED_STRIP, i);
    delay(stripFadeDelay);
  }
  delay(50);

  for (int i = 0; i <= 4095; i+=5) { // Fade in white bottles
    for (int j = 1; j <= 12; j++) {
      setLED(j, i, i, i);
    }
    delay(bottleFadeDelay);
  }
  delay(50);

  for (int i = 0; i <= 255; i++) {  // Fade in bottom strip
    analogWrite(BOTTOM_LED_STRIP, i);
    delay(stripFadeDelay);
  }

  // Set display
  clearLCD();
  setLCDBacklight(0xFF, 0xFF, 0xFF);
  lcd.print("BoozeBot ready  ");
  lcd.print("No queued drinks");

  // Start auto-shutoff timer
  timerId = autoOffTimer.setTimeout(autoShutoffTime * 60000, powerOff);
}

// Run shutdown animation and turn off power supply
void powerOff() {
  int fadeDelay   = 5;
  int blinkDelay  = 200;

  // Display shutdown indicator
  clearLCD();
  setLCDBacklight(0xFF, 0x0, 0x0);
  lcd.print("Shutting down...");

  // Blink button four times
  for (int count = 0; count < 4; count++) {
    setLED(BUTTON_LED_NUM, 4095, 0, 0);
    delay(blinkDelay);
    setLED(BUTTON_LED_NUM, 0, 0, 0);
    delay(blinkDelay);
  }

  // Animation
  for (int i = 255; i >= 0; i--) {  // Fade off top and bottom, fade bottles to red
    analogWrite(TOP_LED_STRIP, i);
    analogWrite(BOTTOM_LED_STRIP, i);
    for (int j = 1; j <= 12; j++) {
      setLED(j, 4080, 16 * i, 16 * i);
    }

    delay(fadeDelay);
  }

  for (int i = 0; i <= 511; i++) {  // Fade out red in wave pattern
    if (i <= 255) {
      int firstOff = 255 - i;
      for (int j = 1; j <= 4; j++) {
        setLED(j, 16 * firstOff, 0, 0);
      }
    }

    if ((i >= 128) && (i <= 383)) {
      int secondOff = 383 - i;
      for (int j = 5; j <= 8; j++) {
        setLED(j, 16 * secondOff, 0, 0);
      }
    }

    if (i >= 256) {
      int thirdOff = 511 - i;
      for (int j = 9; j <= 12; j++) {
        setLED(j, 16 * thirdOff, 0, 0);
      }
    }
    delay(fadeDelay);
  }

  // Turn off power supply and set display
  powerStatus = false;
  digitalWrite(PSU_POWER_PIN, LOW);
  setLCDBacklight(0x0, 0x0, 0x0);
  clearLCD();
}

// Fades out all white lighting to make drink
void fadeOutLighting() {
  int fadeDelay = 10;

  for (int i = 255; i >= 0; i--) {  // Fade off top and bottom,
    analogWrite(TOP_LED_STRIP, i);
    analogWrite(BOTTOM_LED_STRIP, i);
    for (int j = 1; j <= 12; j++) {
      setLED(j, 16 * i, 16 * i, 16 * i);
    }

    delay(fadeDelay);
  }
}

// Fades in all white lighting after making drink
void fadeInLighting() {
  int fadeDelay = 10;

  for (int i = 0; i <= 255; i++) {  // Fade on top and bottom
    analogWrite(TOP_LED_STRIP, i);
    analogWrite(BOTTOM_LED_STRIP, i);
    for (int j = 1; j <= 12; j++) {
      setLED(j, 16 * i, 16 * i, 16 * i);
    }

    delay(fadeDelay);
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
  if (msgType && (msgType->valueint == 0)) {  // New drink on queue
    autoOffTimer.disable(timerId);

    // Display drink name on LCD
    aJsonObject *drinkName = aJson.getObjectItem(command, "drinkName");
    String drinkNameString = drinkName->valuestring;

    // Power on BoozeBot if necessary
    if (!powerStatus) {
      powerOn();
    }

    clearLCD();
    setLCDBacklight(0xFF, 0xFF, 0xFF);
    lcd.print("Queued drink:   ");
    lcd.print(drinkNameString.substring(0, 15));

    // Blink dispense button green until action received 
    int refreshRate = 1;
    bool pressed = false;
    bool pulseState = true;
    int holdCount = 0;
    int statusValue = 0;
    int pulseCounter = 0;
    while (statusValue == 0) {
      if (digitalRead(BUTTON_PIN)) {  // Check if button is pressed
        if (!pressed) { // Indicate press with white LED
          setLED(BUTTON_LED_NUM, 4095, 4095, 4095);
        }
        pressed = true;
        pulseCounter = 0;
        holdCount += refreshRate; // Count how long button is held
      } else {
        if (holdCount > 0) {  // Short press, exit loop
          setLED(BUTTON_LED_NUM, 0, 0, 4095);
          statusValue = 2;
        }
        pressed = false;
        holdCount = 0;

        // Pulsing LED logic
        if (pulseCounter >= 819) {
          if (pulseState) {
            setLED(BUTTON_LED_NUM, pulseCounter * 5);
            pulseState = false;
          } else {
            setLED(BUTTON_LED_NUM, 819 - (pulseCounter * 5))
            pulseState = true;
          }
          pulseCounter = 0;
        }
        pulseCounter += refreshRate;  
      }

      if (holdCount >= 3000) {  // Long press if held for 3 seconds, exit loop
        setLED(BUTTON_LED_NUM, 4095, 0, 0);
        statusValue = 1;
      }
      
      delay(refreshRate);
    }

    if (statusValue == 1) { // Drink cancelled
      clearLCD();
      setLCDBacklight(0xFF, 0x0, 0x0);
      lcd.print("Drink cancelled ");

      delay(2500);
      setLED(BUTTON_LED_NUM, 0, 0, 0);
      setLCDBacklight(0xFF, 0xFF, 0xFF);
      clearLCD();
      lcd.print("BoozeBot ready  ");
      delay(50);
      lcd.print("No queued drinks");

      // Restart auto-shutoff timer
      autoOffTimer.restartTimer(timerId);
      autoOffTimer.enable(timerId);

      return 1;
    } else if (statusValue == 2) {  // Start making drink

      fadeOutLighting();

      clearLCD();
      setLCDBacklight(0x0, 0x0, 0xFF);
      lcd.print("Making drink:     ");
      lcd.print(drinkNameString.substring(0, 15));

      return 2;
    }

    // turn off button LED?
  } else if (msgType && (msgType->valueint == 1)) { // Set LED with given values
    aJsonObject *led = aJson.getObjectItem(command, "led");

    if (led) {
      aJsonObject *num = aJson.getObjectItem(led, "num");

      if ((num->valueint > 0) && (num->valueint < 13)) {
        aJsonObject *r = aJson.getObjectItem(led, "r");
        aJsonObject *g = aJson.getObjectItem(led, "g");
        aJsonObject *b = aJson.getObjectItem(led, "b");
        setLED(num->valueint, r->valueint, g->valueint, b->valueint);
      }

      return 99;
    }
  } else if (msgType && (msgType->valueint == 3)) { // Drink completed

    fadeInLighting();

    setLED(BUTTON_LED_NUM, 0, 0, 0);
    setLCDBacklight(0xFF, 0xFF, 0xFF);
    clearLCD();
    lcd.print("BoozeBot ready  ");
    delay(50);
    lcd.print("No queued drinks");

    // Restart auto-shutoff timer
      autoOffTimer.restartTimer(timerId);
      autoOffTimer.enable(timerId);
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
  Serial.begin(9600);
  pinMode(TOP_LED_STRIP, OUTPUT);
  pinMode(BOTTOM_LED_STRIP, OUTPUT);
  pinMode(BUTTON_PIN, INPUT);
  pinMode(PSU_POWER_PIN, OUTPUT);
  digitalWrite(PSU_POWER_PIN, LOW);

  initLCD();
  clearLCD();

  serial_stream.flush();
}

void loop() {
  autoOffTimer.run();

  // Logic to allow power on and off with button hold
  while (digitalRead(BUTTON_PIN)) {
    if (powerStatus) {
      setLED(BUTTON_LED_NUM, 4095, 0, 0);

      if (powerHoldCount >= 2500) {
        powerOff();
      }
    } else {
      setLED(BUTTON_LED_NUM, 0, 4095, 0);

      if (powerHoldCount >= 2500) {
        powerOn();
      }
    }

    powerHoldCount++;
    delay(1);
  } 

  // Clear if button released
  if (!digitalRead(BUTTON_PIN)) {
    setLED(BUTTON_LED_NUM, 0, 0, 0);
    powerHoldCount = 0;
  }

  // Skip garbage
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
