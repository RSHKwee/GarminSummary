package kwee.garminSummary.lib;

/**
 * OSM Map Excel, package for Reading and Writing Excel workbook
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import kwee.library.ApplicationMessages;
import kwee.logger.MyLogger;

public class SummaryExcel {
  private static final Logger LOGGER = MyLogger.getLogger();
  private ApplicationMessages bundle = ApplicationMessages.getInstance();

  private int kMstandEindidx = -1;
  private int dayidx = -1;
  private int dateidx = -1;
  private int originidx = -1;
  private int finishidx = -1;
  private int longitudeOriginidx = -1;
  private int latitudeOriginidx = -1;
  private int longitudeFinishidx = -1;
  private int latitudeFinishidx = -1;
  private int addressOriginidx = -1;
  private int addressFinishidx = -1;
  private int distanceidx = -1;
  private int durationidx = -1;
  private int AvrSpeedidx = -1;

  private String C_KMstandeind = "KM stand eind";
  private String C_Day = "Day";
  private String C_Date = "Date";
  private String C_Origin = "Origin";
  private String C_Finish = "Finish";
  private String C_LongitudeOrigin = "Longitude(origin)";
  private String C_LatitudeOrigin = "Latitude(origin)";
  private String C_LongitudeFinish = "Longitude(finish)";
  private String C_LatitudeFinish = "Latitude(finish)";
  private String C_AddressOrigin = "Address origin";
  private String C_AddressFinish = " Address finish";
  private String C_Distance = " Distance (km)";
  private String C_Duration = "Duration";
  private String C_AvrSpeed = "Avr speed (km/h)";

  private int maxCellCount = -1;
  private String m_ExcelFile = "";
  private FormulaEvaluator evaluator;

  private ArrayList<Trip> m_TripArr = new ArrayList<Trip>();

  /**
   * Constructor
   * 
   * @param inputFile Excel file
   */
  public SummaryExcel(String inputFile) {
    m_ExcelFile = inputFile;
  }

  /**
   * Read Excel file
   * 
   * @return List of Memoo content
   */
  public ArrayList<Trip> ReadExcel() {
    // Zet de FileMagic cache aan voor betere detectie
    System.setProperty("org.apache.poi.util.POILogger", "org.apache.poi.util.NullLogger");

    // 1. Open the file
    try (FileInputStream file = new FileInputStream(m_ExcelFile); Workbook workbook = WorkbookFactory.create(file)) {
      Sheet sheet = workbook.getSheetAt(0);

      // 2. Maak een formule-evaluator aan
      evaluator = workbook.getCreationHelper().createFormulaEvaluator();
      evaluator.evaluateAll(); // Eén keer alles berekenen

      int rowIndex = 0;
      for (Row row : sheet) {
        if (rowIndex == 0) {
          // Header
          int cellindex = 0;
          for (Cell cell : row) {
            CellType type = cell.getCellType();
            if (type == CellType.STRING) {
              String str = cell.getStringCellValue();
              if (str.equalsIgnoreCase(C_KMstandeind)) {
                kMstandEindidx = cellindex;
              } else if (str.toLowerCase().contains(C_Day)) {
                dayidx = cellindex;
              } else if (str.toLowerCase().contains(C_Date)) {
                dateidx = cellindex;
              } else if (str.toLowerCase().contains(C_Origin)) {
                originidx = cellindex;
              } else if (str.toLowerCase().contains(C_Finish)) {
                finishidx = cellindex;
              } else if (str.toLowerCase().contains(C_LongitudeOrigin)) {
                longitudeOriginidx = cellindex;
              } else if (str.toLowerCase().contains(C_LatitudeOrigin)) {
                latitudeOriginidx = cellindex;
              } else if (str.toLowerCase().contains(C_LongitudeFinish)) {
                longitudeFinishidx = cellindex;
              } else if (str.toLowerCase().contains(C_LatitudeFinish)) {
                latitudeFinishidx = cellindex;
              } else if (str.toLowerCase().contains(C_AddressOrigin)) {
                addressOriginidx = cellindex;
              } else if (str.toLowerCase().contains(C_AddressFinish)) {
                addressFinishidx = cellindex;
              } else if (str.toLowerCase().contains(C_Distance)) {
                distanceidx = cellindex;
              } else if (str.toLowerCase().contains(C_Duration)) {
                durationidx = cellindex;
              } else if (str.toLowerCase().contains(C_AvrSpeed)) {
                AvrSpeedidx = cellindex;
              } else {
                LOGGER.log(Level.FINE, "cellindex: " + cellindex);
              }
            } else {
              LOGGER.log(Level.FINE, "Ander celltype: " + type.toString());
            }
            maxCellCount = cellindex;
            cellindex++;
          }
          LOGGER.log(Level.FINE, "maxCellCount: " + maxCellCount);
        } else {
          Trip trip = new Trip();
          trip = getTripFromRow(row);
          if (!trip.isEmpty()) {
            m_TripArr.add(trip);
          }
        }
        rowIndex++;
      }
      workbook.close();
      file.close();
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, e.getMessage());
    }
    return m_TripArr;
  }

  // Declare dummy Graphics for use without GUI.
  private JProgressBar m_ProgressBar;
  private JLabel m_Progresslabel;
  private int m_Processed = -1;
  private int m_Number = 0;

  /**
   * Add Longitude and Latitude to Address.
   * 
   * @param outputFile Excel with Longitude and Latitude
   */
  public void WriteExcel(String outputFile) {
    m_ProgressBar = new JProgressBar();
    m_Progresslabel = new JLabel();
    WriteExcel(outputFile, m_ProgressBar, m_Progresslabel);
  }

  /**
   * Add Longitude and Latitude to Address.
   * 
   * @param outputFile      Excel with Longitude and Latitude
   * @param a_ProgressBar   Points to progress bar
   * @param a_Progresslabel Points to progress label for additional information.
   */
  public void WriteExcel(String outputFile, JProgressBar a_ProgressBar, JLabel a_Progresslabel) {
    m_ProgressBar = a_ProgressBar;
    m_Progresslabel = a_Progresslabel;

    m_Processed = -1;
    m_Progresslabel.setVisible(true);
    m_ProgressBar.setVisible(true);
    verwerkProgress();

    try (FileInputStream file = new FileInputStream(m_ExcelFile); Workbook workbook = WorkbookFactory.create(file)) {
      Sheet sheet = workbook.getSheetAt(0);

      // Create a formula-evaluator
      evaluator = workbook.getCreationHelper().createFormulaEvaluator();
      evaluator.evaluateAll(); // Calculate all at once

      int totalRows = sheet.getLastRowNum() + 1;
      LOGGER.log(Level.INFO, "Totaal aantal rijen te verwerken: ~" + totalRows);

      m_Number = totalRows;
      m_ProgressBar.setMaximum(m_Number);

      int rowIndex = 0;
      for (Row row : sheet) {
        if (rowIndex == 0) {
          if (kMstandEindidx == -1) {
            maxCellCount++;
            kMstandEindidx = maxCellCount;
            Cell longCell = row.getCell(kMstandEindidx);
            if (longCell == null) {
              longCell = row.createCell(kMstandEindidx);
            }
            longCell.setCellValue(C_KMstandeind);
          }
          if (dayidx == -1) {
            maxCellCount++;
            dayidx = maxCellCount;
            Cell latCell = row.getCell(dayidx);
            if (latCell == null) {
              latCell = row.createCell(dayidx);
            }
            latCell.setCellValue(C_Day);
          }
          if (dateidx == -1) {
            maxCellCount++;
            dateidx = maxCellCount;
            Cell countryCell = row.getCell(dateidx);
            if (countryCell == null) {
              countryCell = row.createCell(dateidx);
            }
            countryCell.setCellValue(C_Date);
          }
          if (originidx == -1) {
            maxCellCount++;
            originidx = maxCellCount;
            Cell countryCell = row.getCell(originidx);
            if (countryCell == null) {
              countryCell = row.createCell(originidx);
            }
            countryCell.setCellValue(C_Origin);
          }
          if (finishidx == -1) {
            maxCellCount++;
            finishidx = maxCellCount;
            Cell countryCell = row.getCell(finishidx);
            if (countryCell == null) {
              countryCell = row.createCell(finishidx);
            }
            countryCell.setCellValue(C_Finish);
          }
          if (longitudeOriginidx == -1) {
            maxCellCount++;
            longitudeOriginidx = maxCellCount;
            Cell countryCell = row.getCell(longitudeOriginidx);
            if (countryCell == null) {
              countryCell = row.createCell(longitudeOriginidx);
            }
            countryCell.setCellValue(C_LongitudeOrigin);
          }
          if (latitudeOriginidx == -1) {
            maxCellCount++;
            latitudeOriginidx = maxCellCount;
            Cell countryCell = row.getCell(latitudeOriginidx);
            if (countryCell == null) {
              countryCell = row.createCell(latitudeOriginidx);
            }
            countryCell.setCellValue(C_LatitudeOrigin);
          }
          if (longitudeFinishidx == -1) {
            maxCellCount++;
            longitudeFinishidx = maxCellCount;
            Cell countryCell = row.getCell(longitudeFinishidx);
            if (countryCell == null) {
              countryCell = row.createCell(longitudeFinishidx);
            }
            countryCell.setCellValue(C_LongitudeFinish);
          }
          if (latitudeFinishidx == -1) {
            maxCellCount++;
            latitudeFinishidx = maxCellCount;
            Cell countryCell = row.getCell(latitudeFinishidx);
            if (countryCell == null) {
              countryCell = row.createCell(latitudeFinishidx);
            }
            countryCell.setCellValue(C_LatitudeFinish);
          }
          if (addressOriginidx == -1) {
            maxCellCount++;
            addressOriginidx = maxCellCount;
            Cell countryCell = row.getCell(addressOriginidx);
            if (countryCell == null) {
              countryCell = row.createCell(addressOriginidx);
            }
            countryCell.setCellValue(C_AddressOrigin);
          }
          if (addressFinishidx == -1) {
            maxCellCount++;
            addressFinishidx = maxCellCount;
            Cell countryCell = row.getCell(addressFinishidx);
            if (countryCell == null) {
              countryCell = row.createCell(addressFinishidx);
            }
            countryCell.setCellValue(C_AddressFinish);
          }
          if (distanceidx == -1) {
            maxCellCount++;
            distanceidx = maxCellCount;
            Cell countryCell = row.getCell(distanceidx);
            if (countryCell == null) {
              countryCell = row.createCell(distanceidx);
            }
            countryCell.setCellValue(C_Distance);
          }
          if (durationidx == -1) {
            maxCellCount++;
            durationidx = maxCellCount;
            Cell countryCell = row.getCell(durationidx);
            if (countryCell == null) {
              countryCell = row.createCell(durationidx);
            }
            countryCell.setCellValue(C_Duration);
          }
          if (AvrSpeedidx == -1) {
            maxCellCount++;
            AvrSpeedidx = maxCellCount;
            Cell countryCell = row.getCell(AvrSpeedidx);
            if (countryCell == null) {
              countryCell = row.createCell(AvrSpeedidx);
            }
            countryCell.setCellValue(C_AvrSpeed);
          }
        } else {
          Trip trip = new Trip();
          trip = getTripFromRow(row);
        }
        rowIndex++;
        verwerkProgress();
      }

      // Store in file
      try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
        workbook.write(outputStream);
        LOGGER.log(Level.INFO, "Bestand opgeslagen als: " + outputFile);
      }

      // Close file and input stream
      workbook.close();
      file.close();
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, e.getMessage());
    }
  }

  // ========= Private functions ============================
  /**
   * Fill structure Memo content.
   * 
   * @param row Excel row to convert to Memo content.
   * @return Memo content
   */
  private Trip getTripFromRow(Row row) {
    Trip trip = new Trip();
    try {
      if (kMstandEindidx != -1) {
        Cell l_cell = row.getCell(kMstandEindidx);
        if (l_cell != null) {
          trip.setKMstandEind(getCelValue(l_cell));
        }
      }
      if (dayidx != -1) {
        Cell l_cell = row.getCell(dayidx);
        if (l_cell != null) {
          trip.setDay(getCelValue(l_cell));
        }
      }
      if (dateidx != -1) {
        Cell l_cell = row.getCell(dateidx);
        if (l_cell != null) {
          trip.setDate(getCelValue(l_cell));
        }
      }
      if (originidx != -1) {
        Cell l_cell = row.getCell(originidx);
        if (l_cell != null) {
          trip.setOrigin(getCelValue(l_cell));
        }
      }
      if (finishidx != -1) {
        Cell l_cell = row.getCell(finishidx);
        if (l_cell != null) {
          trip.setFinish(getCelValue(l_cell));
        }
      }
      if (longitudeOriginidx != -1) {
        Cell l_cell = row.getCell(longitudeOriginidx);
        if (l_cell != null) {
          trip.setLongitudeOrigin(getCelValue(l_cell));
        }
      }
      if (latitudeOriginidx != -1) {
        Cell l_cell = row.getCell(latitudeOriginidx);
        if (l_cell != null) {
          trip.setLatitudeOrigin(getCelValue(l_cell));
        }
      }
      if (longitudeFinishidx != -1) {
        Cell l_cell = row.getCell(longitudeFinishidx);
        if (l_cell != null) {
          trip.setLongitudeFinish(getCelValue(l_cell));
        }
      }
      if (latitudeFinishidx != -1) {
        Cell l_cell = row.getCell(latitudeFinishidx);
        if (l_cell != null) {
          trip.setLatitudeFinish(getCelValue(l_cell));
        }
      }
      if (addressOriginidx != -1) {
        Cell l_cell = row.getCell(addressOriginidx);
        if (l_cell != null) {
          trip.setAddressOrigin(getCelValue(l_cell));
        }
      }
      if (addressFinishidx != -1) {
        Cell l_cell = row.getCell(addressFinishidx);
        if (l_cell != null) {
          trip.setAddressFinish(getCelValue(l_cell));
        }
      }
      if (distanceidx != -1) {
        Cell l_cell = row.getCell(distanceidx);
        if (l_cell != null) {
          trip.setDistance(getCelValue(l_cell));
        }
      }
      if (durationidx != -1) {
        Cell l_cell = row.getCell(durationidx);
        if (l_cell != null) {
          trip.setDuration(getCelValue(l_cell));
        }
      }
      if (AvrSpeedidx != -1) {
        Cell l_cell = row.getCell(AvrSpeedidx);
        if (l_cell != null) {
          trip.setAvrSpeed(getCelValue(l_cell));
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e.getMessage());
    }
    return trip;
  }

  /**
   * Handle cell
   * 
   * @param a_cell Cell info
   * @return Result as String
   */
  private String getCelValue(Cell a_cell) {
    String str = "";
    CellType type = a_cell.getCellType();
    if (type == CellType.STRING) {
      str = a_cell.getStringCellValue();
    } else if (type == CellType.NUMERIC) {
      double huisnr = a_cell.getNumericCellValue();
      long ihuisnr = Math.round(huisnr);
      str = Long.toString(ihuisnr);
    } else if (type == CellType.BOOLEAN) {
      boolean bstat = a_cell.getBooleanCellValue();
      str = Boolean.toString(bstat);
    } else if (type == CellType.FORMULA) {

      // Handle formula
      LOGGER.log(Level.FINE, "Formule: " + a_cell.getCellFormula());
      try {
        CellValue cellValue = evaluator.evaluate(a_cell);
        switch (cellValue.getCellType()) {
        case NUMERIC:
          Double huisnr = a_cell.getNumericCellValue();
          long ihuisnr = Math.round(huisnr);
          str = Long.toString(ihuisnr);
          break;
        case STRING:
          str = cellValue.getStringValue();
          break;
        case BOOLEAN:
          boolean bstat = a_cell.getBooleanCellValue();
          str = Boolean.toString(bstat);
          break;
        case ERROR:
          byte bstr = cellValue.getErrorValue();
          str = Byte.toString(bstr);
          break;
        default:
          str = "";
        }
      } catch (Exception e) {
        str = "";
      }
    }
    return str;
  }

  /**
   * Display progress processed files.
   */
  private void verwerkProgress() {
    m_Progresslabel.setVisible(true);
    m_ProgressBar.setVisible(true);
    m_Processed++;
    try {
      m_ProgressBar.setValue(m_Processed);
      m_ProgressBar.paintImmediately(m_ProgressBar.getVisibleRect());
      Double v_prog = ((double) m_Processed / (double) m_Number) * 100;
      int v_iprog = v_prog.intValue();

      int remain = m_Number - m_Processed;
      int iMin = remain / 60;
      int iSec = remain - (iMin * 60);
      String tijd = String.format("%02d:%02d", iMin, iSec);

      m_Progresslabel.setText(bundle.getMessage("Progress", v_iprog, m_Processed, m_Number, tijd));
      m_Progresslabel.paintImmediately(m_Progresslabel.getVisibleRect());
    } catch (Exception e) {
      // Do nothing
    }
  }
}
