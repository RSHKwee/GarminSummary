import java.io.IOException;
import java.util.ArrayList;

import library.OutputTxt;

public class testappendfile {

  public static void main(String[] argv) {
    // testFile tt = new testFile("TestFile.txt");
    // tt.append("Aap noot mies");
    // tt.append("Weet je");
    // tt.close();

    try {
      OutputTxt ttt = new OutputTxt("TestFile1.txt", false);
      ArrayList<String> a_Regels = new ArrayList<String>();

      a_Regels.add("Aap noot mies");
      a_Regels.add("Weet je");

      ttt.Schrijf(a_Regels);

      ttt.Close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}