import processing.core.PApplet;

public class test extends PApplet{
public boolean firstMousePress = false;
private HScrollbar hs1;  // Two scrollbars
int rectX, rectY;      // Position of square button
int circleX, circleY;  // Position of circle button
int rectSize = 90;     // Diameter of rect
int circleSize = 93;   // Diameter of circle
int rectColor, circleColor, baseColor;
int rectHighlight, circleHighlight;
int currentColor;
boolean rectOver = false;
boolean circleOver = false;

public void settings() {
  size(800, 600);
  //noStroke();

  hs1 = new HScrollbar(width-16, 0, 16, height, 16, this);

  rectColor = color(0);
  rectHighlight = color(51);
  circleColor = color(255);
  circleHighlight = color(204);
  baseColor = color(102);
  currentColor = baseColor;
  circleX = width/2+circleSize/2+10;
  circleY = height/2;
  rectX = width/2-rectSize-10;
  rectY = height-rectSize/2;
  //this.ellipseMode(CENTER);

  // Load images
  /*img1 = loadImage("");
  img2 = loadImage("");*/
}

public void draw() {
    background(currentColor);

    rectY = height-(int)(hs1.getPos()*height)+300;

  hs1.update();
  hs1.display();

  //stroke(0);

  //After it has been used in the sketch, set it back to false
  if (firstMousePress) {
    firstMousePress = false;
  }
  update(mouseX, mouseY);
  
  if (rectOver) {
    fill(rectHighlight);
  } else {
    fill(rectColor);
  }
  stroke(255);
  rect(rectX, rectY, rectSize, rectSize);
  
  if (circleOver) {
    fill(circleHighlight);
  } else {
    fill(circleColor);
  }
  stroke(0);
  ellipse(circleX, circleY, circleSize, circleSize);
}

public void mousePressed() {
  if (!firstMousePress) {
    firstMousePress = true;
  }
  if (circleOver) {
    currentColor = circleColor;
  }
  if (rectOver) {
    currentColor = rectColor;
  }
}

void update(int x, int y) {
    if ( overCircle(circleX, circleY, circleSize) ) {
      circleOver = true;
      rectOver = false;
    } else if ( overRect(rectX, rectY, rectSize, rectSize) ) {
      rectOver = true;
      circleOver = false;
    } else {
      circleOver = rectOver = false;
    }
  }
  
  boolean overRect(int x, int y, int width, int height)  {
    if (mouseX >= x && mouseX <= x+width && 
        mouseY >= y && mouseY <= y+height) {
      return true;
    } else {
      return false;
    }
  }
  
  boolean overCircle(int x, int y, int diameter) {
    float disX = x - mouseX;
    float disY = y - mouseY;
    if (sqrt(sq(disX) + sq(disY)) < diameter/2 ) {
      return true;
    } else {
      return false;
    }
  }


public static void main(String[] args){
    String[] processingArgs = {"MySketch"};
    test mySketch = new test();
    PApplet.runSketch(processingArgs, mySketch);
}
}