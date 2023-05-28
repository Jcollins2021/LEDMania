import java.io.File;
import java.util.ArrayList;
import java.util.List;
import processing.core.PApplet;
import processing.serial.*;
import processing.sound.*;


public class App extends PApplet{
    private Serial port;
    private StepFileReader reader;
    private File mainDirectory;
    private ArrayList<String> packs;
    private String currentPack;
    private ArrayList<String> songs;
    private String currentSong;
    private ArrayList<String> difficulty;
    private String currentDifficulty;
    private int[] wordY;
    private int state;
    private boolean[] overText;
    private boolean overBack;
    private StepSongPlayer player;
    private ArrayList<ArrayList<Double>> stops;
    private int stopsIndex = 0;
    private ArrayList<ArrayList<Double>> BPMs;
    private int bpmsIndex = 0;;
    private double currentBPM;
    private int currentBeat;
    private boolean firstRunThrough;
    private int delayTime;
    private int startTime;
    private SoundFile music;
    private int amountOfNotes;
    private int beatSize;

    private boolean songStarted = false;
    private boolean musicStarted = false;

    public boolean firstMousePress = false;
    private HScrollbar hs1;  // Two scrollbars
    private int currentColor;
    private String serialName = "/dev/cu.usbserial-14130";
    final int BAUD_RATE = 230400;




    public void settings() {
        size(800, 600);
        //noStroke();
        currentColor = 120;
        reader = new StepFileReader();
        mainDirectory = new File("./src/Songs");
        packs = reader.getPlaylistFolders(mainDirectory);
        overText = new boolean[packs.size()];
        state = 0;
        wordY = getWordsY(packs);
        port = new Serial(this, serialName, BAUD_RATE); // open port

        hs1 = new HScrollbar(width-16, 30, 16, height-30, 16, this);
      }
      
    public void draw() {
        background(currentColor);
        hs1.update();
        hs1.display();
      
        //After it has been used in the sketch, set it back to false
        if (firstMousePress) {
          firstMousePress = false;
        }
        update(mouseX, mouseY);
        textSize(40);
        if (state == 3){
            textSize(40);
            fill(0, 408, 612);
            text("Currently playing on " + currentDifficulty, 10, 100);
            fill(GRAY);
            rect(0,0,width,30);
            if(overBack){
                fill(0, 408, 0);
            }else{
                fill(0, 408, 612);
            }
            textSize(30);
            text("<Back", 5, 25);
            if(!songStarted){
                loadSong(player);
                songStarted = true;
                firstRunThrough = true;
                musicStarted = false;
                currentBeat = 0;
            }else if(songStarted && firstRunThrough){
                delayTime = (int)((player.getOffset()*1000));
                delay(3000);//For grace period
                amountOfNotes = 0;
                firstRunThrough = false;
                currentBPM = BPMs.get(0).get(1);
                bpmsIndex = 1;
                startTime = millis();
            }else{
                if(delayTime != 0){
                    ArrayList<byte[]> measure = player.getMeasure();
                    for(int i = 0; i < 4; i++){
                        List<byte[]> beat = measure.subList((int)(measure.size()*(i/4.0)), (int)(measure.size()*((i+1)/4.0)));
                        currentBeat++;
                        int beatSize = beat.size();
                        int beatDelayTime = (int)(1000*(1/(currentBPM/60.0))/beatSize);
                        for(int j = 0; j < beatSize; j++){
                            port.write(beat.get(j));
                            delay(beatDelayTime);
                            if(amountOfNotes < 8){
                                amountOfNotes++;
                            }
                        }
                        if(BPMs.get(bpmsIndex).get(0) == currentBeat+1){
                            currentBPM = BPMs.get(bpmsIndex).get(1);
                            bpmsIndex++;
                        }if(stops.get(stopsIndex).get(0) == currentBeat){
                            delay((int)(stops.get(stopsIndex).get(1)*1000));
                            stopsIndex++;
                        }
                        if(startTime + delayTime <= millis() && !musicStarted && amountOfNotes == 8){
                            //Start music
                            music.play();
                            musicStarted = true;
                            delayTime = 0;
                        }
                    }
                }else{
                    ArrayList<byte[]> measure = player.getMeasure();
                    if(measure == null){
                        wordY = getWordsY(songs);
                        overText = new boolean[songs.size()];
                        int beatDelayTime = (int)(1000*(1/(currentBPM/60.0))/beatSize);
                        byte[] empty = {0,0,0,0};
                        for(int i = 0; i < 16; i++){
                            port.write(empty);
                            delay(beatDelayTime);
                        }
                        songStarted = false;
                        music.stop();
                        state = 1;
                        return;
                    }
                    for(int i = 0; i < 4; i++){
                        List<byte[]> beat = measure.subList((int)(measure.size()*(i/4.0)), (int)(measure.size()*((i+1)/4.0)));
                        currentBeat++;
                        beatSize = beat.size();
                        int beatDelayTime = (int)(1000*(1/(currentBPM/60.0))/beatSize);
                        for(int j = 0; j < beatSize; j++){
                            port.write(beat.get(j));
                            delay(beatDelayTime);
                        }
                        if(BPMs.get(bpmsIndex).get(0) == currentBeat+1){
                            currentBPM = BPMs.get(bpmsIndex).get(1);
                            bpmsIndex++;
                        }if(stops.get(stopsIndex).get(0) == currentBeat){
                            delay((int)(stops.get(stopsIndex).get(1)*1000));
                            stopsIndex++;
                        }
                    }
                }
            }
        }else if(state == 0){
            for(int i = 0; i < packs.size(); i++){
                if(overText[i]){
                    fill(0, 408, 0);
                }else{
                    fill(0, 408, 612);
                }
                text(packs.get(i), 10, wordY[i]-(int)(wordY[packs.size()-1]*hs1.getPos())); 
            }
            fill(GRAY);
            rect(0,0,width,30);
        }else if(state == 1){
            for(int i = 0; i < songs.size(); i++){
                if(overText[i]){
                    fill(0, 408, 0);
                }else{
                    fill(0, 408, 612);
                }
                text(songs.get(i), 10, wordY[i]-(int)(wordY[songs.size()-1]*hs1.getPos())); 
            }
            fill(GRAY);
            rect(0,0,width,30);
            if(overBack){
                fill(0, 408, 0);
            }else{
                fill(0, 408, 612);
            }
            textSize(30);
            text("<Back", 5, 25);
        }else if(state == 2){
            for(int i = 0; i < difficulty.size(); i++){
                if(overText[i]){
                    fill(0, 408, 0);
                }else{
                    fill(0, 408, 612);
                }
                text(difficulty.get(i), 10, wordY[i]-(int)(wordY[difficulty.size()-1]*hs1.getPos())); 
            }
            fill(GRAY);
            rect(0,0,width,30);
            if(overBack){
                fill(0, 408, 0);
            }else{
                fill(0, 408, 612);
            }
            textSize(30);
            text("<Back", 5, 25);
        }
      }

      public int[] getWordsY(ArrayList<String> list){
        int len = list.size();
        int[] retVal = new int[len];
        for(int i = 0; i < len; i++){
            retVal[i] = 100 + (40 * i);
        }
        return retVal;
      }
      
      public void mousePressed() {
        if (!firstMousePress) {
          firstMousePress = true;
        }
        int len = 0;
        if(state == 3){
            if(overBack){
                wordY = getWordsY(difficulty);
                overText = new boolean[difficulty.size()];
                state = 2;
                songStarted = false;
                music.stop();
                return;
            }
        }else if(state == 0){
            len = packs.size();
            for(int i = 0; i < len; i++){
                if(overText[i]){
                    currentPack = packs.get(i);
                    songs = reader.getPackList(new File(mainDirectory.getAbsolutePath() + "/" + currentPack));
                    wordY = getWordsY(songs);
                    overText = new boolean[songs.size()];
                    state = 1;
                    break;
                }
            }
        }else if(state == 1){
            len = songs.size();
            if(overBack){
                wordY = getWordsY(packs);
                overText = new boolean[packs.size()];
                state = 0;
                return;
            }
            for(int i = 0; i < len; i++){
                if(overText[i]){
                    currentSong = songs.get(i);
                    difficulty = reader.getDifficulty(new File(mainDirectory.getAbsolutePath() + "/" + currentPack + "/" + currentSong));
                    wordY = getWordsY(difficulty);
                    overText = new boolean[difficulty.size()];
                    state = 2;
                    break;
                }
            }
        }else if(state == 2){
            if(overBack){
                wordY = getWordsY(songs);
                overText = new boolean[songs.size()];
                state = 1;
                return;
            }
            len = difficulty.size();
            for(int i = 0; i < len; i++){
                if(overText[i]){
                    player = reader.startSong(new File(mainDirectory.getAbsolutePath() + "/" + currentPack + "/" + currentSong), difficulty.get(i));
                    currentDifficulty = difficulty.get(i);
                    state = 3;
                }
            }
        }
    }
      
      void update(int x, int y) {
        int len = 0;
        boolean changed = false;
        if(state == 3){
            if(overText(5, 30, (int)textWidth("<Back"), 25, true)){
                overBack = true;
                for(int j = 0; j < len; j++){
                    overText[j] = false;
                }
                changed = true;
            }
        }else if(state == 0){
            len = packs.size();
            for(int i = 0; i < len; i++){
                if(overText(10, wordY[i]-(int)(wordY[packs.size()-1]*hs1.getPos()), (int)textWidth(packs.get(i)), 40, false)){
                    for(int j = 0; j < len; j++){
                        overText[j] = false;
                    }
                    overText[i] = true;
                    changed = true;
                }
            }
        }else if(state == 1){
            len = songs.size();
            for(int i = 0; i < len; i++){
                if(overText(10, wordY[i]-(int)(wordY[songs.size()-1]*hs1.getPos()),(int)textWidth(songs.get(i)),40, false)){
                    for(int j = 0; j < len; j++){
                        overText[j] = false;
                    }
                    overBack = false;
                    overText[i] = true;
                    changed = true;
                }
            }
            if(overText(5, 30, (int)textWidth("<Back"), 25, true)){
                overBack = true;
                for(int j = 0; j < len; j++){
                    overText[j] = false;
                }
                changed = true;
            }
        }else if(state == 2){
            len = difficulty.size();
            for(int i = 0; i < len; i++){
                if(overText(10, wordY[i]-(int)(wordY[difficulty.size()-1]*hs1.getPos()),(int)textWidth(difficulty.get(i)),40, false)){
                    for(int j = 0; j < len; j++){
                        overText[j] = false;
                    }
                    overBack = false;
                    overText[i] = true;
                    changed = true;
                }
            }
            if(overText(5, 30, (int)textWidth("<Back"), 25, true)){
                overBack = true;
                for(int j = 0; j < len; j++){
                    overText[j] = false;
                }
                changed = true;
            }
        }
        if(!changed){
            for(int i = 0; i < len; i++){
                overText[i] = false;
                overBack = false;
            }
        }
    }

    private boolean overText(int x, int y, int width, int height, boolean back){
        if(!back && mouseY <= 30){
            return false;
        }else if(mouseX >= x && mouseX <= x+width && 
        mouseY <= y && mouseY >= y-height) {
            return true;
        } else {
            return false;
        }
    }

	private void loadSong(StepSongPlayer player){
        bpmsIndex = 0;
        stopsIndex = 0;
        BPMs = player.getBPMs();
        stops = player.getStops();
        ArrayList<Double> time = new ArrayList<Double>();
        time.add(-1.0);
        time.add(0.0);
        BPMs.add(time);
        stops.add(time);

        music = new SoundFile(this, player.getMP3());
    }

    public static void main(String[] args){
        String[] processingArgs = {"MySketch"};
		App mySketch = new App();
		PApplet.runSketch(processingArgs, mySketch);
    }
}
