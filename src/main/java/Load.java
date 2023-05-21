import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Load extends Application {

    @Override
    public void start(Stage stage) {
        UI ui = new UI();
        ui.load(stage);
    }

    public static void main(String[] args) {
        Constants.setNullImage();
        launch();
    }

}