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

import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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

public class AppFX extends Application {

    @Override
    public void start(Stage stage) {
        // Setting up the label for the vending machine
        Label label = new Label("Vending Machine");
        label.setFont(new Font("Arial",20));
        // Getting the JSON
        JSONObject json = readJSONConfig("src\\main\\java\\com\\mycompany\\app\\input.json");
        JSONObject config = (JSONObject)json.get("config");
        int columns = Integer.parseInt((String)config.get("columns"));
        int rows = Integer.parseInt((String)config.get("columns"));
        // Making the table
        TableView table = new TableView();
        // setting up the table columns
        for(int i = 0; i < columns; i++){
            // TableColumn tc1 = new TableColumn("Right Side?");
            // TableColumn tc2 = new TableColumn("Left Side?");
            TableColumn tc = new TableColumn(Integer.toString(i + 1));
            // tc.getColumns().addAll(tc1,tc2);
            table.getColumns().addAll(tc);
        }
        // setting up the rows    
        // Setting up the viewbox
        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10,0,0,10));
        vbox.getChildren().addAll(label, table);
        // Scene scene = new Scene(new StackPane(l), 640, 480);
        Scene scene = new Scene(new Group());
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

