package com.berbin.weatherapp.controllers;

import java.io.IOException;
import java.util.List;

import com.berbin.weatherapp.models.ForecastEntry;
import com.berbin.weatherapp.state.UnitSystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller responsible for rendering forecast data.
 *
 * This controller manages the forecast list component,
 * including empty state handling and dynamic creation
 * of forecast cards.
 */
public class ForecastController {

    /** Label displayed when no forecast data is available */
    @FXML private Label noDataLabel;

    /** Root content container */
    @FXML private VBox contentPane;

    /** Container that holds forecast cards */
    @FXML private VBox forecastContainer;

    /**
     * Initializes the component in its empty state.
     */
    @FXML
    private void initialize() {
        render(null, null);
    }

    /**
     * Renders forecast entries into the UI.
     *
     * When the list is null or empty, an empty state message
     * is displayed instead of the forecast content.
     *
     * @param forecastList forecast entries to display
     * @param unit preferred unit system
     */
    public void render(List<ForecastEntry> forecastList, UnitSystem unit) {

        forecastContainer.getChildren().clear();

        if (forecastList == null || forecastList.isEmpty()) {
            noDataLabel.setVisible(true);
            contentPane.setVisible(false);
            return;
        }

        noDataLabel.setVisible(false);
        contentPane.setVisible(true);

        for (ForecastEntry item : forecastList) {
            forecastContainer.getChildren().add(createCard(item, unit));
        }
    }

    /**
     * Creates a forecast card node and populates it with data.
     *
     * @param item forecast entry to render
     * @param unit preferred unit system
     * @return populated forecast card node
     */
    private Node createCard(ForecastEntry item, UnitSystem unit) {

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/berbin/weatherapp/fxml/components/forecast-card.fxml")
            );

            Node card = loader.load();
            ForecastCardController controller = loader.getController();
            controller.setData(item, unit);

            return card;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load forecast card.", e);
        }
    }
}