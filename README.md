# Client-Server DB Application

Two-tier client-server application demonstrating a simple client-server architecture using Java and MySQL.
Separated the client interface and the database server to build a basic implementation of a two-tier architecture. It allows users to interact with a MySQL database through a Java GUI client.


## Compilation and Execution
1. **Compile the Main Application:**
    ```sh
    javac -cp lib/mysql-connector-j-x.x.x.jar -d bin src/DatabaseGUI.java src/DatabaseConnection.java
    ```
2. **Compile the Accountant Application:**
    ```sh
    javac -cp lib/mysql-connector-j-x.x.x.jar -d bin src/AccountantGUI.java src/DatabaseConnection.java
    ```
3. **Run the Main Application:**
    ```sh
    java -cp "bin:lib/mysql-connector-j-x.x.x.jar" DatabaseGUI
    ```
4. **Run the Accountant Application:**
    ```sh
    java -cp "bin:lib/mysql-connector-j-x.x.x.jar" AccountantGUI
    ```
