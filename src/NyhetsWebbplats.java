import java.sql.*;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NyhetsWebbplats {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/news_website";
    private static final String USER = "root"; // ersätt med ditt MySQL-användarnamn
    private static final String PASS = "asd-112211"; // ersätt med ditt MySQL-lösenord

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("NyhetsWebbplats Kontohantering");
            System.out.println("1. Skapa Konto");
            System.out.println("2. Visa Konton");
            System.out.println("3. Radera Konto");
            System.out.println("4. Avsluta");
            System.out.print("Välj ett alternativ: ");
            int val = scanner.nextInt();
            scanner.nextLine(); // För att konsumera newline

            switch (val) {
                case 1:
                    skapaKonto(scanner);
                    break;
                case 2:
                    visaKonton();
                    break;
                case 3:
                    raderaKonto(scanner);
                    break;
                case 4:
                    System.out.println("Avslutar programmet...");
                    scanner.close();
                    return; // Avslutar programmet
                default:
                    System.out.println("Ogiltigt val. Försök igen.");
            }
        }
    }

    private static void skapaKonto(Scanner scanner) {
        System.out.print("Ange Namn: ");
        String namn = scanner.nextLine();

        System.out.print("Ange E-post: ");
        String email = scanner.nextLine();

        System.out.print("Ange Ålder: ");
        int alder = scanner.nextInt();
        scanner.nextLine(); // Rensa newline

        System.out.print("Ange Kön (M/F/Other): ");
        String kon = scanner.nextLine();

        System.out.print("Ange Lösenord: ");
        String losenord = scanner.nextLine();
        String hashatLosenord = hashaLosenord(losenord);

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (name, email, age, gender, password) VALUES (?, ?, ?, ?, ?)")) {

            stmt.setString(1, namn);
            stmt.setString(2, email);
            stmt.setInt(3, alder);
            stmt.setString(4, kon);
            stmt.setString(5, hashatLosenord);

            stmt.executeUpdate();
            System.out.println("Konto skapat!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void visaKonton() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String namn = rs.getString("name");
                String email = rs.getString("email");
                int alder = rs.getInt("age");
                String kon = rs.getString("gender");

                System.out.println("ID: " + id + ", Namn: " + namn + ", E-post: " + email + ", Ålder: " + alder + ", Kön: " + kon);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void raderaKonto(Scanner scanner) {
        System.out.print("Ange användar-ID att radera: ");
        int id = scanner.nextInt();

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {

            stmt.setInt(1, id);
            int radBorttagen = stmt.executeUpdate();

            if (radBorttagen > 0) {
                System.out.println("Konto raderat!");
            } else {
                System.out.println("Användar-ID hittades inte.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String hashaLosenord(String losenord) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(losenord.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
