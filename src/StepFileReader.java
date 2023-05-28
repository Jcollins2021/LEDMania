import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList; 


public class StepFileReader{
    private SongPackDirectory pack;

    public StepFileReader(){
        pack = new SongPackDirectory();
    }

    public ArrayList<String> getPlaylistFolders(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                if(fileEntry.isDirectory()){
                    pack.addPack(fileEntry.getName());
                }
            }
        }
        return pack.getPacks();
    }

    public ArrayList<String> getPackList(final File folder){
        if(pack.isSet(folder.getName())){
            return pack.getSongsFromPack(folder.getName());
        }
        ArrayList<String> names = new ArrayList<String>();
        for(final File fileEntry : folder.listFiles()){
            if(fileEntry.isDirectory()){
                names.add(fileEntry.getName());
            }
        }
        pack.addSong(names, folder.getName());
        return pack.getSongsFromPack(folder.getName());
    }

    private File getSM(final File folder){
        File song = null;
        if(folder.isDirectory()){
            for(final File songEntry: folder.listFiles()){
                if(!songEntry.isDirectory()){
                    String name = songEntry.getName();
                    if(name.substring(name.length()-3).equals(".sm")){
                        song = new File(songEntry.getAbsolutePath());
                    }
                }
            }
        }else{
            throw new Error("Not folder");
        }
        if(song != null){
            return song;
        }else{
            throw new Error("No sm file");
        }
    }

    public File getMP3(final File folder){
        File music = null;
        if(folder.isDirectory()){
            for(final File songEntry: folder.listFiles()){
                if(!songEntry.isDirectory()){
                    String name = songEntry.getName();
                    if(name.substring(name.length() - 4).equals(".mp3")){
                        music = new File(songEntry.getAbsolutePath());
                    }
                }
            }
            if(music == null){
                for(final File songEntry: folder.listFiles()){
                    if(!songEntry.isDirectory()){
                        String name = songEntry.getName();
                        if(name.substring(name.length() - 4).equals(".ogg")){
                            music = new File(songEntry.getAbsolutePath());
                        }
                    }
                }
            }
        }else{
            throw new Error("Not folder");
        }
        if(music != null){
            return music;
        }else{
            throw new Error("No mp3 file");
        }
    }

    public ArrayList<String> getDifficulty(File folder){
        ArrayList<String> difficulty = new ArrayList<String>();
        File sm = getSM(folder);
        BufferedReader reader;
        try{
            reader = new BufferedReader(new FileReader(sm));
        }catch(Exception error){
            System.out.println(sm.getAbsolutePath());
            System.out.println(error);
            return null;
        }
        String line = "";
        while(line != null){
            try{
                line = reader.readLine();
            }catch(Exception e){
                System.out.println("Line reader error");
                return null;
            }
            if(line == null){
                break;
            }
            if(line.equals("#NOTES:")){
                try{
                    reader.readLine();
                    reader.readLine();
                    line = reader.readLine();
                    line = line.replaceAll("\\s", "");
                    difficulty.add(line.substring(0,line.length()-1));
                    line = reader.readLine();
                    line = line.replaceAll("\\s", "");
                    int index = difficulty.size()-1;
                    difficulty.set(index, difficulty.get(index)+ " " + line.substring(0,line.length()-1));
                }catch(Exception e){
                    System.out.println("Line reader error");
                    return null;
                }
            }
        }
        try{
            reader.close();
        }catch(Exception e){
            System.out.println("Reader couldn't close");
            return null;
        }
        return difficulty;
    }

    public StepSongPlayer startSong(final File folder, String difficulty){
        File sm = getSM(folder);
        File mp3 = getMP3(folder);
        BufferedReader reader;
        double offset = 0;
        ArrayList<ArrayList<Double>> BPMs = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Double>> Stops = new ArrayList<ArrayList<Double>>();
        
        

        //Difficulty is a number
        try{
            reader = new BufferedReader(new FileReader(sm));
        }catch(Exception error){
            System.out.println(sm.getAbsolutePath());
            System.out.println(error);
            return null;
        }
        String line = "";
        boolean foundOffset = false; boolean foundStops = false; boolean foundBPMs = false;
        while(!(foundOffset && foundStops && foundBPMs)){
            try{
                line = reader.readLine();
                if(line.contains("OFFSET")){
                    foundOffset = true;
                    String temp = line.substring(line.indexOf(":")+1);
                    if(temp.contains(";"))
                        temp = temp.substring(0, temp.indexOf(";"));
                    offset = Double.parseDouble(temp);
                }if(line.contains("#BPM")){
                    foundBPMs = true;
                    String temp = line.substring(line.indexOf(":")+1,line.indexOf("="));
                    ArrayList<Double> tempList = new ArrayList<Double>();
                    tempList.add(Double.parseDouble(temp));
                    temp = line.substring(line.indexOf("=")+1);
                    if(temp.contains(",") || temp.contains(";")){
                        temp = temp.substring(0, temp.length()-1);
                    }
                    tempList.add(Double.parseDouble(temp));
                    BPMs.add(tempList);
                    try{
                        line = reader.readLine();
                    }catch(Exception e){
                        System.out.println("BPM Error");
                        return null;
                    }
                    while(!line.contains("#")){
                        try{
                            if(line.length() == 1)//If it's just a semicolon
                                break;
                            if(line.contains(",")){
                                line = line.replaceAll(",", "");
                            }
                            if(line.contains(";")){
                                line = line.replaceAll(";", "");
                            }
                            temp = line.substring(0, line.indexOf("="));
                            tempList = new ArrayList<Double>();
                            tempList.add(Double.parseDouble(temp));
                            temp = line.substring(line.indexOf("=") + 1);
                            tempList.add(Double.parseDouble(temp));
                            BPMs.add(tempList);
                            line = reader.readLine();
                        }catch(Exception e){
                            System.out.println("BPM Error");
                            return null;
                        }
                    }
                }if(line.contains("STOPS")){
                    foundStops = true;
                    if(!(line.contains("."))){
                        continue;
                    }
                    String temp = line.substring(line.indexOf(":")+1,line.indexOf("="));
                    ArrayList<Double> tempList = new ArrayList<Double>();
                    tempList.add(Double.parseDouble(temp));
                    temp = line.substring(line.indexOf("=")+1);
                    if(temp.contains(",") || temp.contains(";")){
                        temp = temp.substring(0, temp.length()-1);
                    }
                    tempList.add(Double.parseDouble(temp));
                    Stops.add(tempList);
                    try{
                        line = reader.readLine();
                    }catch(Exception e){
                        System.out.println("Stop Error");
                        return null;
                    }
                    while(!line.contains("#")){
                        try{
                            if(line.length() == 1)//If it's just a semicolon
                                break;
                            if(line.contains(",")){
                                line = line.replaceAll(",", "");
                            }
                            if(line.contains(";")){
                                line = line.replaceAll(";", "");
                            }
                            temp = line.substring(0, line.indexOf("="));
                            tempList = new ArrayList<Double>();
                            tempList.add(Double.parseDouble(temp));
                            temp = line.substring(line.indexOf("=") + 1);
                            tempList.add(Double.parseDouble(temp));
                            Stops.add(tempList);
                            line = reader.readLine();
                        }catch(Exception e){
                            System.out.println("Stop Error");
                            return null;
                        }
                    }
                }
            }catch(Exception e){
                System.out.println(e);
                return null;
            }
        }
        
        
        
        difficulty = "     " + difficulty.substring(difficulty.length()-2).replaceAll("\\s", "") + ":";
        while(line != null){
            try{
                line = reader.readLine();
            }catch(Exception e){
                System.out.println("Line reader error");
                return null;
            }
            if(line == null){
                break;
            }
            if(line.equals(difficulty)){
                try{
                    
                    line = reader.readLine();
                    while(!line.contains(":")){
                        line = reader.readLine();
                    }
                    break;
                }catch(Exception e){
                    System.out.println("Line reader error");
                    return null;
                }
            }
        }

        /*song = new Media(mp3.toURI().toString());
        

        System.out.println(line);
        while (!line.equals(";")){

        }*/
        return new StepSongPlayer(reader, BPMs, Stops, offset, mp3);
    }
}