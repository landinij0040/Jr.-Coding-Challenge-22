package com.mycompany.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

// Using Big Decimal for money 
import java.math.BigDecimal; // TODO: Delete probably
import java.nio.file.FileAlreadyExistsException;

// Used to Read the JSON file

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
        // Setting up the prompt label
        Label prompt = new Label("Welcome Enter Your Selection");
        prompt.setFont(new Font("Arial",20));
        

        
        
        final HBox promptHBox = new HBox();
        promptHBox.getChildren().addAll(prompt);
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

        }    
        // Setting up the selction options
        final HBox hb = new HBox();
        final TextField itemRow = new TextField();
        itemRow.setPromptText("Row");
        final TextField itemColumn = new TextField();
        itemColumn.setPromptText("Column");
        final TextField payment = new TextField();
        payment.setPromptText("Payment");

        final Button btn = new Button("Add");
        btn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e){
                System.out.println(itemRow.getText());
                System.out.println(itemColumn.getText());
                System.out.println(payment.getText());
                // prompt.setOnInputMethodTextChanged(arg0);
                // prompt.applyCss("color: red;");

                System.out.println();
                prompt.setText("Switch");
                itemRow.clear();
                itemColumn.clear();
                payment.clear();
            }
        });
        hb.getChildren().addAll(itemRow, itemColumn, payment,btn);

    
        // Setting up the loading options
        final HBox loadingHBoxLabel = new HBox();
        Label loading = new Label("Enter new Item");
        loading.setFont(new Font("Arial",20));
        loadingHBoxLabel.getChildren().addAll(loading);
        final HBox loadingHBox = new HBox();
        final TextField itemName = new TextField();
        itemName.setPromptText("New Item Name");
        final TextField itemAmount = new TextField();
        itemAmount.setPromptText("New Item Amount");
        final TextField itemPrice = new TextField();
        itemPrice.setPromptText("New Price");
        loadingHBox.getChildren().addAll(itemName, itemAmount, itemPrice);
        
        
    

        
        
        // Setting up the button
        
        // Setting up the Vbox
        VBox vbox = new VBox();
        // vbox.setSpacing(5);
        // vbox.setPadding(new Insets(10,0,0,10));
        vbox.getChildren().addAll(label, table, promptHBox,hb, loadingHBoxLabel,loadingHBox);
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

    public static addItem(){
        // Update the ArrayList
        // Update the Json
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
}

