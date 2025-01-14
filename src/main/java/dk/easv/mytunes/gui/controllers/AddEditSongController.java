package dk.easv.mytunes.gui.controllers;

import dk.easv.mytunes.be.Artist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.ArtistDAO;
import dk.easv.mytunes.dal.SongDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class AddEditSongController {
    @FXML private TextField titleField, artistField, genreField, durationField;
    @FXML private Button selectFileButton, saveButton, cancelButton;
    @FXML private ComboBox<Artist> artistComboBox;

    private ArtistDAO artistDAO = new ArtistDAO();

    private SongDAO songDAO;
    private Song songToEdit;
    private String filePath;

    public AddEditSongController() {
        this.songDAO = new SongDAO();
    }

    // Method to initialize the dialog
    public void initialize(Song songToEdit) {
        List<Artist> artists = artistDAO.getArtists(); // Fetch the list of artists from the database
        artistComboBox.setItems(FXCollections.observableList(artists)); // Populate the ComboBox with the existing artists
        artistComboBox.setCellFactory(param -> new ListCell<Artist>() {
            @Override
            protected void updateItem(Artist artist, boolean empty) {
                super.updateItem(artist, empty);
                if (artist != null) {
                    setText(artist.getName());
                } else {
                    setText(null);
                }
            }
        });

        // Handle default selection if editing an existing song
        if (songToEdit != null) {
            titleField.setText(songToEdit.getTitle());
            Artist selectedArtist = artists.stream()
                    .filter(artist -> artist.getName().equals(songToEdit.getArtist_name()))
                    .findFirst()
                    .orElse(null);
            artistComboBox.setValue(selectedArtist);  // Set the artist combo box value to the selected artist
            genreField.setText(songToEdit.getCategory());
            durationField.setText(String.valueOf(songToEdit.getDuration()));
            filePath = songToEdit.getFile_path();
        }
    }

    @FXML
    private void chooseFile() {
        // Use FileChooser to select an audio file
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav"));
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            // Set the file path in the song object
            filePath = file.getName(); // Store the file name, without directory path
        }
    }

    @FXML
    private void saveSong() {
        String title = titleField.getText();
        String artist = artistField.getText();  // The user can type the artist here
        String genre = genreField.getText();
        int duration = Integer.parseInt(durationField.getText());

        if (title.isEmpty() || (artist.isEmpty() && artistComboBox.getSelectionModel().isEmpty()) || genre.isEmpty() || duration <= 0 || filePath == null) {
            showAlert("Invalid Input", "Please fill in all fields and select an audio file.");
            return;
        }

        if (artist.isEmpty() && artistComboBox.getSelectionModel().getSelectedItem() == null) {
            showAlert("Invalid Artist", "Please either select or enter a valid artist.");
            return;
        }

        // If the artist field is empty but combo box has a selected artist, use the combo box value
        Artist selectedArtist = artistComboBox.getSelectionModel().getSelectedItem();
        if (selectedArtist == null && !artist.isEmpty()) {
            // Artist is not in the combo box, so add it
            artistDAO.addArtist(new Artist(0, artist)); // Add new artist
            selectedArtist = new Artist(0, artist);  // Create a new Artist object with the entered name
        }

        Song newSong = new Song(songToEdit != null ? songToEdit.getId() : 0, title, selectedArtist.getName(), genre, duration, filePath);

        try {
            if (songToEdit != null) {
                songDAO.updateSong(newSong);
            } else {
                songDAO.addSong(newSong);
            }
            closeDialog();
        } catch (Exception e) {
            showAlert("Error", "Failed to save song.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
