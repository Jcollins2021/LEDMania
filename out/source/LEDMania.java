/* autogenerated by Processing revision 1292 on 2023-05-06 */
import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.io.File;
import .StepFileReader;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class LEDMania extends PApplet {




public void setup(){
    /* size commented out by preprocessor */;
    StepFileReader reader = new StepFileReader();
}

public void draw(){
  
}


  public void settings() { size(800, 600); }

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "LEDMania" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}