import com.mpatric.mp3agic.*;
import javafx.collections.ObservableMap;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class Song {
    private String songName;
    private String artist;
    private String album;
    private String songPath;
    private String year;
    private String genre;
    private String comment;

    private String lyrics;
    private String composer;
    private String publisher;
    private String originalArtist;
    private String albumArtist;
    private String copyright;
    private String url;

    private byte[] art;
    private boolean playable = true;
    private ObservableMap<String, Object> metadata;

    public Song(String songPath) {
        this.songPath = songPath;
        loadMP3Meta();
    }

    public String getTitle() {
        return songName;
    }

    public Image getImage() {
        if (art == null) {
            return Constants.NO_SONG_IMAGE;
        }
        Image image = new Image(new ByteArrayInputStream(art));
        return image;
    }

    public Media getMedia() {
        Media media = new Media(new File(Constants.SONG_DIRECTORY + "/" + songPath).toURI().toString());
        return media;
    }

    public String getSongPath() {
        return songPath;
    }

    public boolean getPlayable() {
        return this.playable;
    }

    private void loadMP3Meta() {
        try {

            Mp3File mp3file = new Mp3File(Constants.SONG_DIRECTORY + "/" + songPath);
            if (mp3file.hasId3v2Tag()) {
                ID3v2 meta = mp3file.getId3v2Tag();
                songName = meta.getTitle();
                artist = meta.getArtist();
                album = meta.getAlbum();
                year = meta.getYear();
                genre = meta.getGenre() + " (" + meta.getGenreDescription() + ")";
                comment = meta.getComment();
                lyrics = meta.getLyrics();
                composer = meta.getComposer();
                publisher = meta.getPublisher();
                originalArtist = meta.getOriginalArtist();
                albumArtist = meta.getAlbumArtist();
                copyright = meta.getCopyright();
                url = meta.getUrl();
                art = meta.getAlbumImage();
            }
            else if (mp3file.hasId3v1Tag()) {
                ID3v1 meta = mp3file.getId3v1Tag();
                songName = meta.getTitle();
                artist = meta.getArtist();
                album = meta.getAlbum();
                year = meta.getYear();
                genre = meta.getGenre() + " (" + meta.getGenreDescription() + ")";
                comment = meta.getComment();
            } else {
                songName = this.songPath;
            }


        } catch (MediaException e) {
            System.out.println(e.getMessage());
        } catch (InvalidDataException e) {
            System.out.println("The file " + songPath + " is not valid.");
            playable = false;
        } catch (UnsupportedTagException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
