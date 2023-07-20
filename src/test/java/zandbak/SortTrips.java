package zandbak;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import kwee.library.FileUtils;
import kwee.library.TxtBestand;

public class SortTrips {

  static String Header = "";

  static public void sortTrips(String a_infile, String a_outfile) {
    SortedMap<LocalDateTime, String> sortedMap = new TreeMap<>();

    TxtBestand tbst = new TxtBestand(a_infile);
    ArrayList<String> l_regels = new ArrayList<String>();
    l_regels = tbst.getTxtContent();

    l_regels.forEach(regel -> {
      if (!regel.startsWith("#")) {
        String[] relElms = regel.split(";");
        if (relElms.length >= 2) {
          String datstr = relElms[0] + " " + relElms[1];

          // Define the DateTimeFormatter with the format of the input string
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

          // Parse the string and obtain the LocalDateTime object
          LocalDateTime localDateTime = LocalDateTime.parse(datstr, formatter);

          sortedMap.put(localDateTime, regel);
        }
      } else if (Header.isBlank()) {
        String[] headelm = regel.split("#");
        Header = headelm[1];
      }
    });
    Collection<String> l_Sortregels1 = sortedMap.values();
    ArrayList<String> l_Sortregels = new ArrayList<String>(l_Sortregels1);

    TxtBestand.DumpBestand(a_outfile, Header, l_Sortregels);
  }

  public static void main(String[] args) {
    String l_Directory = "";

    File l_File;
    l_File = FileUtils.GetResourceFile("current_362a.csv");
    l_Directory = l_File.getParent();

    String outfile = l_Directory + "\\" + "current_362a.csv";

    sortTrips(l_File.getAbsolutePath(), outfile);
  }

}
