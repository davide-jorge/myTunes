package dk.easv.mytunes.gui.controllers;

import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.PlaylistSongs;
import dk.easv.mytunes.dal.PlaylistDAO;
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
import java.util.stream.Collectors;

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
    private TextField txtFilter;

    @FXML
    private ListView<Song> lstSongs;

    @FXML
    private ListView<Playlist> lstPlaylists;

    @FXML
    private ListView<PlaylistSongs> lstPlaylistSongs;

    @FXML
    private Button btnAddSong, btnEditSong, btnDeleteSong;

    @FXML
    private Button btnNewPlaylist, btnEditPlaylist, btnDeletePlaylist;

    @FXML
    private Button btnAddToPlaylist, btnDeleteFromPlaylist;

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
    private PlaylistDAO playlistDAO = new PlaylistDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load songs and playlists
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
                    if (selectedSong != null) {
                        currentSong = selectedSong; // Update the current song
                        loadMedia();
                        playRemainingSongs();  // Automatically play subsequent songs in the playlist
                    }
                }
            }
        });

        // Initialize Filter functionality
        txtFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            filterSongs(newValue);
        });

        // Media Player button actions
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

        // Add, Edit or Delete Song button actions
        btnAddSong.setOnAction(event -> openAddEditSongs(null));
        btnEditSong.setOnAction(event -> {
            Song selectedSong = lstSongs.getSelectionModel().getSelectedItem();
            if (selectedSong != null) {
                openAddEditSongs(selectedSong);
            }
        });
        btnDeleteSong.setOnAction(event -> deleteSong());

        // Add, Edit or Delete Playlist button actions
        btnNewPlaylist.setOnAction(event -> openAddEditPlaylist(null)); // New Playlist
        btnEditPlaylist.setOnAction(event -> {
            Playlist selectedPlaylist = lstPlaylists.getSelectionModel().getSelectedItem();
            if (selectedPlaylist != null) {
                openAddEditPlaylist(selectedPlaylist); // Edit Playlist
            }
        });
        btnDeletePlaylist.setOnAction(event -> deletePlaylist()); // Delete Playlist

        // Add or Delete Song from Playlist button actions
        // btnAddToPlaylist.setOnAction(event -> addSongToPlaylist());
    }

    private void filterSongs(String filterText) {
        // If the filter text is empty, show all songs
        if (filterText == null || filterText.isEmpty()) {
            lstSongs.setItems(FXCollections.observableList(songs));
        } else {
            // Filter the songs based on the title
            List<Song> filteredSongs = songs.stream()
                    .filter(song -> song.getTitle().toLowerCase().contains(filterText.toLowerCase()))
                    .collect(Collectors.toList());

            // Update the ListView with the filtered list
            lstSongs.setItems(FXCollections.observableList(filteredSongs));
        }
    }

    private void openAddEditSongs(Song songToEdit) {
        // Open the dialog to Add or Edit a song
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/mytunes/views/add-edit-songs.fxml"));
            Parent root = loader.load();
            AddEditSongController dialogController = loader.getController();
            if (songToEdit != null) {
                dialogController.initialize(songToEdit); // Pass the existing song details if editing
            }
            Stage stage = new Stage();
            stage.setTitle(songToEdit == null ? "Add Song" : "Edit Song");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            // After the dialog is closed, refresh the ListView to reflect changes
            refreshSongList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshSongList() {
        songs.clear();
        songs.addAll(songDAO.getSongs());  // Reload the songs from the database
        lstSongs.setItems(FXCollections.observableList(songs));
    }

    private void addSong(Song song) {
        try {
            songDAO.addSong(song); // Add song to the database
            songs.add(song); // Add song to the ListView
            lstSongs.getItems().add(song); // Update ListView
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error adding song", "There was an issue adding the song.");
        }
    }

    private void editSong(Song editedSong) {
        try {
            songDAO.updateSong(editedSong); // Update the song in the database
            lstSongs.getItems().set(lstSongs.getSelectionModel().getSelectedIndex(), editedSong); // Update the ListView
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error updating song", "There was an issue updating the song.");
        }
    }

    private void deleteSong() {
        Song selectedSong = lstSongs.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Song");
            alert.setHeaderText("Are you sure you want to delete this song?");
            alert.setContentText(selectedSong.getTitle());
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        songDAO.deleteSong(selectedSong); // Delete the song from the database
                        songs.remove(selectedSong); // Remove the song from the ObservableList and ListView
                        lstSongs.getItems().remove(selectedSong);
                        lstSongs.setItems(FXCollections.observableList(songs)); // Refresh the entire ListView
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showError("Error deleting song", "There was an issue deleting the selected song.");
                    }
                }
            });
        } else {
            showError("No song selected", "Please select a song to delete.");
        }
    }

    private void openAddEditPlaylist(Playlist playlistToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/mytunes/views/add-edit-playlists.fxml"));
            Parent root = loader.load();
            AddEditPlaylistController dialogController = loader.getController();
            if (playlistToEdit != null) {
                dialogController.initialize(playlistToEdit); // Pass existing playlist details if editing
            }
            Stage stage = new Stage();
            stage.setTitle(playlistToEdit == null ? "New Playlist" : "Edit Playlist");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshPlaylistList();  // Refresh playlist list after dialog is closed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshPlaylistList() {
        playlists.clear();
        playlists.addAll(playlistDAO.getPlaylists());  // Reload playlists from the database
        lstPlaylists.setItems(FXCollections.observableList(playlists));
    }

    private void addPlaylist(Playlist playlist) {
        try {
            // Save the playlist to the database
            playlistDAO.addPlaylist(playlist);

            // Add the new playlist to the ObservableList and ListView
            playlists.add(playlist);
            lstPlaylists.setItems(FXCollections.observableList(playlists)); // Update ListView

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error adding playlist", "There was an issue adding the playlist.");
        }
    }

    private void deletePlaylist() {
        Playlist selectedPlaylist = lstPlaylists.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Playlist");
            alert.setHeaderText("Are you sure you want to delete this playlist?");
            alert.setContentText(selectedPlaylist.getName());
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        playlistDAO.deletePlaylist(selectedPlaylist);
                        playlists.remove(selectedPlaylist);
                        lstPlaylists.getItems().remove(selectedPlaylist);
                        lstPlaylists.setItems(FXCollections.observableList(playlists));
                    } catch (Exception e) { // Catch a general exception
                        e.printStackTrace();
                        showError("Error deleting playlist", "There was an issue deleting the playlist.");
                    }
                }
            });
        } else {
            showError("No playlist selected", "Please select a playlist to delete.");
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
