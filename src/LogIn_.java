import java.util.Scanner;
import java.util.Random;
import java.sql.*;

// Class for user login and reservation handling
public class LogIn_ {

    // Constants for PNR generation (min and max bounds)
    private static final int min = 10000;
    private static final int max = 99999;

    // Nested user class for login credentials handling
    public static class user {
        private String user_name;
        private String pass_word;
        Scanner sc = new Scanner(System.in);

        // Method to get user name input
        public String getuser_name() {
            System.out.println("Enter a User Name:");
            user_name = sc.nextLine();
            return user_name;
        }

        // Method to get password input
        public String getpass_word() {
            System.out.println("Enter Password:");
            pass_word = sc.nextLine();
            return pass_word;
        }
    }

    // Nested class for managing PNR records
    public static class PnrRecord {
        private int PnrNumber;
        private String passengerName;
        private String TrainNumber;
        private String ClassType;
        private String journey_Date;
        private String from;
        private String To;

        Scanner sc = new Scanner(System.in);

        // Method to generate random PNR number
        public int getpnrNumber() {
            Random R = new Random();
            PnrNumber = R.nextInt(max) + min;
            return PnrNumber;
        }

        // Method to get passenger's name
        public String getPassengerName() {
            System.out.println("Enter the Passenger name:");
            passengerName = sc.nextLine();
            return passengerName;
        }

        // Method to get train number
        public String getTrainNumber() {
            System.out.println("Enter the Train number:");
            TrainNumber = sc.nextLine();
            return TrainNumber;
        }

        // Method to get class type (e.g., first class, sleeper)
        public String getClassType() {
            System.out.println("Enter the Class type:");
            ClassType = sc.nextLine();
            return ClassType;
        }

        // Method to get journey date in 'yyyy-MM-DD' format
        public String getJourneyDate() {
            System.out.println("Enter the Journey Date in 'yyyy-MM-DD' format:");
            journey_Date = sc.nextLine();
            return journey_Date;
        }

        // Method to get journey starting point
        public String getFrom() {
            System.out.println("Enter the City Name where your journey starts from:");
            from = sc.nextLine();
            return from;
        }

        // Method to get destination of the journey
        public String getTo() {
            System.out.println("Enter your Destination:");
            To = sc.nextLine();
            return To;
        }
    }
}

// Main class to execute the program logic
class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Instantiate the user class to get login credentials
        LogIn_.user u1 = new LogIn_.user();
        String user_name = u1.getuser_name();  // Get the username
        String pass_word = u1.getpass_word();  // Get the password

        // Database connection URL (make sure the database 'sarthak' exists)
        String url = "jdbc:mysql://localhost:3306/sarthak?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection using the provided credentials
            try (Connection connection = DriverManager.getConnection(url, user_name, pass_word)) {
                System.out.println("User connection granted...\n");

                // Infinite loop to keep the program running until exit is selected
                while (true) {
                    // SQL queries for insertion, deletion, and selection
                    String InsertQuery = "insert into reservation values (?, ?, ?, ?, ?, ?, ?)";
                    String DeleteQuery = "Delete From reservation Where Pnr_number = ?";
                    String ShowQuery = "Select * from reservation";

                    // Display menu options for the user
                    System.out.println("Enter the choice:");
                    System.out.println("1. Insert Record.");
                    System.out.println("2. Delete Record.");
                    System.out.println("3. Show all the records.");
                    System.out.println("4. Exit.");
                    int choice = sc.nextInt();  // Take user's menu choice

                    // Case 1: Insert a new record
                    if (choice == 1) {
                        LogIn_.PnrRecord p1 = new LogIn_.PnrRecord();  // Instantiate PnrRecord to gather passenger details
                        int pnrnumber = p1.getpnrNumber();
                        String passengerName = p1.getPassengerName();
                        String trainNumber = p1.getTrainNumber();
                        String ClassType = p1.getClassType();
                        String journeyDate = p1.getJourneyDate();
                        String getfrom = p1.getFrom();
                        String getto = p1.getTo();

                        // Execute Insert query to add the record to the database
                        try (PreparedStatement preparedStatement = connection.prepareStatement(InsertQuery)) {
                            preparedStatement.setInt(1, pnrnumber);
                            preparedStatement.setString(2, passengerName);
                            preparedStatement.setString(3, trainNumber);
                            preparedStatement.setString(4, ClassType);
                            preparedStatement.setString(5, journeyDate);
                            preparedStatement.setString(6, getfrom);
                            preparedStatement.setString(7, getto);

                            int rowsAffected = preparedStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("Record added Successfully.");
                            } else {
                                System.out.println("No records were added.");
                            }
                        } catch (SQLException e) {
                            System.err.println("SQLException: " + e.getMessage());
                        }

                        // Case 2: Delete a record based on PNR number
                    } else if (choice == 2) {
                        System.out.println("Enter the PNR number to delete the record:");
                        int PnrNumber = sc.nextInt();

                        // Execute Delete query to remove the record from the database
                        try (PreparedStatement preparedStatement = connection.prepareStatement(DeleteQuery)) {
                            preparedStatement.setInt(1, PnrNumber);
                            int rowsAffected = preparedStatement.executeUpdate();

                            if (rowsAffected > 0) {
                                System.out.println("Record deleted Successfully.");
                            } else {
                                System.out.println("No records were deleted.");
                            }
                        } catch (SQLException e) {
                            System.err.println("SQLException: " + e.getMessage());
                        }

                        // Case 3: Display all records
                    } else if (choice == 3) {
                        // Execute Select query to retrieve and display all records
                        try (PreparedStatement preparedStatement = connection.prepareStatement(ShowQuery);
                             ResultSet resultSet = preparedStatement.executeQuery()) {
                            System.out.println("\nAll records:\n");
                            while (resultSet.next()) {
                                String PnrNumber = resultSet.getString("pnr_number");
                                String passengerName = resultSet.getString("passenger_name");
                                String trainNumber = resultSet.getString("train_number");
                                String classType = resultSet.getString("class_type");
                                String journeyDate = resultSet.getString("journey_date");
                                String fromLocation = resultSet.getString("from_location");
                                String toLocation = resultSet.getString("to_location");

                                // Print the details of each record
                                System.out.println("PNR Number: " + PnrNumber);
                                System.out.println("Passenger Name: " + passengerName);
                                System.out.println("Train Number: " + trainNumber);
                                System.out.println("Class Type: " + classType);
                                System.out.println("Journey Date: " + journeyDate);
                                System.out.println("From Location: " + fromLocation);
                                System.out.println("To Location: " + toLocation);
                                System.out.println("-------------------------");
                            }
                        } catch (SQLException e) {
                            System.out.println("SQLException: " + e.getMessage());
                        }

                        // Case 4: Exit the program
                    } else if (choice == 4) {
                        System.out.println("Exiting the program.");
                        break;

                        // Invalid choice
                    } else {
                        System.out.println("Invalid Choice Entered.");
                    }
                }

            } catch (SQLException e) {
                System.out.println("SQLException: " + e.getMessage());
            }

        } catch (ClassNotFoundException e) {
            System.err.println("Error loading JDBC driver: " + e.getMessage());
        }

        sc.close();  // Close the Scanner to avoid resource leak
    }
}
