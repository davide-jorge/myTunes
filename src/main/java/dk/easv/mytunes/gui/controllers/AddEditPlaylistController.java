package dk.easv.mytunes.gui.controllers;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.dal.PlaylistDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class AddEditPlaylistController {

    @FXML
    private TextField playlistNameField;
    @FXML
    private TextField playlistDurationField;

    private PlaylistDAO playlistDAO = new PlaylistDAO();
    private Playlist playlistToEdit;

    public void initialize(Playlist playlistToEdit) {
        this.playlistToEdit = playlistToEdit;
        if (playlistToEdit != null) {
            playlistNameField.setText(playlistToEdit.getName());
            playlistDurationField.setText(String.valueOf(playlistToEdit.getDuration()));
        }
    }

    @FXML
    private void handleSave() {
        String name = playlistNameField.getText();
        String durationText = playlistDurationField.getText();

        if (name.isEmpty() || durationText.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        try {
            int duration = Integer.parseInt(durationText);
            Playlist playlist = new Playlist(playlistToEdit != null ? playlistToEdit.getId() : 0, name, duration);

            if (playlistToEdit == null) {
                playlistDAO.addPlaylist(playlist); // Add new playlist
            } else {
                playlistDAO.updatePlaylist(playlist); // Update existing playlist
            }

            closeWindow();
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for duration.");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) playlistNameField.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}
