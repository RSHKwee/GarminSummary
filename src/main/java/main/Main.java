package main;

import library.Address;
import library.NominatimReverseGeocodingJAPI;

public class Main {
  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("use -help for instructions");
    } else if (args.length < 2) {
      if (args[0].equals("-help")) {
        System.out.println("Mandatory parameters:");
        System.out.println("   -lat [latitude]");
        System.out.println("   -lon [longitude]");
        System.out.println("\nOptional parameters:");
        System.out.println("   -zoom [0-18] | from 0 (country) to 18 (street address), default 18");
        System.out.println("   -osmid       | show also osm id and osm type of the address");
        System.out.println("\nThis page:");
        System.out.println("   -help");
      } else
        System.err.println("invalid parameters, use -help for instructions");
    } else {
      boolean latSet = false;
      boolean lonSet = false;
      boolean osm = false;

      double lat = -200;
      double lon = -200;
      int zoom = 18;

      for (int i = 0; i < args.length; i++) {
        if (args[i].equals("-lat")) {
          try {
            lat = Double.parseDouble(args[i + 1]);
          } catch (NumberFormatException nfe) {
            System.out.println("Invalid latitude");
            return;
          }

          latSet = true;
          i++;
          continue;
        } else if (args[i].equals("-lon")) {
          try {
            lon = Double.parseDouble(args[i + 1]);
          } catch (NumberFormatException nfe) {
            System.out.println("Invalid longitude");
            return;
          }

          lonSet = true;
          i++;
          continue;
        } else if (args[i].equals("-zoom")) {
          try {
            zoom = Integer.parseInt(args[i + 1]);
          } catch (NumberFormatException nfe) {
            System.out.println("Invalid zoom");
            return;
          }

          i++;
          continue;
        } else if (args[i].equals("-osm")) {
          osm = true;
        } else {
          System.err.println("invalid parameters, use -help for instructions");
          return;
        }
      }

      if (latSet && lonSet) {
        NominatimReverseGeocodingJAPI nominatim = new NominatimReverseGeocodingJAPI(zoom);
        Address address = nominatim.getAdress(lat, lon);
        System.out.println(address);
        if (osm) {
          System.out.print("OSM type: " + address.getOsmType() + ", OSM id: " + address.getOsmId());
        }
      } else {
        System.err.println("please specifiy -lat and -lon, use -help for instructions");
      }
    }
  }

}
