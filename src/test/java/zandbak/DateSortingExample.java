package zandbak;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class DateSortingExample {
    public static void main(String[] args) {
        String[] dateStrings = {"2023-07-15", "2023-07-18", "2023-07-16", "2023-07-17"};

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate[] dates = Arrays.stream(dateStrings)
                .map(str -> LocalDate.parse(str, formatter))
                .toArray(LocalDate[]::new);

        Arrays.sort(dates);

        for (LocalDate date : dates) {
            System.out.println(date.format(formatter));
        }
    }
}

