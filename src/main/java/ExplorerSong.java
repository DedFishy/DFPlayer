import javafx.scene.image.Image;

public class ExplorerSong extends ExplorerItem {
    public ExplorerSong(Song song) {
        super(song.getTitle(), null, song.getImage());
    }
    public ExplorerSong(String name, String path) {
        super(name, path);
    }

}
