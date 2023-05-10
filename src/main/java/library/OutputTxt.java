package library;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * 
 * @author kweers
 *
 */
public class OutputTxt {
  private String m_Header = "";
  private String m_Footer = "";
  private BufferedWriter m_writer;
  private String m_comment = "#";

  public OutputTxt(String a_OutputFile, boolean a_Append) throws IOException {
    OpenTxtFile(a_OutputFile, a_Append);
  }

  public OutputTxt(String a_OutputFile) throws IOException {
    OpenTxtFile(a_OutputFile, false);
  }

  public void SetFooter(String a_Footer) {
    m_Footer = m_comment + a_Footer;
  }

  public void SetComment(String a_Comment) {
    m_comment = a_Comment;
  }

  public void SetHeader(String a_Header) {
    m_Header = m_comment + a_Header;
  }

  public void Close() throws IOException {
    LocalDate today = LocalDate.now();
    LocalTime time = LocalTime.now();
    m_writer.write(m_Footer + "\r\n");
    m_writer.write(m_comment + " Gegenereerd op " + today.toString() + " " + time.toString() + "\r\n");

    m_writer.close();
  }

  public void Schrijf(ArrayList<String> a_Regels) {
    if (!m_Header.isEmpty()) {
      try {
        m_writer.write(m_Header + "\r\n");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    a_Regels.forEach(v_Regel -> {
      try {
        m_writer.write(v_Regel + "\r\n");
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  void OpenTxtFile(String a_OutputFile, boolean a_Append) throws IOException {
    Path outpath = Paths.get(a_OutputFile);
    Charset charset = Charset.forName("ISO-8859-1");
    if (a_Append) {
      OpenOption[] Options = { StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND };
      m_writer = Files.newBufferedWriter(outpath, charset, Options);
    } else {
      m_writer = Files.newBufferedWriter(outpath, charset);
    }
  }
}
