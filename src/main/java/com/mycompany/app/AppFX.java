package com.mycompany.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

// Using Big Decimal for money
import java.math.BigDecimal;
import java.nio.file.FileAlreadyExistsException;

// Used Read the JSON file

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.*;

public class AppFX extends Application {

    @Override
    public void start(Stage stage) {
        // Setting up the label for the vending machine
        Label label = new Label("Vending Machine");
        label.setFont(new Font("Arial",20));
        // Getting the JSON
        JSONObject json = readJSONConfig("src\\main\\java\\com\\mycompany\\app\\input.json");
        JSONObject config = (JSONObject)json.get("config");
        JSONArray items = (JSONArray)json.get("items");
        int itemsAmount = items.size();
        int columns = Integer.parseInt((String)config.get("columns"));
        long rows = (long) config.get("rows");
        // Making the table
        TableView<ObservableList<String>> table = new TableView<>();
        // table.setStyle("-fx-background-color: white; -fx-border-width: 0px;"); TODO:Delete Later
        // setting up the table columns
        TableColumn <ObservableList<String>, String> letterColumn = new TableColumn<>("Letter");
        letterColumn.setCellValueFactory(param ->{ 
            // System.out.println(param.getValue().get(0)); // TODO: Delete Later 
            return new ReadOnlyObjectWrapper(param.getValue().get(0)); 
        });
        table.getColumns().add(letterColumn);

        for(int i = 0; i < columns; i++){
            final int finalIdx = i;
            TableColumn <ObservableList<String>,String> tc = new TableColumn<>(Integer.toString(i + 1));
            tc.setCellValueFactory(param ->{ 
                System.out.println(param.getValue().get(0)); 
                return new ReadOnlyObjectWrapper(param.getValue().get(finalIdx + 1)); 
            });
            table.getColumns().add(tc);
        }
        
        // Setting up the rows
        int currentItem = 0;
        int rowName = 65;
        for(int row = 0; row < rows; row++){
            ObservableList<String> currentRow = FXCollections.observableArrayList();
            currentRow.add(Character.toString((char)rowName));
            rowName++;
            // Going througth all the available slots 
            for(int aval = 0; aval < columns; aval++){
                String itemToPut = "";
                System.out.println(currentItem);
                System.out.println(currentItem != itemsAmount);
                if(currentItem < itemsAmount){
                    JSONObject item = (JSONObject)items.get(currentItem);
                    itemToPut = (String)item.get("name") + "\n";
                    itemToPut = itemToPut + "amount: " +  String.valueOf(item.get("amount")) + "\n";
                    itemToPut = itemToPut + "price: " +   (String)item.get("price");
                    System.out.println(itemToPut); //TODO: Delete           
                    currentItem++; 
                }
                currentRow.add(itemToPut);
            }
            table.getItems().add(currentRow);

            //TODO: I dont think I need the below
            // for(int i = 0; i < columns; i++){
            //     currentRow.add("");
            // }
            // table.getItems().add(currentRow);
        }    
        // table.getItems().add(FXCollections.observableArrayList("1","2","3","4","5","6","7","8")); // TODO: Delete Later
        // Setting up the viewbox
        VBox vbox = new VBox();
        // vbox.setSpacing(5);
        // vbox.setPadding(new Insets(10,0,0,10));
        vbox.getChildren().addAll(label, table);
        // Scene scene = new Scene(new StackPane(l), 640, 480);
        Scene scene = new Scene(new Group());
        // TODO make the style sheet work lol
        // Adding the styling sheet
        String cssPath = System.getProperty("user.dir") + "\\"+"src\\main\\java\\com\\mycompany\\app\\stylesheet.css";
        // scene.getStylesheets().add("D:\\Documents\\MS3 Coding Challenge\\my-app\\src\\main\\java\\com\\mycompany\\app\\stylesheet.css");
        // System.out.println(this.getClass().getResource("stylesheet.css"));
        // scene.getStylesheets().add("D:/Documents/MS3 Coding Challenge/my-app/src/main/java/com/mycompany/app/stylesheet.css");
        // scene.getStylesheets().add(".table-row-cell:empty{-fx-background-color: white;}");
        // Adding the View Box to the scene
        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        stage.setTitle("Vending Machine");
        stage.setScene(scene);
        stage.show();
        
    }

    public static void main(String[] args) {
        launch();
    }

    public static JSONObject readJSONConfig(String jsonPath){ 
        // getting the json path
        String path = System.getProperty("user.dir") + "\\"+ jsonPath;
        // JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
        JSONObject employeeList = null;
        try(FileReader reader = new FileReader(path)){
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            employeeList = (JSONObject) obj;
            return employeeList;
            // System.out.println(employeeList);
        }catch (FileAlreadyExistsException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (ParseException e){  
            e.printStackTrace();
        }
        return null;
    }

    public static class Item{
        private String name;
        private int amount;
        private BigDecimal price;
        
        public String getName(){
            return this.name;
        }

        public void setName(String name){
            this.name = name;
        }

        public int getAmount(){
            return amount;
        }

        public void setAmount(int amount){
            this.amount = amount;
        }

        public BigDecimal getPrice(){
            return this.price;
        }

        public void setPrice(BigDecimal price){
            this.price = price;
        }
    }
}

