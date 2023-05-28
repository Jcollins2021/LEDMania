import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;

public class StepSongPlayer{ //extends Application{
    BufferedReader reader;
    ArrayList<ArrayList<Double>> Stops;
    ArrayList<ArrayList<Double>> BPMs;
    double offset;
    File mp3;
    boolean ended;
    public StepSongPlayer(BufferedReader reader, ArrayList<ArrayList<Double>> BPMs, ArrayList<ArrayList<Double>> Stops, double offset
    , File mp3){
        this.reader = reader;
        this.BPMs = BPMs;
        this.Stops = Stops;
        this.offset = offset;
        this.mp3 = mp3;
        ended = false;
    }
    /*MediaPlayer media;
    public void start (Stage primaryStage) throws Exception {  
        // TODO Auto-generated method stub  
        //Initialising path of the media file, replace this with your file path   
          
        //Instantiating Media class    
          
        //by setting this property to true, the audio will be played
        primaryStage.setTitle("Playing Audio");  
        primaryStage.show();  
    }  */
    public boolean getEnded(){
        return ended;
    }
    public ArrayList<ArrayList<Double>> getStops(){
        return Stops;
    }
    public ArrayList<ArrayList<Double>> getBPMs(){
        return BPMs;
    }
    public double getOffset(){
        return offset;
    }
    public String getMP3(){
        return mp3.getAbsolutePath();
    }
    public ArrayList<byte[]> getMeasure(){
        try{
            ArrayList<byte[]> retVal = new ArrayList<byte[]>();
            String line = reader.readLine();
            while(!line.contains(",")){
                if(line.contains(";")){//End of song
                    try{
                        ended = true;
                        reader.close();
                        return retVal;
                    }catch(Exception e){
                        System.out.println("Reader couldn't clsoe");
                        return retVal;
                    }
                }
                while(line.contains("/")){
                    line = reader.readLine();
                }
                byte[] notes = new byte[4];
                if(line.charAt(0) == 'M'){
                    notes[0] = 5;
                }else{
                    notes[0] = (byte)(line.charAt(0)-48);
                }
                if(line.charAt(0) == 'M'){
                    notes[1] = 5;
                }else{
                    notes[1] = (byte)(line.charAt(1)-48);
                }
                if(line.charAt(0) == 'M'){
                    notes[2] = 5;
                }else{
                    notes[2] = (byte)(line.charAt(2)-48);
                }
                if(line.charAt(0) == 'M'){
                    notes[3] = 5;
                }else{
                    notes[3] = (byte)(line.charAt(3)-48);
                }
                retVal.add(notes);
                line = reader.readLine();
            }  
            return retVal;
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }
    /*public static void main(String[] args){

        launch(args);
    }*/
    public void endSong(){
        try{
            reader.close();
        }catch(Exception e){
            System.out.println("Couldn't close reader");
        }
    }
}
