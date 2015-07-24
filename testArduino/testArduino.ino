int incomingByte = 0;

void setup() {
  Serial.begin(9600);
  pinMode(13, OUTPUT);
}

// try to start sending JSON from computer

void loop() {
	if (Serial.available() > 0) {
		incomingByte = Serial.read();

		for (int i = 0; i < 5; i++) {
			digitalWrite(13, HIGH);
			delay(200);
			digitalWrite(13, LOW);
			delay(200);
		}
	}
}