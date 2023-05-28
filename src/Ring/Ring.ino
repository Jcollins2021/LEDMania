#include <Adafruit_NeoPixel.h>
#include <Wire.h>
#include <Adafruit_SSD1306.h>

#define BUTTON0 12
#define BUTTON1 13
#define BUTTON2 14
#define BUTTON3 15
#define PIN 25
#define POT A0
#define BAUD_RATE 230400

long WHITE_COLOR = 0xFFFFFF;
long PURPLE = 0xA020F0;
long RED = 0XFF0000;
long GREEN = 0X00FF00;
long height = 17;
long width = 16;
long BLACK_COLOR = 0X000000;
long BLUE = 0x0000FF;
long SKY = 0x4fccf3;
int score = 0;
#define CYAN 0x07FF;
#define MAGENTA 0xF81F;
#define YELLOW 0xFFE0;

// #define boardshow_noglitch() {delay(1);pixel_board.show();delay(1);pixel_board.show();}

Adafruit_NeoPixel pixel_board = Adafruit_NeoPixel(256, PIN, NEO_GRB + NEO_KHZ800);
Adafruit_SSD1306 lcd(128, 64); // create display object

int board[17][16];

void detectPressed(int status[4]) {
  for (int i = 0; i < 4; i++) { // iterate through each button
    if (digitalRead(BUTTON0 + i) == LOW) { // if button is pressed
      if (status[i] == 0) {
        status[i] = 1; // set to 1 if button is pressed for the first time
      } else {
        status[i] = 2; // set to 2 if button is held down
      }
    } else {
      status[i] = 0; // set back to 0 if button is released
    }
  }
}
int buttonStatus[4] = {0,0,0,0};

void getIndexBoard(int board[16][16]){
    int count = 0;
    for(int i = 0; i<16; i++){
        int c = 15;
        for(int j = 0; j < 16; j++){
            if(i % 2 == 0){
                board[j][i] = count;
            }
            else{
                board[c][i] = count;
                c--;
            }
            count ++;
        }
    }
}

int index_arr[16][16]; //A 2D array that maps the index of the int[256] board

void setup() {
  getIndexBoard(index_arr);
  lcd.begin(SSD1306_SWITCHCAPVCC, 0x3C); // init
  lcd.clearDisplay();
  lcd.setTextColor(WHITE);
  lcd.setCursor(0, 28);
  lcd.print("Waiting for data...");
  lcd.display();

  pixel_board.begin();
  pixel_board.setBrightness(15);
  pixel_board.clear(); // clear all pixels
  pixel_board.setPixelColor(0,0);
  pixel_board.show();

  pinMode(BUTTON0, INPUT_PULLUP);
  pinMode(BUTTON1, INPUT_PULLUP);
  pinMode(BUTTON2, INPUT_PULLUP);
  pinMode(BUTTON3, INPUT_PULLUP);


  for(int i = 0; i<height; i++){
    for(int j = 0; j<16; j++){
      board[i][j] = 0;
      if(i < 16){
        pixel_board.setPixelColor(index_arr[i][j],BLACK_COLOR);
      }
    }
  }
  for (int i = 2; i < 14; i++) {
    pixel_board.setPixelColor(index_arr[0][i],WHITE_COLOR);
  }
  Serial.begin(BAUD_RATE);
  border(SKY);
}

bool isHold[4] = {false, false, false, false};
int arr[15][4];
int count[4] = {1,1,1,1};
byte notes[4];

void border(int color){
  for(int i = 0; i < height; i++){
    for(int j = 0; j < 16; j++){
      if(j < 2 || j > 13 && i < 16){
        pixel_board.setPixelColor(index_arr[i][j],color);
      }
    }
  }
  pixel_board.show();
}

int colors[6] = {BLACK_COLOR, PURPLE, BLUE, GREEN, GREEN, RED};
int timeout=0;
boolean changed;

void loop() {
  // border(SKY);

  if(Serial.available()){
    changed = false;
    Serial.readBytes(notes,4);
    lcd.clearDisplay();
    lcd.setCursor(0,28);
    
    for (int j = 0; j < 4; j++) {
      int color = notes[j];
      if(color == 2){
        isHold[j] = true;
      }
      else if(color == 3){
        isHold[j] = false;
        color = 2;
      }
      if(isHold[j]){
        color = 2;
      }
      for(int k = j*3+2; k < (j+1)*3+2; k++){
        board[0][k] = color;
        board[1][k] = color;
        pixel_board.setPixelColor(index_arr[0][k],colors[notes[j]]);
        pixel_board.setPixelColor(index_arr[1][k],colors[notes[j]]);
      }
      pixel_board.show();
      
    }
    for (int i = 2; i < 14; i++) {
      pixel_board.setPixelColor(index_arr[0][i],WHITE_COLOR);
    }
    // pixel_board.show();

    detectPressed(buttonStatus);
    for(int i = 0; i<4; i++){
      int index = i*3+2;
      // lcd.print((int)buttonStatus[i]);
      if((board[15][index] != 0 && board[15][index] == buttonStatus[i]) || board[15][index] == 2 && buttonStatus[i] == 1 || (board[16][index] != 0 && board[16][index] == buttonStatus[i]) || board[16][index] == 2 && buttonStatus[i] == 1){
        border(GREEN);
        changed = true;  
        score += 10;      
      }
      else if (board[15][index] == 0 && buttonStatus[i] == 1 || board[15][index] != 0 && buttonStatus[i] == 0 || board[15][index] == 1 && buttonStatus[i] == 2){
        border(RED);
        changed = true;
        if(score >= 0){
          score -= 2;
        }
      }
    }
    if(!changed){
      border(SKY);
    }
    lcd.print("Score: "+String(score));
    lcd.display();

    for (int i = 16; i > 1; i--) {
      for (int j = 2; j < 14; j++) {
        board[i-1][j] = board[i-2][j];
        board[i][j] = board[i-1][j];
        if(i < 16){
          pixel_board.setPixelColor(index_arr[i-1][j],colors[board[i][j]]);
          pixel_board.setPixelColor(index_arr[i][j],colors[board[i][j]]);
        }
      }
    }
    timeout=millis();
  }
  else if(timeout+3000 <= millis()){ 
    border(SKY);
    score = 0;
    for(int i = 0; i<4; i++){
      isHold[i] = false;
    }
    for(int i = 1; i<height-2; i++){
      for(int j = 2; j<14; j++){
        board[i][j] = 0;
        pixel_board.setPixelColor(index_arr[i][j],BLACK_COLOR);
      }
    }   
    pixel_board.show(); 
  }
}
