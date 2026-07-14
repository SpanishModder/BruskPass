package scripts;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import classes.EntryData;

public class Globals {
  private static String separator = "///;///";

  public static Window WINDOW = null;
  public static HashMap<Integer, EntryData> dataHash = new HashMap<>();
  //public static JFrame mainFrame = null;

  public static final Color DARK_WHITE = new Color(245, 245, 245);
  public static final Color LIGHT_GRAY = new Color(238, 238, 238);
  public static final Color GRAY = new Color(220,220,220);

  public static final Font MIN_FONT = new Font("Segoe UI", Font.PLAIN, 12);
  public static final Font MID_FONT = new Font("Segoe UI", Font.BOLD, 16);
  public static final Font BIG_FONT = new Font("Segoe UI", Font.PLAIN, 16);

  public static final ImageIcon DELETE_ICON = new ImageIcon("resources/delete.png");

  public static void init() {
    WINDOW = new Window();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        Encripter.saveIntoVault();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }));
  }

  public static String toEntryString(HashMap<Integer, EntryData> map) {
    String result = "";

    for (Map.Entry<Integer, EntryData> entry : map.entrySet()) {
      EntryData data = entry.getValue();
      String title = data.getTitle();
      String username = data.getUsername();
      String password = data.getPassword();

      result = result + title + separator + username + separator + password + separator;
    }

    return result;
  }

  // style: (username)(fieldSeparator)(password)(entrySeparator) ...
  public static HashMap<Integer, EntryData> toHashMap(String decoded) {
    HashMap<Integer, EntryData> result = new HashMap<>();

    String[] parts = decoded.split(separator);

    if (parts.length == 0 || parts.length % 3 != 0) {return result;}

    for (int i = 0; i < parts.length; i += 3) {
      result.put(i, new EntryData(parts[i + 1], parts[i + 2], parts[i], i));
    }

    return result;
  }
}
