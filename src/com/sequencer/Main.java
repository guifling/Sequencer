/*
 * Developed by Guilherme F. Schling.
 * Last time updated: 15/08/2020 21:55.
 * Copyright (c) 2020.
 */

package com.sequencer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    private double xOffset = 0;
    private double yOffset = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));

        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX() + 8;
            yOffset = event.getSceneY() + 31;
        });

        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });


        primaryStage.setTitle("Sequencer");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/sequencer/images/logoTw163x163.png")));
        primaryStage.setScene(new Scene(root, 800, 450));
        primaryStage.show();
    }
}
