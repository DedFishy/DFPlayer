import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Explorer {
    HashMap<String, Object> directories;

    Set<Song> songs;
    public Explorer() {
        directories = new HashMap<>();
        songs = new HashSet<Song>() {};

        Set<String> songFileNames = DataLoader.loadSongDirectory();

        for (String file : songFileNames) {
            Song song = new Song(file);
            if (song.getPlayable()) {
                songs.add(song);
            }
        }
        directories.put("songs", songs);
    }

    public Object getPath(String rawPath) {
        String[] path = rawPath.split(Constants.PATH_SEPARATOR);

        HashMap currentLayer = directories;

        for(String directory : path) {

            Object nextLayer = currentLayer.get(directory);
            if (nextLayer instanceof Set) {
                return nextLayer;
            }
            else if (nextLayer instanceof HashMap<?,?>) {
                currentLayer = (HashMap) nextLayer;
            }
        }
        return currentLayer;
    }
}
