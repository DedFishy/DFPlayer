import javafx.scene.image.Image;
import javafx.scene.text.Font;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    static Font HEADER_FONT = new Font("Arial", 30);

    static int MAX_NAME_LENGTH = 15;
    static Map<String, String> COLORS  = new HashMap<String, String>() {{
        put("bg-primary", "#1f1f1f");
        put("bg-secondary", "#0a0a0a");
    }};

    static String SONG_DIRECTORY = "C:/Users/boyne/Music"; // TODO: Dynamically load this for the sake of humanity

    static String PATH_SEPARATOR = ">";

    static Image NO_SONG_IMAGE;

    public static void setNullImage() {
        try (
                InputStream imageStream = Constants.class.getResourceAsStream("disc.png")) {
            File file = File.createTempFile("none", null);
            file.deleteOnExit();
            try (OutputStream outputStream = new FileOutputStream(file)) {
                IOUtils.copy(imageStream, outputStream);
            } catch (FileNotFoundException e) {
                // handle exception here
            } catch (IOException e) {
                // handle exception here
            }
            NO_SONG_IMAGE = new Image(file.toURI().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
