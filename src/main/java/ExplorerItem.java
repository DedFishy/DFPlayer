import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class ExplorerItem extends VBox {
    private String name;
    private String path;

    private Label label;
    private ImageView image;
    private int WIDTH = 150;
    private int HEIGHT = 150;
    public ExplorerItem(String name, String path, Image image) {
        initialize(name, path, image);
    }

    public ExplorerItem(String name, String path) {
        try (InputStream imageStream = this.getClass().getResourceAsStream("none.png")) {
            File file = File.createTempFile("none", null);
            file.deleteOnExit();
            try(OutputStream outputStream = new FileOutputStream(file)){
                IOUtils.copy(imageStream, outputStream);
            } catch (FileNotFoundException e) {
                // handle exception here
            } catch (IOException e) {
                // handle exception here
            }
            initialize(name, path, new Image(file.toURI().toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void initialize(String name, String path, Image image) {
        this.name = name;
        this.path = path;
        this.image = new ImageView(image);

        this.setPrefSize(WIDTH, HEIGHT);

        if (name == null) {
            name = "Unknown";
        }

        if (name.length() > Constants.MAX_NAME_LENGTH) {
            name = name.substring(0, Constants.MAX_NAME_LENGTH) + "...";
        }

        this.label = new Label(name);
        this.label.setWrapText(true);
        this.getChildren().add(this.image);
        this.getChildren().add(this.label);
        this.label.setPadding(new Insets(10, 0, 0, 0));

        this.setStyle("-fx-background-color: " + Constants.COLORS.get("bg-secondary") + "; -fx-border-radius: 4 4 4 4; -fx-background-radius: 4 4 4 4;");
        this.setAlignment(Pos.CENTER);
        this.label.setStyle("-fx-text-fill: white;");
        this.label.setMaxWidth(WIDTH);
        this.label.setAlignment(Pos.CENTER);
        this.image.setFitHeight(HEIGHT);
        this.image.setFitWidth(WIDTH);
        this.image.setStyle("-fx-border-radius: 4 4 4 4; -fx-background-radius: 4 4 4 4;");

        this.setWidth(WIDTH);


        this.image.setPreserveRatio(true);

        this.clipChildren();
    }

    private void clipChildren() {

        final Rectangle outputClip = new Rectangle();
        outputClip.setArcWidth(0);
        outputClip.setArcHeight(0);
        this.setClip(outputClip);

        this.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            outputClip.setWidth(newValue.getWidth());
            outputClip.setHeight(newValue.getHeight());
        });
    }

    public String getPath() {
        return path + Constants.PATH_SEPARATOR + name;
    }
}
