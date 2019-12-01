package main;

import java.util.ArrayList;

import garmin.Summary;
import library.TxtBestand;

public class Main {

  public static void main(String[] args) {

    Summary v_sum = new Summary("F:\\Users\\René\\OneDrive\\Documenten\\Auto\\Garmin\\Tracks\\2019\\Archive\\240.gpx");
    ArrayList<String> v_Regels = v_sum.TripsSummary();

    for (int i = 241; i < 284; i++) {
      v_Regels.addAll(v_sum
          .TripsSummary("F:\\Users\\René\\OneDrive\\Documenten\\Auto\\Garmin\\Tracks\\2019\\Archive\\" + i + ".gpx"));

    }
    TxtBestand.DumpBestand("F:\\Users\\René\\OneDrive\\Documenten\\Auto\\Garmin\\current_2019.csv", v_Regels);

  }
}
