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
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.NumberFormatException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.*;

public class AppFX extends Application {
    static ArrayList<ObservableList<String>> allItems = new ArrayList<ObservableList<String>>();
    static Label loading = new Label("Enter new Item");
    // For making a prompt
    static final HBox hb = new HBox();
    static final TextField itemRow = new TextField();
    static final TextField itemColumn = new TextField();
    static final Button btn = new Button("Select");
    // For making payment
    static final HBox hb2 = new HBox();
    static Label paymentLabel = new Label();
    static final TextField payment = new TextField();
    static final Button btn2 = new Button("Select");

    static VBox vbox = new VBox();
    
    
    // For making a new item
    static final TextField itemName = new TextField();
    static final TextField itemPrice = new TextField();
    static final TextField itemAmount = new TextField();
    static JSONObject json = readJSONConfig("src\\main\\java\\com\\mycompany\\app\\input.json");

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
                // System.out.println(param.getValue().get(0)); 
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
                if(currentItem < itemsAmount){
                    JSONObject item = (JSONObject)items.get(currentItem);
                    itemToPut = (String)item.get("name") + "\n";
                    itemToPut = itemToPut + "amount: " +  String.valueOf(item.get("amount")) + "\n";
                    itemToPut = itemToPut + "price: " +   (String)item.get("price");          
                    currentItem++; 
                }
                currentRow.add(itemToPut);
            }
            // Adding to all items list
            allItems.add(currentRow);
            table.getItems().add(currentRow);

        }    
        // Setting up the prompt
        itemRow.setPromptText("Row");
        itemColumn.setPromptText("Column");
        payment.setPromptText("Payment");
        btn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e){
                // System.out.println(itemRow.getText());
                // System.out.println(itemColumn.getText());
                // System.out.println(payment.getText());
                // prompt.setOnInputMethodTextChanged(arg0);
                // prompt.applyCss("color: red;");
                payment.setOpacity(100);
                btn2.setOpacity(100);
                paymentLabel.setOpacity(100);
                itemRow.clear();
                itemColumn.clear();
            }
        });
        hb.getChildren().addAll(itemRow, itemColumn, payment,btn);

        // Setting up the payment
        paymentLabel.setText("Please enter payment");
        paymentLabel.setOpacity(0);
        payment.setPromptText("Payment");
        payment.setOpacity(0);
        btn2.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e){
                payment.setOpacity(0);
                btn2.setOpacity(0);
                paymentLabel.setOpacity(0);
            } 
        });
        btn2.setOpacity(0);
        hb2.getChildren().addAll(payment,btn2);
    
        // Setting up the loading options
        final HBox loadingHBoxLabel = new HBox();
        
        loading.setFont(new Font("Arial",20));
        loadingHBoxLabel.getChildren().addAll(loading);
        final HBox loadingHBox = new HBox();
        itemName.setPromptText("New Item Name");
        itemAmount.setPromptText("New Item Amount");
        itemPrice.setPromptText("New Price");
        Button add = new Button("Add Item");
        add.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e){
                addItem();
                table.refresh();
            } 
        });
        loadingHBox.getChildren().addAll(itemName, itemAmount, itemPrice, add);
        
        // Setting up the Vbox
        // vbox.setSpacing(5);
        // vbox.setPadding(new Insets(10,0,0,10));
        vbox.getChildren().addAll(label, table, promptHBox,hb,paymentLabel,hb2, loadingHBoxLabel,loadingHBox);
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

    public static void addItem(){
        // Update the ArrayList
        // Find the next available slot
        for(int i = 0; i < allItems.size(); i++){
            ObservableList<String> currentRow = allItems.get(i);
            System.out.println(currentRow);
            for(int j = 0; j < currentRow.size(); j++){
                if (currentRow.get(j).equals("")){
                    ObservableList<String> newRow = FXCollections.observableArrayList(currentRow);
                    // Making the new item
                    String newItemName = itemName.getText();
                    String newItemPrice = itemPrice.getText();
                    String newItemAmount = itemAmount.getText();
                    if( !newItemName.equals("") && !newItemPrice.equals("") && !newItemAmount.equals("") ){
                        String newItem = newItemName + "\n" +  "amount: " + newItemAmount +  "\n" + "price: $" + newItemPrice;
                        System.out.println(newItem); // TODO: Delete;
                        newRow.set(j, newItem);
                        currentRow.removeAll(currentRow);
                        currentRow.addAll(newRow);
                        itemName.clear();
                        itemPrice.clear();
                        itemAmount.clear();
                        // Up date the json
                        JSONObject newJSONObject = new JSONObject();
                        newJSONObject.put("amount", Long.parseLong(newItemAmount));
                        newJSONObject.put("price", newItemPrice);
                        newJSONObject.put("name",newItemName);
                        
                        JSONArray itemsList = (JSONArray)json.get("items");
                        itemsList.add(newJSONObject);
                        writeJSONconfig();
                    }
                    return;
                }
            }
        }
        loading.setText("Sorry No More Room");
        loading.setStyle("-fx-text-fill: red");
        
    }

    public static JSONObject readJSONConfig(String jsonPath) throws NumberFormatException{ 
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

    public static void writeJSONconfig(){
        try(FileWriter file = new FileWriter("src/main/java/com/mycompany/app/input.json")){
            file.write(json.toJSONString());
            file.flush();
            System.out.println("writeJSONConfig");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

