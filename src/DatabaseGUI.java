/*
Name: David Samson
Course: CNT 4714 Summer 2024
Assignment title: Project 2 – A Two-tier Client-Server Application
Date: July 7, 2024
Class: DatabaseGUI
*/

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DatabaseGUI extends JFrame {
    private JTextArea sqlInputArea;
    private JTable sqlOutputTable;
    private JTextField usernameField, passwordField;
    private JComboBox<String> dbUrlProperties, userProperties;
    private JButton executeButton, clearSqlCommandButton, connectButton, disconnectButton, clearResultButton;
    private JLabel statusLabel;
    private DatabaseConnection dbConnection;
    private DefaultTableModel tableModel;
    private Properties dbProperties;
    private Properties userPropertiesFile;

    public DatabaseGUI() {
        createUI();
        dbConnection = new DatabaseConnection();
        setTitle("SQL Client Application");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createUI() {
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        add(topPanel, BorderLayout.NORTH);

        JPanel connectionDetailsPanel = new JPanel(new GridLayout(2, 4));
        topPanel.add(connectionDetailsPanel);

        connectionDetailsPanel.add(new JLabel("DB URL Properties:"));
        dbUrlProperties = new JComboBox<>(new String[]{"project2.properties", "bikedb.properties"});
        connectionDetailsPanel.add(dbUrlProperties);

        connectionDetailsPanel.add(new JLabel("User Properties:"));
        userProperties = new JComboBox<>(new String[]{"root.properties", "client1.properties", "client2.properties"});
        connectionDetailsPanel.add(userProperties);

        connectionDetailsPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        connectionDetailsPanel.add(usernameField);

        connectionDetailsPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        connectionDetailsPanel.add(passwordField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        topPanel.add(buttonPanel);

        connectButton = new JButton("Connect to Database");
        buttonPanel.add(connectButton);

        disconnectButton = new JButton("Disconnect From Database");
        buttonPanel.add(disconnectButton);

        JPanel statusPanel = new JPanel(new FlowLayout());
        topPanel.add(statusPanel);

        statusLabel = new JLabel("NOT CONNECTED");
        statusPanel.add(statusLabel);

        JPanel sqlCommandPanel = new JPanel(new BorderLayout());
        sqlCommandPanel.add(new JLabel("Enter An SQL Command"), BorderLayout.NORTH);
        sqlInputArea = new JTextArea(5, 30);
        JScrollPane sqlInputScrollPane = new JScrollPane(sqlInputArea);
        sqlCommandPanel.add(sqlInputScrollPane, BorderLayout.CENTER);

        JPanel commandButtonPanel = new JPanel(new FlowLayout());
        clearSqlCommandButton = new JButton("Clear SQL Command");
        commandButtonPanel.add(clearSqlCommandButton);
        executeButton = new JButton("Execute SQL Command");
        commandButtonPanel.add(executeButton);
        sqlCommandPanel.add(commandButtonPanel, BorderLayout.SOUTH);

        JPanel sqlOutputPanel = new JPanel(new BorderLayout());
        JPanel clearButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clearResultButton = new JButton("Clear SQL Output");
        clearButtonPanel.add(clearResultButton);
        sqlOutputPanel.add(clearButtonPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        sqlOutputTable = new JTable(tableModel);
        JScrollPane sqlOutputScrollPane = new JScrollPane(sqlOutputTable);
        sqlOutputPanel.add(sqlOutputScrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sqlCommandPanel, sqlOutputPanel);
        splitPane.setResizeWeight(0.3);
        add(splitPane, BorderLayout.CENTER);

        executeButton.addActionListener(e -> executeSQL());
        connectButton.addActionListener(e -> connectToDatabase());
        disconnectButton.addActionListener(e -> disconnectFromDatabase());
        clearSqlCommandButton.addActionListener(e -> sqlInputArea.setText(""));
        clearResultButton.addActionListener(e -> tableModel.setRowCount(0));
    }

    private void executeSQL() {
        String sql = sqlInputArea.getText();
        try {
            ResultSet resultSet = dbConnection.executeQuery(sql);
            if (resultSet != null) {
                displayResults(resultSet);
            } else {
                JOptionPane.showMessageDialog(this, "Command executed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "SQL Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void displayResults(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        for (int i = 1; i <= columnCount; i++) {
            tableModel.addColumn(metaData.getColumnLabel(i));
        }

        while (resultSet.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = resultSet.getObject(i);
            }
            tableModel.addRow(row);
        }
    }

    private void connectToDatabase() {
        String dbUrlFile = (String) dbUrlProperties.getSelectedItem();
        String userFile = (String) userProperties.getSelectedItem();
        String username = usernameField.getText();
        String password = passwordField.getText();
        try {
            dbProperties = loadProperties(dbUrlFile);
            userPropertiesFile = loadProperties(userFile);

            String expectedUsername = userPropertiesFile.getProperty("username");
            String expectedPassword = userPropertiesFile.getProperty("password");

            if (!username.equals(expectedUsername) || !password.equals(expectedPassword)) {
                JOptionPane.showMessageDialog(this, "Incorrect credentials. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String dbUrl = dbProperties.getProperty("url");
            dbConnection.connect(dbUrl, username, password);
            statusLabel.setText("CONNECTED TO: " + dbUrl);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading properties files: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void disconnectFromDatabase() {
        try {
            dbConnection.disconnect();
            statusLabel.setText("NOT CONNECTED");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Properties loadProperties(String filename) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("properties/" + filename)) {
            properties.load(fis);
        }
        return properties;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseGUI gui = new DatabaseGUI();
            gui.setVisible(true);
        });
    }
}
