import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.ArrayList;

public class Queue {
    private UI ui;
    private ArrayList<Song> songs;
    public Queue(UI ui) {
        this.ui = ui;
        this.songs = new ArrayList<Song>();
    }

    public void addSong(Song song) {
        this.songs.add(song);
        reloadQueue();
        updateQueue();
    }

    public void updateQueue() {
        if (ui.mediaPlayer == null) {

            if (this.songs.size() < 1) {
                ui.unloadSong();
                reloadQueue();
                return;
            }

            Song nextSong = this.songs.get(0);
            this.songs.remove(0);
            reloadQueue();
            ui.loadSong(nextSong);

        }
        //ui.mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING
    }

    public void reloadQueue() {
        ListView queueView = (ListView) this.ui.getScene().lookup("#queueList");

        queueView.getItems().clear();

        for (Song song : songs) {
            queueView.getItems().add(0, song.getTitle());
        }
    }
}
