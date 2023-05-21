import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataLoader {

    public static Set<String> listDirectory(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    public static Set<String> loadSongDirectory() {
        Set songs = listDirectory(Constants.SONG_DIRECTORY);
        return songs;
    }

}
