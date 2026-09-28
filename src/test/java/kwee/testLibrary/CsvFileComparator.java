package kwee.testLibrary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

public final class CsvFileComparator {

  private static final String SEP = ";";
  private static final String IGNORED = "<IGNORED>";

  private CsvFileComparator() {
  }

  /**
   * Vergelijkt twee CSV-bestanden met de opgegeven opties.
   */
  public static void assertFilesEqual(Path expected, Path actual, Options options) throws IOException {
    List<String> expLines = normalize(Files.readAllLines(expected), options);
    List<String> actLines = normalize(Files.readAllLines(actual), options);

    if (!expLines.equals(actLines)) {
      throw new AssertionError(buildDiff(expected, actual, expLines, actLines));
    }
  }

  private static List<String> normalize(List<String> lines, Options options) {
    List<String> result = new ArrayList<>(lines.size());
    for (String line : lines) {
      result.add(normalizeLine(line, options));
    }
    return result;
  }

  private static String normalizeLine(String line, Options options) {
    // Commentaar-regels: "# ..." of "## ..."
    if (line.startsWith("#")) {
      return options.ignoreCommentLines ? IGNORED : line;
    }

    String[] cols = line.split(Pattern.quote(SEP), -1);

    // Eventuele regex-normalisaties toepassen op elke kolom
    for (int i = 0; i < cols.length; i++) {
      cols[i] = applyRegexNormalizers(cols[i], options);
    }

    // Kolommen negeren
    for (int idx : options.ignoredColumns) {
      if (idx >= 0 && idx < cols.length) {
        cols[idx] = IGNORED;
      }
    }

    // Floating point kolommen afronden
    for (Map.Entry<Integer, Integer> e : options.roundedColumns.entrySet()) {
      int idx = e.getKey();
      int decimals = e.getValue();
      if (idx >= 0 && idx < cols.length) {
        cols[idx] = round(cols[idx], decimals, options.decimalSeparator);
      }
    }

    return String.join(SEP, cols);
  }

  private static String applyRegexNormalizers(String value, Options options) {
    String result = value;
    for (Pattern p : options.regexNormalizers) {
      result = p.matcher(result).replaceAll(IGNORED);
    }
    return result;
  }

  private static String round(String value, int decimals, char decimalSep) {
    if (value == null || value.isEmpty()) {
      return value;
    }
    // Intern werkt Double.parseDouble met '.', dus vervang komma
    String normalized = value.replace(decimalSep, '.');
    try {
      double d = Double.parseDouble(normalized);
      String formatted = String.format(Locale.ROOT, "%." + decimals + "f", d);
      // Terug naar gewenste decimaalteken
      return decimalSep == '.' ? formatted : formatted.replace('.', decimalSep);
    } catch (NumberFormatException ex) {
      return value; // laat ongemoeid als het geen getal is
    }
  }

  private static String buildDiff(Path expected, Path actual, List<String> expLines, List<String> actLines) {
    StringBuilder sb = new StringBuilder();
    sb.append("CSV files verschillen:\n");
    sb.append("  verwacht: ").append(expected).append("\n");
    sb.append("  werkelijk: ").append(actual).append("\n\n");

    int max = Math.max(expLines.size(), actLines.size());
    int diffs = 0;
    for (int i = 0; i < max; i++) {
      String e = i < expLines.size() ? expLines.get(i) : "<geen regel>";
      String a = i < actLines.size() ? actLines.get(i) : "<geen regel>";
      if (!e.equals(a)) {
        diffs++;
        sb.append("Regel ").append(i + 1).append(":\n").append("  verwacht: ").append(e).append("\n")
            .append("  werkelijk: ").append(a).append("\n");
        if (diffs >= 20) {
          sb.append("... (meer verschillen onderdrukt)\n");
          break;
        }
      }
    }
    return sb.toString();
  }

  // ---------------------------------------------------------------------
  // Options builder
  // ---------------------------------------------------------------------

  public static final class Options {
    private final Set<Integer> ignoredColumns = new TreeSet<>();
    private final Map<Integer, Integer> roundedColumns = new TreeMap<>();
    private final List<Pattern> regexNormalizers = new ArrayList<>();
    private boolean ignoreCommentLines = true;
    private char decimalSeparator = ','; // NL-formaat default

    public static Options builder() {
      return new Options();
    }

    /** Negeer een of meer kolommen (0-based index). */
    public Options ignoreColumns(int... indexes) {
      for (int i : indexes) {
        ignoredColumns.add(i);
      }
      return this;
    }

    /** Rond een kolom af op N decimalen (voor floating point vergelijkingen). */
    public Options roundColumn(int index, int decimals) {
      roundedColumns.put(index, decimals);
      return this;
    }

    /** Voeg een regex toe die overal vervangen wordt door <IGNORED>. */
    public Options normalizePattern(String regex) {
      regexNormalizers.add(Pattern.compile(regex));
      return this;
    }

    /** Negeer "# ..." regels (default: true). */
    public Options ignoreCommentLines(boolean ignore) {
      this.ignoreCommentLines = ignore;
      return this;
    }

    /** Decimaalteken in de CSV (default: ','). */
    public Options decimalSeparator(char sep) {
      this.decimalSeparator = sep;
      return this;
    }
  }
}
