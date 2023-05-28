import processing.core.PApplet;

public class HScrollbar extends PApplet{
    int swidth, sheight;    // width and height of bar
    float xpos, ypos;       // x and y position of bar
    float spos, newspos;    // x position of slider
    float sposMin, sposMax; // max and min values of slider
    int loose;              // how loose/heavy
    boolean over;           // is the mouse over the slider?
    boolean locked;
    float ratio;
    App t;
  
    public HScrollbar (float xp, float yp, int sw, int sh, int l, App t) {
        this.t = t;
        swidth = sw;
        sheight = sh;
        xpos = xp;
        ypos = yp;
        spos = ypos;
        newspos = spos;
        sposMin = ypos;
        sposMax = ypos + sheight - swidth;
        loose = l;
    }
  
    public void update() {
      if (overEvent()) {
        over = true;
      } else {
        over = false;
      }
      if (t.firstMousePress && over) {
        locked = true;
      }
      if (!t.mousePressed) {
        locked = false;
      }
      if (locked) {
        newspos = constrain(t.mouseY, sposMin, sposMax);
      }
      if (abs(newspos - spos) > 1) {
        spos = spos + (newspos-spos)/loose;
      }
    }
  
    /*public float constrain(float val, float minv, float maxv) {
      return min(max(val, minv), maxv);
    }*/
  
    public boolean overEvent() {
      if (t.mouseX > xpos && t.mouseX < xpos+swidth &&
        t.mouseY > ypos && t.mouseY < ypos+sheight) {
        return true;
      } else {
        return false;
      }
    }
  
    void display() {
      t.noStroke();
      t.fill(204);
      t.rect(xpos, ypos, swidth, sheight);
      if (over || locked) {
        t.fill(0, 0, 0);
      } else {
        t.fill(102, 102, 102);
      }
      t.rect(xpos, spos, swidth, swidth);
    }
  
    float getPos() {
      // Convert spos to be values between
      // 0 and the total width of the scrollbar
      return spos / sheight;
    }
  }