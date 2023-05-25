package library;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author james
 */
public class OutputToLoggerReader {
  public String getReadOut(String a_cmd) throws IOException, InterruptedException {
    Runtime rt = Runtime.getRuntime();

    Process p;
    p = rt.exec(a_cmd);

    BufferedReader processOutput = new BufferedReader(new InputStreamReader(p.getInputStream()), 500000);
    BufferedReader errorOutput = new BufferedReader(new InputStreamReader(p.getErrorStream()), 500000);

    ReadThread r = new ReadThread(processOutput);
    Thread th = new Thread(r);
    th.start();
    ReadThread e = new ReadThread(errorOutput);
    Thread the = new Thread(r);
    the.start();

    p.waitFor();
    r.stop();
    e.stop();
    String s = r.res + "\n" + e.res;

    p.destroy();
    th.join();
    return s;
  }

  public String getReadOut(String[] params) throws IOException, InterruptedException {
    Process p = Runtime.getRuntime().exec(params);

    BufferedReader processOutput = new BufferedReader(new InputStreamReader(p.getInputStream()), 500000);
    BufferedReader errorOutput = new BufferedReader(new InputStreamReader(p.getErrorStream()), 500000);

    ReadThread r = new ReadThread(processOutput);
    Thread th = new Thread(r);
    th.start();
    ReadThread e = new ReadThread(errorOutput);
    Thread the = new Thread(r);
    the.start();

    p.waitFor();
    r.stop();
    e.stop();
    String s = r.res + "\n" + e.res;

    p.destroy();
    th.join();
    return s;
  }

  public class ReadThread implements Runnable {
    BufferedReader reader;
    char[] buf = new char[100000];
    String res = "";
    boolean stop;

    public ReadThread(BufferedReader reader) {
      this.reader = reader;
      stop = false;
    }

    @Override
    public void run() {
      res = "";

      while (!stop) {
        try {
          reader.read(buf);
          res += new String(buf);
        } catch (IOException ex) {
          Logger.getLogger(OutputToLoggerReader.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }

    public void stop() {
      stop = true;
    }
  }
}