package com.mycompany.app;

import javafx.application.Application;
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

import java.nio.file.FileAlreadyExistsException;

// Used to Read the JSON file

import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.NumberFormatException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.*;

//For making the time stamp
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;    

public class AppFX extends Application {
    
    // Used for loging
    static FileWriter fw;
    static BufferedWriter bw;
    static PrintWriter pw;

    static Long rows;
    static int columns;
    static ArrayList<ObservableList<String>> allItems = new ArrayList<ObservableList<String>>();
    
    // For making a prompt
    static Label loading = new Label("Enter new Item");
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
    static JSONObject currentObject;
    
    // For making a new item
    static final TextField itemName = new TextField();
    static final TextField itemPrice = new TextField();
    static final TextField itemAmount = new TextField();
    static JSONObject json = readJSONConfig("src\\main\\java\\com\\mycompany\\app\\input.json");
    static JSONArray items;

    static Label prompt;
    @Override
    public void start(Stage stage) {

        // Making the log file
        try {
            File logFile = new File("log.txt");
            logFile.createNewFile();
            
        } catch (Exception e) {
            System.out.println("Error Ocurred when making the log file");
            e.printStackTrace();
        }
        // Setting up the label for the vending machine
        Label label = new Label("Vending Machine");
        label.setFont(new Font("Arial",20));
        // Setting up the prompt label
        prompt = new Label("Welcome Enter Your Selection");
        prompt.setFont(new Font("Arial",20));
    
        final HBox promptHBox = new HBox();
        promptHBox.getChildren().addAll(prompt);
        // Getting the JSON
        
        JSONObject config = (JSONObject)json.get("config");
        items = (JSONArray)json.get("items");
        int itemsAmount = items.size();
        columns = Integer.parseInt((String)config.get("columns"));
        rows = (long) config.get("rows");
        // Making the table
        TableView<ObservableList<String>> table = new TableView<>();
        // table.setStyle("-fx-background-color: white; -fx-border-width: 0px;"); TODO:Delete Later
        // setting up the table columns
        TableColumn <ObservableList<String>, String> letterColumn = new TableColumn<>("Letter");
        letterColumn.setCellValueFactory(param ->{ 
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
                if(checkIfSelection() && isAvailable()){
                    prompt.setText("Welcome Enter Selection");
                    paymentLabel.setStyle("-fx-text-fill: black");
                    prompt.setStyle("-fx-text-fill: black");
                    // Change the payment text to show the payment for the item selected.
                    String row = itemRow.getText();
                    int column = Integer.valueOf(itemColumn.getText());
                    JSONObject foundItem = findItem(row, column);
                    Long amount = (long) foundItem.get("amount");
                    if(amount == 0){
                        prompt.setText("Selction is empty");
                        prompt.setStyle("-fx-text-fill: red");
                        return;
                    }
                    paymentLabel.setText(foundItem.get("name") + " is " + foundItem.get("price") );
                    payment.setOpacity(100);
                    btn2.setOpacity(100);
                    paymentLabel.setOpacity(100);  
                }else{
                    prompt.setText("Not a valid selection Please Try Again");
                    prompt.setStyle("-fx-text-fill: red");
                }
                
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
                
                // Check if the money is good
                String price = (String) currentObject.get("price");
                price = price.replace("$","");
                Double doublePrice = Double.parseDouble(price); 
                Double doublePayment = Double.parseDouble(payment.getText()); 

                Double result = doublePrice - doublePayment;
                
                if(!(result < 0)){
                    String text = paymentLabel.getText();
                    text = "Sorry not enough!! \n" + text; 
                    paymentLabel.setText(text);
                    paymentLabel.setStyle("-fx-text-fill: red");
                    return; 
                }else{
                    prompt.setText("Your change is $" + Double.toString(result) + " Welcome Enter Selection");
                }
                updateJSON();
                int oldRow = ((int) itemRow.getText().charAt(0)) - 65;
                int oldColumn = Integer.parseInt(itemColumn.getText());
                ObservableList<String> oldItem = allItems.get(oldRow);
                String oldValue = oldItem.get(oldColumn);
                String[] oldValueArray = oldValue.split("\n");
                String[] oldAmountArray = oldValueArray[1].split(" ");
                oldAmountArray[1] = String.valueOf( Integer.parseInt(oldAmountArray[1]) - 1 );
                oldValueArray[1] = "";

                for(int i = 0; i < oldAmountArray.length; i++){
                    oldValueArray[1] = oldValueArray[1] + oldAmountArray[i] + " ";
                }
                oldValue = "";
                for(int i = 0; i < oldValueArray.length; i++){
                    System.out.println(oldValueArray[i]);
                    oldValue = oldValue + oldValueArray[i] + "\n";
                }
                System.out.println("In the second button");
                System.out.println(oldValue);
                oldItem.set(oldColumn, oldValue);
                table.refresh();
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
                log("Adding mother fucker");
                table.refresh();
                
            } 
            
        });
        loadingHBox.getChildren().addAll(itemName, itemAmount, itemPrice, add);
        vbox.getChildren().addAll(label, table, promptHBox,hb,paymentLabel,hb2, loadingHBoxLabel,loadingHBox);
        Scene scene = new Scene(new Group());
        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        stage.setTitle("Vending Machine");
        stage.setScene(scene);
        stage.show();
        
    }

    public static void main(String[] args) {
        launch();
    }

    public static JSONObject findItem(String row, int column){
        JSONObject r = new JSONObject();
        // Get the item
        for(int i = 0; i < allItems.size(); i++){
            ObservableList<String> currentRow = allItems.get(i);
            Boolean correctRow = currentRow.get(0).equals(row);
            if(correctRow){
                String foundItem = currentRow.get(column);
                // If the current slot is empty
                if(foundItem.equals("")){
                    return r;
                }
                String currName = "";
                for(int z = 0; z < foundItem.length(); z++){
                    if(!(foundItem.charAt(z) == '\n')){
                        currName = currName + foundItem.charAt(z);
                    }else{
                        break;
                    }
                }

                for(int item = 0;item < items.size(); item++){
                    currentObject = (JSONObject) items.get(item);
                    if(currentObject.get("name").equals(currName)){
                        return currentObject;
                    }
                }
            }
            
        }
        return null;

    }

    public static void updateJSON(){
        Long lastValue = (Long) currentObject.get("amount");
        currentObject.replace("amount", lastValue - 1);
        writeJSONconfig();
    }

    public static void addItem(){
        // Update the ArrayList
        // Find the next available slot
        for(int i = 0; i < allItems.size(); i++){
            ObservableList<String> currentRow = allItems.get(i);
            for(int j = 0; j < currentRow.size(); j++){
                if (currentRow.get(j).equals("")){
                    ObservableList<String> newRow = FXCollections.observableArrayList(currentRow);
                    // Making the new item
                    String newItemName = itemName.getText();
                    String newItemPrice = itemPrice.getText();
                    String newItemAmount = itemAmount.getText();
                    if( !newItemName.equals("") && !newItemPrice.equals("") && !newItemAmount.equals("") ){
                        String newItem = newItemName + "\n" +  "amount: " + newItemAmount +  "\n" + "price: $" + newItemPrice;
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
        }catch (FileAlreadyExistsException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (ParseException e){  
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isAvailable(){
        int row = (int) itemRow.getText().charAt(0) - 65;
        int col = Integer.valueOf(itemColumn.getText());
        if(! allItems.get(row).get(col).equals("")){
            return true;
        }
        return false;
    }
    public static boolean checkIfSelection(){
        ArrayList<String> availChars = new ArrayList<String>();
        int currentRow = 65;
        for(int i = 0; i < rows; i++ ){
            availChars.add(Character.toString((char) currentRow));
            currentRow++;
        }
        Boolean inColumn = availChars.contains(itemRow.getText());
        int columnToCheck = Integer.parseInt(itemColumn.getText()); 
        Boolean isNotZero = !(columnToCheck == 0);
        Boolean isNotNegative = columnToCheck > 0;
        Boolean isNotBiggerThanRow = !(columnToCheck > columns);
        Boolean inRow = (isNotZero && isNotNegative && isNotBiggerThanRow);
        if(inColumn && inRow){
            return true; 
        }else{
            return false;
        }
    }

    public static void writeJSONconfig(){
        try(FileWriter file = new FileWriter("src/main/java/com/mycompany/app/input.json")){
            file.write(json.toJSONString());
            file.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void log(String text){
            
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now();     
        
        try {
            fw = new FileWriter("log.txt", true);
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);
            
            pw.println("[ "+ dtf.format(now) + " ] " + text);
            System.out.println("Data Successfully appended into file");
            pw.flush();

        }catch(IOException io){
            io.printStackTrace();
        } 
        finally {
            try {
                // pw.close();
                bw.close();
                fw.close();
            } catch (IOException io) {// can't do anything }
                io.printStackTrace();
            }

        }
    }
}

