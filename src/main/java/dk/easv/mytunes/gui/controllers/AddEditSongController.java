package dk.easv.mytunes.gui.controllers;

import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.dal.SongDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class AddEditSongController {
    @FXML private TextField titleField, artistField, genreField;;
    @FXML private Button saveButton, cancelButton;

    private SongDAO songDAO = new SongDAO();
    private Song songToEdit;

    // Method to initialize the dialog (used for editing as well)
    public void initialize(Song song) {
        if (song != null) {
            // If editing, populate the fields with the song data
            songToEdit = song;
            titleField.setText(song.getTitle());
            artistField.setText(song.getArtist_name());
            genreField.setText(song.getCategory());
        }
    }

    @FXML
    private void chooseFile() {
        // Ensure songToEdit is not null, if adding a new song, create a new Song object
        if (songToEdit == null) {
            songToEdit = new Song(0, "", "", "", 0, ""); // Create a new song with default values
        }

        // Use FileChooser to select an audio file
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav"));
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            // Set the file path in the song object
            songToEdit.setFile_path(file.getAbsolutePath()); // Use the full path, not just the file name
        }
    }

    @FXML
    private void saveSong() {
        // Validate input
        if (titleField.getText().isEmpty() || artistField.getText().isEmpty() || genreField.getText().isEmpty()) {
            showAlert("Error", "All fields must be filled.");
            return;
        }

        if (songToEdit == null) {
            songToEdit = new Song(0, titleField.getText(), artistField.getText(), genreField.getText(), 0, songToEdit.getFile_path());
            songDAO.addSong(songToEdit);  // Add the song to the database
        } else {
            songToEdit.setTitle(titleField.getText());
            songToEdit.setArtist_name(artistField.getText());
            songToEdit.setCategory(genreField.getText());
            songDAO.updateSong(songToEdit);  // Update the song in the database
        }
        closeDialog();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close(); // Close the dialog without saving changes
    }

    private void closeDialog() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
}
