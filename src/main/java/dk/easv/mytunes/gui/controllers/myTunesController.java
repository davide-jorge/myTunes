package dk.easv.mytunes.gui.controllers;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.gui.models.PlaylistModel;
import dk.easv.mytunes.gui.models.SongModel;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class myTunesController implements Initializable {

    @FXML
    private Label playingSongTitle;

    @FXML
    private ProgressBar songProgress;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Button playButton, pauseButton, stopButton, skipBackwardsButton, skipForwardsButton;

    @FXML
    private ListView<Song> lstSongs;

    @FXML
    private ListView<Playlist> lstPlaylists;

    private MediaPlayer mediaPlayer;
    private List<Song> songs;
    private Song currentSong;
    private SongModel songModel = new SongModel();
    //private PlaylistModel playlistModel = new PlaylistModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        songs = songModel.getSongs();
        lstSongs.getItems().addAll(songs);
        lstSongs.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Song>() {
            @Override
            public void changed(ObservableValue<? extends Song> observable, Song oldValue, Song newValue) {
                currentSong = newValue;
                loadMedia();
            }
        });
        playButton.setOnAction(event -> playMedia());
        pauseButton.setOnAction(event -> pauseMedia());
        stopButton.setOnAction(event -> stopMedia());
        skipForwardsButton.setOnAction(event -> skipForward());
        skipBackwardsButton.setOnAction(event -> skipBackward());
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number > arg0, Number arg1, Number arg2) {
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            }
        });
    }

    private void loadMedia() {
        if (currentSong != null) {
            // Get the relative file path from the database
            String fileName = currentSong.getFile_path();

            // Form the complete file path
            String mediaFolder = "media"; // Name of the folder where the files are stored
            File file = new File(mediaFolder, fileName);

            // Check if the file exists
            if (!file.exists()) {
                System.out.println("File does not exist: " + file.getAbsolutePath());
                return; // Exit if the file is not found
            }

            try {
                // Create a Media object with the full path
                Media media = new Media(file.toURI().toString());

                // Initialize the MediaPlayer
                if (mediaPlayer != null) {
                    mediaPlayer.stop(); // Stop any currently playing media
                }

                mediaPlayer = new MediaPlayer(media);
                // Update the label to show the current song name
                playingSongTitle.setText(currentSong.getTitle());
                // Bind the progress bar to the current time of the song
                mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    double progress = newValue.toSeconds() / mediaPlayer.getMedia().getDuration().toSeconds();
                    songProgress.setProgress(progress);  // Update progress bar
                });
            } catch (Exception e) {
                e.printStackTrace(); // Handle any exceptions that occur
            }
        }
    }

    private void playMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            mediaPlayer.play();
            playButton.setDisable(true); // Disable the Play button when playing
        }
    }

    private void pauseMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            playButton.setDisable(false); // Enable the Play button when paused
        }
    }

    private void stopMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private void skipForward() {
        // Get the current selected index
        int currentIndex = lstSongs.getSelectionModel().getSelectedIndex();
        // If there is a next song, select it and play it
        if (currentIndex < songs.size() - 1) {
            lstSongs.getSelectionModel().select(currentIndex + 1);
            currentSong = lstSongs.getSelectionModel().getSelectedItem();
            loadMedia();
            playMedia();
        }
    }

    private void skipBackward() {
        // Skip to the previous song in the list
        int currentIndex = lstSongs.getSelectionModel().getSelectedIndex();
        if (currentIndex > 0) {
            lstSongs.getSelectionModel().select(currentIndex - 1);
            currentSong = lstSongs.getSelectionModel().getSelectedItem();
            loadMedia();
            playMedia();
        }
    }
}
