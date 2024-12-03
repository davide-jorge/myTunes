package dk.easv.mytunes.gui.controllers;

import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.DBConnection;
import dk.easv.mytunes.gui.models.SongModel;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import java.net.URL;
import java.util.ResourceBundle;

public class myTunesController implements Initializable {

    private MediaPlayer mediaPlayer;

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

    @FXML
    private ListView<Song> lstSongs;

    private SongModel songModel = new SongModel();

    @FXML
    private Button playButton;

    @FXML
    private Button pauseButton;

    @FXML
    private Button stopButton;

    @FXML
    public void initialize() {
        lstSongs.setItems(songModel.getObservableSongList());
        lstSongs.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                playSong(newSelection);
            }
        });
        playButton.setOnAction(this::handlePlay);
        pauseButton.setOnAction(this::handlePause);
        stopButton.setOnAction(this::handleStop);
    }

    /*
    @FXML
    void onLoadSongsClick(ActionEvent event) {lstSongs.setItems(songModel.getObservableSongList());}
    */

    private void handlePlay(ActionEvent event) {
        Song selectedSong = lstSongs.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            playSong(selectedSong);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
