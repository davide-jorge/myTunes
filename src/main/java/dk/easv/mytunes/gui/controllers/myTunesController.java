package dk.easv.mytunes.gui.controllers;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.gui.models.PlaylistModel;
import dk.easv.mytunes.gui.models.SongModel;

import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class myTunesController implements Initializable {

    @FXML
    private Label playingSongName;

    @FXML
    private ProgressBar songProgress;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Button playButton;

    @FXML
    private Button pauseButton;

    @FXML
    private Button stopButton;

    @FXML
    private Button skipBackwardsButton;

    @FXML
    private Button skipForwardsButton;

    @FXML
    private ListView<Song> lstSongs;

    @FXML
    private ListView<Playlist> lstPlaylists;

    private MediaPlayer mediaPlayer;
    private SongModel songModel = new SongModel();
    private PlaylistModel playlistModel = new PlaylistModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lstSongs.setItems(songModel.getObservableSongList());

        lstPlaylists.setItems(playlistModel.getObservablePlaylistList());

        lstSongs.setCellFactory(param -> new ListCell<Song>() {
            @Override
            protected void updateItem(Song song, boolean empty) {
                super.updateItem(song, empty);
                if (song != null) {
                    setText(song.getTitle());
                } else {
                    setText(null);
                }
            }
        });
        lstSongs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                playSong(newValue);
            }
        });
        playButton.setOnAction(this::handlePlay);
        pauseButton.setOnAction(this::handlePause);
        stopButton.setOnAction(this::handleStop);
    }

    public void play(String filePath) {
        Media media = new Media("file///"+filePath);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(this::onSongEnd);
        mediaPlayer.play();
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private void onSongEnd() {
        System.out.println("Song Ended");
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    private void handlePlay(ActionEvent event) {
        Song selectedSong = lstSongs.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            // Simulate playing the song
            playingSongName.setText("Now Playing: " + selectedSong.getTitle());
            System.out.println("Playing: " + selectedSong.getTitle()); // OR playSong(selectedSong);
        } else {
            playingSongName.setText("No song selected!");
        }
    }

    private void handlePause(ActionEvent event) {
        mediaPlayer.pause();
    }

    private void handleStop(ActionEvent event) {
        mediaPlayer.stop();
    }

    private void playSong(Song song) {
        mediaPlayer.play();
    }
}
