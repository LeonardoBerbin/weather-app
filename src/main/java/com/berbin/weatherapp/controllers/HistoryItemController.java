package com.berbin.weatherapp.controllers;

import java.util.function.Consumer;

import com.berbin.weatherapp.models.WeatherReport;
import com.berbin.weatherapp.utils.IconResolverUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller responsible for rendering a single history item.
 *
 * Each item represents a previously searched location and
 * can be clicked to perform the search again.
 */
public class HistoryItemController {

    @FXML private Button historyButton;
    @FXML private ImageView flagView;

    private String key; // Original cache key used to repeat the same search query.
    private Consumer<String> onSearch;

    @FXML
    private void initialize() {

        historyButton.setOnAction(event -> {
            if (onSearch != null) {
                onSearch.accept(key);
            }
        });
    }

    /**
     * Populates the history item with report data.
     *
     * @param report weather report to display
     */
    public void setData(String key, WeatherReport report) {

        historyButton.setText(report.city());
        setCountryFlag(report.countryCode());
        
        this.key = key;
    }

    /**
     * Registers the callback invoked when the item is clicked.
     *
     * @param onSearch search handler
     */
    public void setOnSearch(Consumer<String> onSearch) {
        this.onSearch = onSearch;
    }

    /**
     * Displays the country flag when available.
     *
     * @param iso2 ISO country code
     */
    private void setCountryFlag(String iso2) {

        Image flag = IconResolverUtil.getFlag(iso2);

        if (flag == null) {
            flagView.setVisible(false);
            return;
        }

        flagView.setImage(flag);
    }
}