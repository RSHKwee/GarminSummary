package zandbak;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
//import java.nio.charset.Charset;

public class testFile {
  FileWriter fw = null;
  BufferedWriter bw = null;
  PrintWriter out = null;

  public testFile(String filename) {
    try {
      // Charset charset = Charset.forName("ISO-8859-1");
      fw = new FileWriter(filename, true);
      bw = new BufferedWriter(fw);
      out = new PrintWriter(bw);

    } catch (IOException e) {
      // exception handling left as an exercise for the reader
      System.out.println(e.getMessage());
    }
  }

  public void append(String s) {
    out.println(s);
  }

  public void close() {
    if (out != null)
      out.close();

    try {
      if (bw != null)
        bw.close();
    } catch (IOException e) {
      // exception handling left as an exercise for the reader
      System.out.println(e.getMessage());
    }
    try {
      if (fw != null)
        fw.close();
    } catch (IOException e) {
      // exception handling left as an exercise for the reader
      System.out.println(e.getMessage());
    }

  }
}
