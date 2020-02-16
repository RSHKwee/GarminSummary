package main;

import java.util.ArrayList;

import garmin.Summary;
import library.TxtBestand;

public class Main {

  public static void main(String[] args) {
    String v_Rootdir = "D:\\Users\\René\\OneDrive\\Documenten\\Auto\\Garmin\\";
    Summary v_sum = new Summary(v_Rootdir + "Tracks\\2019\\Archive\\270.gpx");
    ArrayList<String> v_Regels = v_sum.TripsSummary();

    for (int i = 271; i <= 298; i++) {
      v_Regels.addAll(v_sum.TripsSummary(v_Rootdir + "Tracks\\2019\\Archive\\" + i + ".gpx"));
    }
    TxtBestand.DumpBestand(v_Rootdir + "current_2019_3.csv", v_Regels);

  }
}
