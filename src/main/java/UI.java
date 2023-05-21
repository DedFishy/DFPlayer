import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class UI {
    private Parent root;
    private GridPane layout;
    private RowConstraints mainRow;
    private RowConstraints playerRow;
    private HBox playerContainer;
    private Slider playerProgress;
    private VBox queueContainer;
    private ListView queueList;
    private final double CONTROL_HEIGHT = 40;
    private TilePane explorerView;
    private Explorer explorer;
    private Pane rootContainer;
    private ScrollPane explorerViewContainer;

    private Queue queue;

    MediaPlayer mediaPlayer;
    private Slider seeker;
    private Label songTitle;
    private Button playButton;
    private Scene scene;

    public UI() {
        explorer = new Explorer();
        try {
            root = FXMLLoader.load(getClass().getResource("UI.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*
        layout = new GridPane();
        root.getChildren().add(layout);

        layout.setStyle("-fx-background-color: " + Constants.COLORS.get("bg-primary"));
        layout.setGridLinesVisible(true);
        layout.prefWidthProperty().bind(root.widthProperty()); // Lock the grid to fill the whole screen horizontally
        layout.prefHeightProperty().bind(root.heightProperty()); // And vertically

        mainRow = new RowConstraints(); // The main row
        mainRow.setVgrow(Priority.ALWAYS); // Make it fill the screen height

        playerRow = new RowConstraints(); // The audio control row

        queueColumn = new ColumnConstraints(200, Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        queueContainer = new VBox();
        Label queueLabel = new Label("Queue");
        queueLabel.setStyle("-fx-text-fill: white;");
        queueContainer.getChildren().add(queueLabel);
        queueList = new ListView(); // The actual list of songs in the queue
        queueList.setStyle("-fx-background-color: " + Constants.COLORS.get("bg-secondary"));
        queueContainer.getChildren().add(queueList);
        layout.add(queueContainer, 0, 0);

        explorerColumn = new ColumnConstraints(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE, Double.MAX_VALUE);
        explorerColumn.setHgrow(Priority.ALWAYS); // The explorer will take precedence over the queue in terms of size
        explorerView = new TilePane();
        explorerView.setHgap(8);
        explorerView.setVgap(8);
        explorerView.setPadding(new Insets(8, 8, 8, 8));
        explorerView.setPrefColumns(4);
        explorerView.setAlignment(Pos.CENTER);
        layout.add(explorerView, 1, 0);

        playerContainer = new HBox();
        playerContainer.setAlignment(Pos.CENTER);
        playerProgress = new Slider(0, 1, 0); // The actual list of songs in the queue
        playerContainer.getChildren().add(playerProgress);
        layout.add(playerContainer, 1, 1);

        layout.getRowConstraints().add(mainRow);
        layout.getRowConstraints().add(playerRow);
        layout.getColumnConstraints().addAll(queueColumn, explorerColumn);
        */
    }

    public void loadPathIntoExplorer(String path) {
        Object explorerData = explorer.getPath(path);

        explorerView.getChildren().clear();

        if (explorerData instanceof HashMap<?,?>) {
            Set subdirectories = ((HashMap<?, ?>) explorerData).keySet();
            for (Object subdirectory : subdirectories) {

                ExplorerItem item = new ExplorerItem((String) subdirectory, path);
                explorerView.getChildren().add(item);

                item.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        loadPathIntoExplorer(item.getPath());
                    }
                });

            }
        } else if (explorerData instanceof HashSet<?>) {
            for (Song song : (HashSet<Song>) explorerData) {

                ExplorerItem item = new ExplorerSong(song);
                explorerView.getChildren().add(item);

                item.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        queue.addSong(song);
                    }
                });
            }
        } else {
            System.out.println(explorerData.getClass());
        }
    }

    public void loadSong(Song song) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        songTitle.setText(song.getTitle());

        mediaPlayer = new MediaPlayer(song.getMedia());

        setPlayerListeners();
        setSongControls();

        mediaPlayer.play();
    }

    public void unloadSong() {
        songTitle.setText("No song is playing");
        mediaPlayer = null;
        seeker.setDisable(true);
    }

    public void setPlayerListeners() {
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration duration, Duration t1) {
                setSongControls();
            }
        });

        mediaPlayer.setOnPlaying(new Runnable() {
            public void run() {

            }
        });

        mediaPlayer.setOnPaused(new Runnable() {
            public void run() {
                setSongControls();
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            public void run() {
                setSongControls();
            }
        });

        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                mediaPlayer = null;
                queue.updateQueue();
            }
        });
    }

    public void setSongControls() {
        if (mediaPlayer == null) {
            seeker.setDisable(true);
            return;
        }
        Duration duration = mediaPlayer.getTotalDuration();
        seeker.setDisable(duration.isUnknown());
        if (!seeker.isDisabled()
                && duration.greaterThan(Duration.ZERO)
                && !seeker.isValueChanging()) {
            seeker.setMax(duration.toMillis() * 100.0);
            seeker.setValue(mediaPlayer.getCurrentTime().toMillis() * 100.0);
        }

        playButton.setText(mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING ? "||" : ">");
    }

    public void togglePlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.play();
            }
        }
    }

    public void load(Stage stage) {
        scene = new Scene(root, 500, 500);
        stage.setTitle("DFPlayer");
        stage.setScene(scene);
        stage.show();

        rootContainer = (Pane) scene.lookup("#rootContainer");
        layout = (GridPane) scene.lookup("#layout");
        explorerView = (TilePane) scene.lookup("#explorerView");
        explorerViewContainer = (ScrollPane) scene.lookup("#explorerViewContainer");
        queueList = (ListView) scene.lookup("#queueList");
        songTitle = (Label) scene.lookup("#loadedSongTitle");
        playButton = (Button) scene.lookup("#playButton");
        seeker = (Slider) scene.lookup("#songSeeker");

        // Set up the elements that can't be set up with CSS

        //Make the grid pane's height be equal to the window's height
        layout.prefHeightProperty().bind(rootContainer.heightProperty());

        //Make the play button toggle playback
        playButton.setOnMouseClicked(mouseEvent -> togglePlayer());

        //Make the seekbar seek the media player


        seeker.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                boolean needsReset = false;
                if (seeker.isFocused()) {
                    explorerViewContainer.requestFocus();
                    needsReset = true;
                }
                if ((seeker.isValueChanging() && mediaPlayer != null) || needsReset) {
                    // multiply duration by percentage calculated by slider position

                    mediaPlayer.seek(Duration.millis(seeker.getValue() / 100.0));
                    setSongControls();

                }
            }
        });



        //Set up the explorer scroll container

        explorerViewContainer.setFitToWidth(true);
        explorerViewContainer.setFitToHeight(true);
        explorerViewContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        explorerViewContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        // Row and column constraints for the main grid pane

        RowConstraints mainRowConstraints = new RowConstraints();
        mainRowConstraints.setVgrow(Priority.ALWAYS);

        RowConstraints playerRowConstraints = new RowConstraints();
        playerRowConstraints.setPrefHeight(CONTROL_HEIGHT);

        ColumnConstraints queueColumnConstraints = new ColumnConstraints(200, Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        ColumnConstraints explorerColumnConstraints = new ColumnConstraints(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE, Double.MAX_VALUE);

        explorerColumnConstraints.setHgrow(Priority.ALWAYS);

        layout.getRowConstraints().addAll(
                mainRowConstraints,
                playerRowConstraints
        );

        layout.getColumnConstraints().addAll(
                queueColumnConstraints, explorerColumnConstraints
        );

        // Initialize queue
        this.queue = new Queue(this);

        // Set up the controls
        setSongControls();

        // Finally, load the root directory

        loadPathIntoExplorer("");
    }

    public Scene getScene() {
        return this.scene;
    }
}
