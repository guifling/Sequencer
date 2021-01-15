/*
 * Developed by Guilherme F. Schling.
 * Last time updated: 14/01/2021 23:19.
 * Copyright (c) 2021.
 */

package com.sequencer;

import com.sequencer.model.Planner;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

public class Controller {
    private Planner planner;
    private boolean plannerIsRunning = false;
    @FXML
    private ProgressBar progress;
    @FXML
    private BorderPane mainPane;
    @FXML
    private TextField lastRackTwField, lastRack5kField, lastRackTw6And7kField, lastRack8kField;
    @FXML
    private Spinner<Integer> ratio5kField, ratio6And7kField, ratio8kField;
    @FXML
    private CheckBox doNotLoad5k, doNotLoad6And7k, doNotLoad8k, combineMachines;
    @FXML
    private ImageView successfulImage;
    @FXML
    private Button dropButton;
    private File chosenFile;

    public void initialize() {
        planner = new Planner();

        dropButton.setOnDragOver(event -> event.acceptTransferModes(TransferMode.LINK));
        dropButton.setOnDragDropped(event -> {
            List<File> files = event.getDragboard().getFiles();
            validateFile(files.get(0));
        });
        dropButton.setCursor(Cursor.HAND);
    }

    @FXML
    private void load() {
        if (chosenFile == null) {
            if (!chooseFile()) {
                return;
            }
        }
        Alert readErrorAlert = new Alert(Alert.AlertType.ERROR);
        readErrorAlert.setTitle("Erro");

        int ratio5k;
        int ratio6And7k;
        int ratio8k;
        String rackTw;
        try {
            ratio5k = Integer.parseInt(ratio5kField.getValue().toString());
        } catch (Exception e) {
            readErrorAlert.setHeaderText("É necessário informar uma proporção para a linha 5000 maior ou igual a 0!");
            readErrorAlert.show();
            return;
        }
        try {
            ratio6And7k = Integer.parseInt(ratio6And7kField.getValue().toString());
        } catch (Exception e) {
            readErrorAlert.setHeaderText("É necessário informar uma proporção para a linha 6000 e 7000 maior ou igual a 0!");
            readErrorAlert.show();
            return;
        }
        try {
            ratio8k = Integer.parseInt(ratio8kField.getValue().toString());
        } catch (Exception e) {
            readErrorAlert.setHeaderText("É necessário informar uma proporção para a linha 8000 maior ou igual a 0!");
            readErrorAlert.show();
            return;
        }
        int lastRack5k = 0;
        int lastRack6And7k = 0;
        int lastRack8k = 0;
        try {
            lastRack5k = Integer.parseInt(lastRack5kField.getText());
        } catch (Exception e) {

        }
        try {
            lastRack6And7k = Integer.parseInt(lastRackTw6And7kField.getText());
        } catch (Exception e) {

        }
        try {
            lastRack8k = Integer.parseInt(lastRack8kField.getText());
        } catch (Exception e) {

        }

        rackTw = lastRackTwField.getText();

        if (!plannerIsRunning) {
            plannerIsRunning = true;
            planner = new Planner(Paths.get(chosenFile.toURI()), ratio5k, ratio6And7k, ratio8k, rackTw, lastRack5k, lastRack6And7k, lastRack8k, combineMachines.isSelected());

            planner.setDoNotLoad5k(doNotLoad5k.isSelected());
            planner.setDoNotLoad6And7k(doNotLoad6And7k.isSelected());
            planner.setDoNotLoad8k(doNotLoad8k.isSelected());
            planner.setOnRunning(event -> progress.setVisible(true));

            planner.setOnSucceeded(event -> {
                if (planner.getValue().size() == 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Planejamento não executado");
                    alert.setHeaderText("Nenhum rack foi planejado!");
                    alert.show();
                    successfulImage(false);
                } else {
                    Alert successfulAlert = new Alert(Alert.AlertType.INFORMATION);
                    successfulAlert.setTitle("Planejamento Criado");
                    successfulAlert.setHeaderText("Planejamento criado com sucesso");
                    successfulAlert.show();
                    successfulImage(true);
                }
                plannerIsRunning = false;
                progress.setVisible(false);
                successfulImage.setVisible(true);
                chosenFile = null;
                dropButton.setText("Arraste aqui ou clique para procurar outro arquivo...");
                dropButton.setTextFill(Paint.valueOf("#000000"));
                dropButton.setStyle("-fx-border-style: solid; -fx-border-color: #838383; -fx-border-width: 2px; -fx-background-color: whitesmoke");
            });
            progress.progressProperty().bind(planner.progressProperty());
            planner.start();

        }
    }

    @FXML
    private boolean chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Planilha \".xlsx\"", "*.xlsx"));
        fileChooser.setTitle("Selecionar o arquivo com os pedidos de produção");
        File chosenFile = fileChooser.showOpenDialog(mainPane.getScene().getWindow());

        return validateFile(chosenFile);
    }

    private boolean validateFile(File file) {
        if (file != null) {
            if (!planner.isValidFile(Paths.get(file.toURI()))) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Arquivo Inválido");
                alert.setHeaderText("O arquivo selecionado não é válido.\n\nÉ necessário selecionar um arquivo\ninalterado extraído do sistema do\ncliente " +
                        "em inglês ou em português!");
                alert.show();
                return false;
            }

            this.chosenFile = file;
            dropButton.setText(chosenFile.getName().toLowerCase() + " carregado!");
            dropButton.setTextFill(Paint.valueOf("#FFFFFF"));
            dropButton.setStyle("-fx-background-color: #5eb884");
            return true;
        }
        return false;
    }

    @FXML
    private void successfulImage(boolean isSuccessful) {
        if (isSuccessful) {
            successfulImage.setImage(new Image(getClass().getResourceAsStream("/com/sequencer/images/successful.png")));
        } else {
            successfulImage.setImage(new Image(getClass().getResourceAsStream("/com/sequencer/images/notSuccessful.png")));
        }
    }
}