package dk.easv.mytunes.gui.controllers;

import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.PlaylistSongs;
import dk.easv.mytunes.dal.SongDAO;
import dk.easv.mytunes.dal.PlaylistSongsDAO;
import dk.easv.mytunes.gui.models.SongModel;
import dk.easv.mytunes.gui.models.ArtistModel;
import dk.easv.mytunes.gui.models.PlaylistModel;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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

    @FXML
    private ListView<PlaylistSongs> lstPlaylistSongs;

    @FXML
    private Button btnAddSong, btnEditSong, btnDeleteSong;

    private MediaPlayer mediaPlayer;
    private List<Song> songs;
    private List<Playlist> playlists;
    private Song currentSong;
    private Playlist currentPlaylist;

    private SongModel songModel = new SongModel();
    private ArtistModel artistModel = new ArtistModel();
    private PlaylistModel playlistModel = new PlaylistModel();
    private PlaylistSongsDAO playlistSongsDAO = new PlaylistSongsDAO();
    private SongDAO songDAO = new SongDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load Songs and Playlists
        songs = songModel.getSongs();
        playlists = playlistModel.getPlaylists();
        lstSongs.getItems().addAll(songs);
        lstPlaylists.getItems().addAll(playlists);

        // Listener for Song selection in lstSongs
        lstSongs.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Song>() {
            @Override
            public void changed(ObservableValue<? extends Song> observable, Song oldValue, Song newValue) {
                currentSong = newValue;
                loadMedia();
            }
        });

        // Listener for Playlist selection in lstPlaylists
        lstPlaylists.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Playlist>() {
            @Override
            public void changed(ObservableValue<? extends Playlist> observable, Playlist oldValue, Playlist newValue) {
                if (newValue != null) {
                    currentPlaylist = newValue;
                    loadPlaylistSongs(newValue.getId());
                }
            }
        });

        // Listener for Song selection in lstPlaylistSongs
        lstPlaylistSongs.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PlaylistSongs>() {
            @Override
            public void changed(ObservableValue<? extends PlaylistSongs> observable, PlaylistSongs oldValue, PlaylistSongs newValue) {
                if (newValue != null) {
                    // Find the corresponding Song object for the selected PlaylistSong
                    Song selectedSong = findSongByTitle(newValue.getTitle());
                    ;
                    if (selectedSong != null) {
                        currentSong = selectedSong; // Update the current song
                        loadMedia();
                        playRemainingSongs();  // Automatically play subsequent songs in the playlist
                    }
                }
            }
        });

        playButton.setOnAction(event -> playMedia());
        pauseButton.setOnAction(event -> pauseMedia());
        stopButton.setOnAction(event -> stopMedia());
        skipForwardsButton.setOnAction(event -> skipForward());
        skipBackwardsButton.setOnAction(event -> skipBackward());
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            }
        });

        // Add button actions
        btnAddSong.setOnAction(event -> openAddEditSongs(null));
        btnEditSong.setOnAction(event -> {
            Song selectedSong = lstSongs.getSelectionModel().getSelectedItem();
            if (selectedSong != null) {
                openAddEditSongs(selectedSong);
            }
        });
        btnDeleteSong.setOnAction(event -> deleteSong());
    }

    private void openAddEditSongs(Song songToEdit) {
        // Open the dialog to add or edit a song
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/mytunes/views/add-edit-songs.fxml"));
            Parent root = loader.load();
            AddEditSongController dialogController = loader.getController();
            if (songToEdit != null) {
                dialogController.initialize(songToEdit); // Pass existing song details for editing
            }
            Stage stage = new Stage();
            stage.setTitle(songToEdit == null ? "Add Song" : "Edit Song");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteSong() {
        Song selectedSong = lstSongs.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            try {
                songDAO.deleteSong(selectedSong); // Call the deleteSong method from SongDAO
                songs.remove(selectedSong); // Remove the song from the ObservableList
                lstSongs.getItems().remove(selectedSong); // Update ListView
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Error deleting song", "There was an issue deleting the selected song.");
            }
        } else {
            showError("No song selected", "Please select a song to delete.");
        }
    }

    private void showError(String title, String message) {
        // Display an error dialog or alert
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Load the songs for the selected playlist
    private void loadPlaylistSongs(int playlistId) {
        try {
            // Retrieve PlaylistSongs for the selected playlist
            List<PlaylistSongs> playlistSongs = playlistSongsDAO.getSongsByPlaylistId(playlistId);
            lstPlaylistSongs.getItems().setAll(playlistSongs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Find the Song object by its title when selecting a song from lstPlaylistSongs
    private Song findSongByTitle(String title) {
        for (Song song : songs) {
            if (song.getTitle().equals(title)) {
                return song;
            }
        }
        return null; // Return null if no song with the given title is found
    }

    // Load the media player with the selected song
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
            playButton.setDisable(false); // Enable the Play button when paused
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

    private void playRemainingSongs() {
        // Get the index of the currently selected song in the playlist
        int startIndex = lstPlaylistSongs.getSelectionModel().getSelectedIndex();
        if (startIndex != -1) {
            // Convert the subList to ObservableList
            ObservableList<PlaylistSongs> remainingSongs = FXCollections.observableArrayList(lstPlaylistSongs.getItems().subList(startIndex, lstPlaylistSongs.getItems().size()));
            playNextSongInPlaylist(remainingSongs);
        }
    }

    private void playNextSongInPlaylist(ObservableList<PlaylistSongs> remainingSongs) {
        mediaPlayer.setOnEndOfMedia(() -> {
            // Find the index of the current song by matching the title
            int currentIndex = -1;
            for (int i = 0; i < remainingSongs.size(); i++) {
                if (remainingSongs.get(i).getTitle().equals(currentSong.getTitle())) {
                    currentIndex = i;
                    break;
                }
            }

            // If there is a next song, play it
            if (currentIndex != -1 && currentIndex < remainingSongs.size() - 1) {
                Song nextSong = findSongByTitle(remainingSongs.get(currentIndex + 1).getTitle());
                if (nextSong != null) {
                    currentSong = nextSong;
                    loadMedia();
                    playMedia();
                    playNextSongInPlaylist(remainingSongs);  // Recursively call to play next song
                }
            }
        });
    }
}
