import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class hotelmanagement {
    private static Connection connection;

    public static void main(String[] args) {
        // Database Connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_booking", "root", "Roman&arun45");
            System.out.println("Database connected!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection failed!");
            return;
        }

        // Creating the UI
        JFrame frame = new JFrame("Hotel Booking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 700);

        // Setting background color
        JPanel panel = new JPanel();
        panel.setBackground(new Color(200, 230, 250));

        JLabel titleLabel = new JLabel("Hotel Booking System", JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30));


        // Buttons
        JPanel pane1 = new JPanel();
        JLabel lable = new JLabel();
        lable.setFont(new Font("Serif", Font.BOLD, 20));
        pane1.setBackground(new Color(200, 230, 250));
        pane1.setLayout(new GridLayout(8, 1, 8, 8));
        JButton viewRoomsButton = new JButton("View Available Rooms");
        JButton bookRoomButton = new JButton("Book a Room");
        JButton checkoutButton = new JButton("Check Out");
        JButton customerDetailsButton = new JButton("View Customer Details");
        JButton searchRoomButton = new JButton("Search Room by Customer Name");
        JButton viewAllRoomsButton = new JButton("View All Rooms");
        JButton updateRoomButton = new JButton("Update Room Info");
        JButton deleteBookingButton = new JButton("Delete Booking");

        pane1.add(viewRoomsButton);
        pane1.add(bookRoomButton);
        pane1.add(checkoutButton);
        pane1.add(customerDetailsButton);
        pane1.add(searchRoomButton);
        pane1.add(viewAllRoomsButton);
        pane1.add(updateRoomButton);
        pane1.add(deleteBookingButton);

        panel.add(titleLabel);
        frame.add(panel,BorderLayout.NORTH);
        frame.add(pane1,BorderLayout.CENTER);
        pane1.add(lable);
        frame.setVisible(true);

        // Button Actions
        viewRoomsButton.addActionListener(e -> viewAvailableRooms());
        bookRoomButton.addActionListener(e -> bookRoom());
        checkoutButton.addActionListener(e -> checkOut());
        customerDetailsButton.addActionListener(e -> viewCustomerDetails());
        searchRoomButton.addActionListener(e -> searchRoomByCustomerName());
        viewAllRoomsButton.addActionListener(e -> viewAllRooms());
        updateRoomButton.addActionListener(e -> updateRoomInfo());
        deleteBookingButton.addActionListener(e -> deleteBooking());
    }

    private static void viewAvailableRooms() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM rooms WHERE status='Available'");

            StringBuilder roomList = new StringBuilder("Available Rooms:\n");
            while (rs.next()) {
                roomList.append("Room Number: ").append(rs.getInt("room_number"))
                        .append(", Type: ").append(rs.getString("type")).append("\n");
            }

            JOptionPane.showMessageDialog(null, roomList.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void bookRoom() {
        String roomNumber = JOptionPane.showInputDialog("Enter Room Number to Book:");
        String customerName = JOptionPane.showInputDialog("Enter Customer Name:");

        try {
            String query = "UPDATE rooms SET status='Booked', customer_name=? WHERE room_number=? AND status='Available'";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, customerName);
            pstmt.setInt(2, Integer.parseInt(roomNumber));

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Room booked successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Room is not available!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void checkOut() {
        String roomNumber = JOptionPane.showInputDialog("Enter Room Number to Check Out:");

        try {
            String query = "UPDATE rooms SET status='Available', customer_name=NULL WHERE room_number=?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(roomNumber));

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Checked out successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Invalid Room Number!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewCustomerDetails() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM rooms WHERE status='Booked'");

            StringBuilder customerList = new StringBuilder("Booked Rooms:\n");
            while (rs.next()) {
                customerList.append("Room Number: ").append(rs.getInt("room_number"))
                        .append(", Customer Name: ").append(rs.getString("customer_name")).append("\n");
            }

            JOptionPane.showMessageDialog(null, customerList.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void searchRoomByCustomerName() {
        String customerName = JOptionPane.showInputDialog("Enter Customer Name to Search:");

        try {
            String query = "SELECT * FROM rooms WHERE customer_name=?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, customerName);
            ResultSet rs = pstmt.executeQuery();

            StringBuilder result = new StringBuilder("Rooms booked by ").append(customerName).append(":\n");
            while (rs.next()) {
                result.append("Room Number: ").append(rs.getInt("room_number"))
                        .append(", Type: ").append(rs.getString("type")).append("\n");
            }

            JOptionPane.showMessageDialog(null, result.length() > 0 ? result.toString() : "No rooms found for this customer.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewAllRooms() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM rooms");

            StringBuilder roomList = new StringBuilder("All Rooms:\n");
            while (rs.next()) {
                roomList.append("Room Number: ").append(rs.getInt("room_number"))
                        .append(", Type: ").append(rs.getString("type"))
                        .append(", Status: ").append(rs.getString("status")).append("\n");
            }

            JOptionPane.showMessageDialog(null, roomList.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateRoomInfo() {
        String roomNumber = JOptionPane.showInputDialog("Enter Room Number to Update:");
        String newType = JOptionPane.showInputDialog("Enter New Room Type:");

        try {
            String query = "UPDATE rooms SET type=? WHERE room_number=?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, newType);
            pstmt.setInt(2, Integer.parseInt(roomNumber));

            int rows = pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, rows > 0 ? "Room info updated!" : "Room not found!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteBooking() {
        String roomNumber = JOptionPane.showInputDialog("Enter Room Number to Delete Booking:");

        try {
            String query = "UPDATE rooms SET status='Available', customer_name=NULL WHERE room_number=?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(roomNumber));

            int rows = pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, rows > 0 ? "Booking deleted!" : "Room not found or already available!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

