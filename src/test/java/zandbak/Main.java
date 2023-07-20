package zandbak;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        // Input string representing the date and time
        String inputString = "2023-03-13 14:02:39";

        // Define the DateTimeFormatter with the format of the input string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Parse the string and obtain the LocalDateTime object
        LocalDateTime localDateTime = LocalDateTime.parse(inputString, formatter);

        // Now, you have the LocalDateTime object to work with
        System.out.println("LocalDateTime: " + localDateTime);
    }
}
