import java.util.ArrayList;

public class SongPackDirectory{
    private ArrayList<String> packNames;
    private ArrayList<ArrayList<String>> songNames; 

    public SongPackDirectory(){
        //songNames and packNames will correlate
        packNames = new ArrayList<String>();
        songNames = new ArrayList<ArrayList<String>>();
    }

    public void addPack(String name){
        packNames.add(name);
        songNames.add(null);
    }

    public void addSong(ArrayList<String> name, String pack){
        //Iterates through packNames incrementing count to find which pack
        int count = 0;
        for(final String packName : packNames){
            if(packName.equals(pack)){
                break;
            }
            count++;
        }
        songNames.set(count, name);
    }

    public ArrayList<String> getPacks(){
        return packNames;
    }

    public ArrayList<String> getSongsFromPack(String pack){
        //Iterates through packNames incrementing count to find which pack
        int count = 0;
        for(final String packName : packNames){
            if(packName.equals(pack)){
                break;
            }
            count++;
        }
        return songNames.get(count);
    }

    public boolean isSet(String pack){
        int count = 0;
        for(final String packName : packNames){
            if(packName.equals(pack)){
                break;
            }
            count++;
        }
        if(songNames.size() == 0 || songNames.get(count) == null){
            return false;
        }
        return true;
    }
}
