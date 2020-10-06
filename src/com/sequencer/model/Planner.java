/*
 * Developed by Guilherme F. Schling.
 * Last time updated: 23/09/2020 22:10.
 * Copyright (c) 2020.
 */

package com.sequencer.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Planner extends Service<ObservableList<Rack>> {
    private final int ratio5k;
    private final int ratio6And7k;
    private final int ratio8k;
    private final String lastRackTw;
    private final Path filePath;
    private XSSFWorkbook loadedWorkbook;
    private XSSFWorkbook savedWorkBook;
    private ObservableList<Rack> productionPlaning = FXCollections.observableArrayList();
    private boolean doNotLoad5k;
    private boolean doNotLoad6And7k;
    private boolean doNotLoad8k;

    public Planner() {
        this(null, 0, 0, 0, null);
    }

    public Planner(Path filePath, int ratio5k, int ratio6And7k, int ratio8k, String lastRackTw) {
        this.ratio5k = ratio5k;
        this.ratio6And7k = ratio6And7k;
        this.ratio8k = ratio8k;
        this.lastRackTw = lastRackTw;
        this.filePath = filePath;
        if (filePath != null) {
            try {
                this.loadedWorkbook = new XSSFWorkbook(new FileInputStream(new File(filePath.toUri().getPath())));
                this.savedWorkBook = new XSSFWorkbook();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected Task<ObservableList<Rack>> createTask() {
        return new Task<ObservableList<Rack>>() {
            @Override
            protected ObservableList<Rack> call() {
                int maxProgress = 5;
                updateProgress(0, maxProgress);

                List<ManufacturingOrder> loadedOrders = loadOrders();
                updateProgress(1, maxProgress);

                if (loadedOrders != null) {
                    List<Rack> createdRacks = createRacks(loadedOrders);
                    updateProgress(2, maxProgress);
                    ObservableList<Rack> plannedRacks = productionPlaning(createdRacks);
                    updateProgress(3, maxProgress);
                    productionPlaning = plannedRacks;

                    addTwIdentificationToRacks(productionPlaning);
                    updateProgress(4, maxProgress);

                    writeProductionPlanningFile();
                    updateProgress(5, maxProgress);
                }
                return productionPlaning;
            }
        };
    }

    /**
     * Loads the orders and do the production planning.
     */
    private List<ManufacturingOrder> loadOrders() {
        if (loadedWorkbook != null) {
            try {
                loadedWorkbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (ratio5k <= 0 && ratio6And7k <= 0) {
            System.out.println("Não é possível planejar a produção com as proporções de 5000, 6000 e 7000 igual a 0.");
            return null;
        }

        List<ManufacturingOrder> loadedOrders = new ArrayList<>();
        try {
            Sheet sheet = loadedWorkbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();


            if (!isValidFile()) {
                return null;
            }

            iterator.next();
            while (iterator.hasNext()) {
                Row row = iterator.next();
                int carNumber = Integer.parseInt(row.getCell(1).getStringCellValue());
                int rackNumber = Integer.parseInt(row.getCell(2).getStringCellValue());
                long orderNumber = Long.parseLong(row.getCell(3).getStringCellValue());
                String mn = row.getCell(5).getStringCellValue();
                int quantity = (int) row.getCell(6).getNumericCellValue();

                ManufacturingOrder manufacturingOrder = new ManufacturingOrder(carNumber, rackNumber, orderNumber, mn, quantity);

                // Do not load 148 (8R)
                if (ratio8k <= 0 || doNotLoad8k) {
                    if (manufacturingOrder.getLine() == 148) {
                        continue;
                    }
                }
                // Do not load if ratio5k is less then or equal to 0 (in this case the user does not want to include this order to the planning.
                if (ratio5k <= 0 || doNotLoad5k) {
                    if (manufacturingOrder.getLine() == 149) {
                        continue;
                    }
                }
                // Do not load if ratio6And7K is less then or equal to 0 (in this case the user does not want to include this order to the planning.
                if (ratio6And7k <= 0 || doNotLoad6And7k) {
                    if (manufacturingOrder.getLine() == 150) {
                        continue;
                    }
                }
                loadedOrders.add(manufacturingOrder);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("Não foi possível ler a primeira aba da planilha: " + e.getMessage());
        }

        return loadedOrders;
    }

    /**
     * Checks if the loaded file is valid.
     *
     * @return A boolean.
     */
    public boolean isValidFile() {
        Sheet sheet = loadedWorkbook.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();

        Row headerRow = iterator.next();
        String carNumber = headerRow.getCell(1).getStringCellValue();
        String rackNumber = headerRow.getCell(2).getStringCellValue();
        String orderNumber = headerRow.getCell(3).getStringCellValue();
        String mn = headerRow.getCell(5).getStringCellValue();
        String quantity = headerRow.getCell(6).getStringCellValue();

        return carNumber.equals("Número do Carro") && rackNumber.equals("Contador") && orderNumber.equals("Ordem") && mn.equals("Material Componente") && quantity.equals("Qtd do componente no carro");
    }

    /**
     * Checks if the loaded file is valid.
     *
     * @param filePath The path to the file.
     * @return A boolean.
     */
    public boolean isValidFile(Path filePath) {
        boolean result = false;
        try (XSSFWorkbook loadedWorkbook = new XSSFWorkbook(new FileInputStream(new File(filePath.toUri().getPath())))) {
            Sheet sheet = loadedWorkbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();

            try {
                Row headerRow = iterator.next();
                String carNumber = headerRow.getCell(1).getStringCellValue();
                String rackNumber = headerRow.getCell(2).getStringCellValue();
                String orderNumber = headerRow.getCell(3).getStringCellValue();
                String mn = headerRow.getCell(5).getStringCellValue();
                String quantity = headerRow.getCell(6).getStringCellValue();
                result = carNumber.equals("Número do Carro") && rackNumber.equals("Contador") && orderNumber.equals("Ordem") && mn.equals("Material " +
                        "Componente") && quantity.equals("Qtd do componente no carro");
            } catch (Exception e) {
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Creates racks from the manufacturing orders.
     *
     * @param orderList List of manufacturing order.
     * @return A list of racks.
     */
    private List<Rack> createRacks(List<ManufacturingOrder> orderList) {
        List<Rack> racks = new ArrayList<>();
        Rack newRack = new Rack();

        for (ManufacturingOrder order : orderList) {
            if (racks.isEmpty()) {
                newRack.setRackNumber(order.getRackNumber());
                newRack.setLine(order.getLine());
                newRack.addOrder(order);
                racks.add(newRack);
            } else if (order.getRackNumber() == newRack.getRackNumber()) {
                newRack.setRackNumber(order.getRackNumber());
                newRack.setLine(order.getLine());
                newRack.addOrder(order);
            } else {
                newRack = new Rack();
                newRack.setRackNumber(order.getRackNumber());
                newRack.setLine(order.getLine());
                newRack.addOrder(order);
                racks.add(newRack);
            }
        }
        return racks;
    }

    /**
     * Creates the production planning.
     *
     * @param sourceRackList The rack list to the planned.
     * @return List of planned racks.
     */
    private ObservableList<Rack> productionPlaning(List<Rack> sourceRackList) {
        int racksToPlan = sourceRackList.size();

        ObservableList<Rack> plannedList = FXCollections.observableArrayList();
        List<Rack> racksTobePlanned5k = new ArrayList<>();
        List<Rack> racksTobePlanned6And7k = new ArrayList<>();
        List<Rack> racksTobePlanned8k = new ArrayList<>();

        for (Rack rack : sourceRackList) {
            if (rack.getLine() == 149) {
                racksTobePlanned5k.add(rack);
            }
        }
        for (Rack rack : racksTobePlanned5k) {
            sourceRackList.remove(rack);
        }

        for (Rack rack : sourceRackList) {
            if (rack.getLine() == 148) {
                racksTobePlanned8k.add(rack);
            }
        }
        for (Rack rack : racksTobePlanned8k) {
            sourceRackList.remove(rack);
        }
        for (Rack rack : sourceRackList) {
            if (rack.getLine() == 150) {
                racksTobePlanned6And7k.add(rack);
            }
        }
        for (Rack rack : racksTobePlanned6And7k) {
            sourceRackList.remove(rack);
        }

        while (plannedList.size() != racksToPlan) {
            makeSequence(racksTobePlanned5k, plannedList, ratio5k);
            makeSequence(racksTobePlanned6And7k, plannedList, ratio6And7k);
            makeSequence(racksTobePlanned8k, plannedList, ratio8k);
        }
        return plannedList;
    }

    /**
     * Creates the sequence according to the user settings.
     *
     * @param racksTobePlanned Racks to be planned.
     * @param plannedRacks     Planned racks.
     * @param lineRatio        Line ratio. This makes possible to intercalate racks from different product lines. Ex: 1 rack (5k) and 2 racks (8k).
     */
    private void makeSequence(List<Rack> racksTobePlanned, List<Rack> plannedRacks, int lineRatio) {
        if (!racksTobePlanned.isEmpty()) {

            int getIndex;
            for (int i = 0; i < lineRatio; i++) {
                if (!racksTobePlanned.isEmpty()) {
                    getIndex = i;
                    if (i != 0) {
                        getIndex -= i;
                    }
                    Rack rack = racksTobePlanned.get(getIndex);
                    plannedRacks.add(rack);
                    racksTobePlanned.remove(rack);
                    continue;
                }
                break;
            }
        }

    }

    /**
     * Adds an internal identification to the racks.
     *
     * @param productionPlaning Racks
     */
    private void addTwIdentificationToRacks(List<Rack> productionPlaning) {
        if (!productionPlaning.isEmpty()) {
            try {
                if (!lastRackTw.isEmpty()) {
                    long lastRackTw = Long.parseLong(this.lastRackTw);
                    lastRackTw++;
                    for (Rack rack : productionPlaning) {
                        String newRackNumber = (lastRackTw++) + "-" + rack.getRackNumber();
                        rack.setPlannedRackNumber(newRackNumber);
                    }
                } else {
                    for (Rack rack : productionPlaning) {
                        String newRackNumber = String.valueOf(rack.getRackNumber());
                        rack.setPlannedRackNumber(newRackNumber);
                    }
                }
            } catch (Exception e) {
                String newRackNumber = "";
                if (!lastRackTw.trim().isEmpty()) {
                    System.out.println("Last rack tw " + lastRackTw);
                    newRackNumber = lastRackTw + "-";
                }
                for (Rack rack : productionPlaning) {
                    rack.setPlannedRackNumber(newRackNumber + rack.getRackNumber());
                }
            }
        }

    }

    /**
     * Writes the planning file.
     */
    private void writeProductionPlanningFile() {
        if (!productionPlaning.isEmpty()) {
            XSSFSheet sheet = savedWorkBook.createSheet("Plano de produção");
            int rowNumber = 0;
            int orderCellNumber = 0;
            int mnCellNumber = 1;
            int rackCellNumber = 2;

            Row header = sheet.createRow(rowNumber++);

            // Creating font style
            CellStyle headerStyle = savedWorkBook.createCellStyle();
            XSSFFont headerFont = savedWorkBook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            header.setRowStyle(savedWorkBook.createCellStyle());
            Cell orderNumberHeader = header.createCell(orderCellNumber);
            orderNumberHeader.setCellValue("Ordem de produção");
            orderNumberHeader.setCellStyle(headerStyle);

            Cell mnNumberHeader = header.createCell(mnCellNumber);
            mnNumberHeader.setCellValue("Modelo");
            mnNumberHeader.setCellStyle(headerStyle);

            Cell rackNumberHeader = header.createCell(rackCellNumber);
            rackNumberHeader.setCellValue("Rack");
            rackNumberHeader.setCellStyle(headerStyle);

            CellStyle cellStyle = savedWorkBook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);


            for (Rack rack : productionPlaning) {
                for (ManufacturingOrder order : rack.getOrders()) {
                    Row row = sheet.createRow(rowNumber++);

                    Cell orderCell = row.createCell(orderCellNumber);
                    orderCell.setCellValue(order.getOrderNumber());
                    orderCell.setCellStyle(cellStyle);

                    Cell mnCell = row.createCell(mnCellNumber);
                    mnCell.setCellValue(order.getMn());
                    mnCell.setCellStyle(cellStyle);

                    Cell rackCell = row.createCell(rackCellNumber);
                    rackCell.setCellValue(rack.getPlannedRackNumber());
                    rackCell.setCellStyle(cellStyle);
                }
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(2);
            try (FileOutputStream file = new FileOutputStream(new File(filePath.getParent() + "/Plano de produção.xlsx"))) {
                savedWorkBook.write(file);
                System.out.println("Arquivo de planejamento da produção salvo com sucesso!");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Nenhum rack está disponível para ser salvo no arquivo.");
        }
    }

    /**
     * Disable loading of 5k products.
     *
     * @param doNotLoad5k A boolean.
     */
    public void setDoNotLoad5k(boolean doNotLoad5k) {
        this.doNotLoad5k = doNotLoad5k;
    }

    /**
     * Disable loading of 6k and 7k products.
     *
     * @param doNotLoad6And7k a boolean.
     */
    public void setDoNotLoad6And7k(boolean doNotLoad6And7k) {
        this.doNotLoad6And7k = doNotLoad6And7k;
    }

    /**
     * Disable loading of 8k products.
     *
     * @param doNotLoad8k A boolean.
     */
    public void setDoNotLoad8k(boolean doNotLoad8k) {
        this.doNotLoad8k = doNotLoad8k;
    }
}