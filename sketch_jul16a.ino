int let13 = 13;
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(let13, OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  /*digitalWrite(let13, HIGH);
  delay(1000);
  digitalWrite(let13, LOW);
  delay(1000);*/
 
  if(Serial.available() > 0){
    char data;
    data = Serial.read();
   
    switch (data){
      case '1':
        digitalWrite(let13, HIGH);
        delay(3000);
        digitalWrite(let13, LOW);
        break;
      default:
        break;
    }
  }
  
}
