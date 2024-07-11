#TO COMPILE CLIENT / MAIN APPLICATION
javac -cp lib/mysql-connector-j-x.x.x.jar -d bin src/DatabaseGUI.java src/DatabaseConnection.java

#TO COMPILE ACCOUNTANT APPLICATION
javac -cp lib/mysql-connector-j-x.x.x.jar -d bin src/AccountantGUI.java src/DatabaseConnection.java

#RUN MAIN APP
java -cp "bin:lib/mysql-connector-j-x.x.x.jar" DatabaseGUI

#RUN ACCOUNTANT APP
java -cp "bin:lib/mysql-connector-j-x.x.x.jar" AccountantGUI
