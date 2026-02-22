package org.example.template;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * JavaFX controller template wired to a simple CSV loader.
 * Adjust column mappings and summary logic to match your domain.
 */
public class TemplateController {
    @FXML
    private TableView<DataRecord> table;
    @FXML
    private TableColumn<DataRecord, String> primaryCol;
    @FXML
    private TableColumn<DataRecord, String> secondaryCol;
    @FXML
    private TableColumn<DataRecord, Integer> metricCol;
    @FXML
    private TableColumn<DataRecord, Integer> spanDaysCol;

    @FXML
    private Label totalLabel;
    @FXML
    private Label maxSpanLabel;
    @FXML
    private TextField filterField;
    @FXML
    private Button openButton;

    private final ObservableList<DataRecord> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        primaryCol.setCellValueFactory(new PropertyValueFactory<>("primary"));
        secondaryCol.setCellValueFactory(new PropertyValueFactory<>("secondary"));
        metricCol.setCellValueFactory(new PropertyValueFactory<>("metric"));
        spanDaysCol.setCellValueFactory(new PropertyValueFactory<>("spanDays"));
        table.setItems(data);
        refreshSummary();
    }

    @FXML
    protected void handleOpen(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open CSV");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File file = chooser.showOpenDialog(openButton.getScene().getWindow());
        if (file == null) {
            return;
        }
        try (FileReader reader = new FileReader(file)) {
            CsvReader csvReader = new CsvReader();
            List<DataRecord> loaded = csvReader.load(reader);
            data.setAll(loaded);
            applyFilter();
        } catch (IOException ex) {
            // In a real app, surface this in the UI.
            ex.printStackTrace();
        }
    }

    @FXML
    protected void handleFilter(ActionEvent event) {
        applyFilter();
    }

    private void applyFilter() {
        String term = filterField.getText();
        if (term == null || term.isBlank()) {
            table.setItems(data);
        } else {
            String lowered = term.toLowerCase();
            ObservableList<DataRecord> filtered = data.filtered(r -> r.getPrimary().toLowerCase().contains(lowered) ||
                    r.getSecondary().toLowerCase().contains(lowered));
            table.setItems(filtered);
        }
        refreshSummary();
    }

    private void refreshSummary() {
        totalLabel.setText("Total: " + table.getItems().size());
        Optional<DataRecord> maxSpan = table.getItems().stream().max(Comparator.comparingInt(DataRecord::getSpanDays));
        maxSpanLabel.setText(maxSpan.map(r -> "Max span: " + r.getSpanDays() + " days").orElse("Max span: n/a"));
    }
}
