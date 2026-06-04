package com.berbin.weatherapp.controllers;

import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Controller responsible for handling user search input.
 *
 * This component allows users to enter a city name and submit
 * a weather search request. The controller itself does not
 * perform any weather lookup; instead, it delegates search
 * requests through a callback registered by a parent controller.
 *
 * This design keeps the component reusable and independent
 * from the service layer.
 */
public class SearchBarController {

    /** Text field used to enter a city name */
    @FXML private TextField searchField;

    /** Button used to trigger a search */
    @FXML private Button searchButton;

    /** Callback invoked when a search is submitted */
    private Consumer<String> onSearch;

    /**
     * Initializes the component after FXML loading.
     *
     * Registers event handlers so that searches can be submitted
     * either by pressing the search button or pressing Enter
     * while focused on the text field.
     */
    @FXML
    private void initialize() {

        searchButton.setOnAction(event -> submitSearch());
        searchField.setOnAction(event -> submitSearch());
    }

    /**
     * Registers a callback that will receive search queries.
     *
     * The callback is typically provided by a parent controller,
     * such as MainController, which is responsible for executing
     * the actual weather lookup.
     *
     * @param onSearch handler invoked when a search is submitted
     */
    public void setOnSearch(Consumer<String> onSearch) {
        this.onSearch = onSearch;
    }

    /**
     * Validates and dispatches the current search query.
     *
     * Empty or blank values are ignored. Valid queries are trimmed
     * and forwarded to the registered callback.
     */
    private void submitSearch() {

        String query = searchField.getText();
        searchField.setText("");

        if (query == null || query.isBlank()) {
            return;
        }

        if (onSearch != null) {
            onSearch.accept(query.trim());
        }
    }
}