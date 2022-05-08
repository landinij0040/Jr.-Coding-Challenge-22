# Jr. Coding Challenge 22

## Command to start

```mvn clean javafx:run ```

## Informtion
This is a vending machine for the Jr. Coding Challenge 22. Was made with JavaFx and Maven.

## Requirements
### Reading the JSON
Reading the JSON configuration file will be read by the simple.JSON package downloaded by maven.

### Providing the loading option for new product lists to update
On the GUI, there is an option for the user to add an item to the 
item list. Once the item is entered, the program will update the 
configure json with the simple.JSON package and update the JavaFX table view for the user.

### Rows will be letters
On the start up of the app the JSON config file will be read and put into
a JSONObject object and the configuration for the row will be recorded
to be put into a for loop that will dynamically add letters ,via ascii, 
to the rows of the vending machine.

### Columns will be numbers.
On the start up of the app the columns amount will be collected and will 
dynamically make columns based on the config JSON.

### User must be able to enter a selection
Once the start of the app is up and running a user can enter the desired selection
using the text entries for column and row.


### Machine must prompt the user for payment and amount in US dollars
Once the user enters a selction a new section pops up and asks the 
user for payment and amount in US dollars.

### If user enters in payment, calculation must occur and be reported.
Once the user enters payment the application will calculate the payment and 
see if the payment is enough. If the paymentis enough, the application will
give the change in the main prompt.

### Machine must state the current state of the transaction.
The application will state the transaction in the 
prompts via the JavaFx gui.

### Actions must be cleanly logged for audit purposes.
Once the appplication starts a new log file will be genterated 
in the src folder if a log file is not already made. Once a transaction 
occurs the actions will be logged.

## Bonus features
### UI/UX
The application uses JavaFX via Maven to make the user interface.


