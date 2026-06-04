package com.berbin.weatherapp.controllers;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

import com.berbin.weatherapp.models.WeatherReport;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

/**
 * Controller responsible for rendering recent search history.
 *
 * This component displays previously searched locations as a
 * horizontally scrollable list of history items.
 */
public class HistoryBarController {

    @FXML private Label noDataLabel;
    @FXML private VBox contentPane;
    @FXML private FlowPane historyContainer;

    private Consumer<String> onSearch;

    public void setOnSearch(Consumer<String> onSearch) {
        this.onSearch = onSearch;
    }

    @FXML
    private void initialize() {
        render(null);
    }
    
    /**
     * Renders the current search history.
     *
     * @param history cached search history
     */
    public void render(Map<String, WeatherReport> history) {

        historyContainer.getChildren().clear();

        if (history == null || history.isEmpty()) {
            noDataLabel.setVisible(true);
            contentPane.setVisible(false);
            return;
        }

        noDataLabel.setVisible(false);
        contentPane.setVisible(true);

        for (Map.Entry<String, WeatherReport> entry : history.entrySet()) {

            String key = entry.getKey();
            WeatherReport report = entry.getValue();

            historyContainer.getChildren().add(createItem(key, report));
        }
    }

    /**
     * Creates a history item node populated with report data.
     *
     * @param report weather report to display
     * @return populated history item
     */
    private Node createItem(String key, WeatherReport report) {

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/berbin/weatherapp/fxml/components/history-item.fxml")
            );

            Node item = loader.load();

            HistoryItemController controller = loader.getController();
            controller.setData(key, report);
            controller.setOnSearch(onSearch);

            return item;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load history item.", e);
        }
    }
}