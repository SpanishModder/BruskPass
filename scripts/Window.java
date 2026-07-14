package scripts;

import classes.EntryData;
import classes.EntryPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Window {
  private final int WIDTH = 800;
  private final int HEIGHT = 500;

  JFrame mainFrame = new JFrame();
  JPanel topPanel = new JPanel();
  JPanel optionsPanel = new JPanel(new BorderLayout(8, 8));
  JPanel passwordsPanel = new JPanel();

  public Window() {
    init();

    File dataFolder = new File("data");
    if (!dataFolder.exists()) {
      dataFolder.mkdir();
    }

    if (isFirstStart()) {
      try {
        Encripter.setupVault();
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      try {
        Encripter.initVault();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    Globals.dataHash = Encripter.getVaultHash();
    setupGui();
    
    mainFrame.setVisible(true);
  }

  public boolean isFirstStart() {
    File file = new File("data/vault.dat");
    return !file.exists();
  }

  public void init() {
    mainFrame.setTitle("BruskPass");
    mainFrame.setSize(WIDTH, HEIGHT);
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setLocationRelativeTo(null);
    mainFrame.setLayout(new BorderLayout());
  }

  public static String getUserInput(String msg) {
    return JOptionPane.showInputDialog(msg);
  }

  public static void sendMessageToUser(String msg) {
    JOptionPane.showMessageDialog(null, msg);
  }

  public static Integer getUserConfirmation(String msg) {
    return JOptionPane.showConfirmDialog(null, msg, "Confirm deletion", JOptionPane.YES_NO_OPTION, 0, Globals.DELETE_ICON);
  }

  public JFrame getMainFrame() {
    return mainFrame;
  }

  public void setupGui() {
    JButton addPasswordButton = new JButton("Add Password");
    JTextField searchField = new JTextField();
    JPanel tableHeader = new JPanel();

    JLabel titleHeader = new JLabel("Title");
    JLabel userHeader = new JLabel("Username");
    JLabel passHeader = new JLabel("Password");
    JLabel actionsHeader = new JLabel("Actions");

    tableHeader.setBackground(Color.WHITE);
    tableHeader.setPreferredSize(new Dimension(WIDTH, 35));
    tableHeader.setLayout(new GridLayout(1, 4));
    tableHeader.setBorder(new LineBorder(Globals.LIGHT_GRAY, 2));

    titleHeader.setFont(Globals.MID_FONT);
    titleHeader.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

    userHeader.setFont(Globals.MID_FONT);

    passHeader.setFont(Globals.MID_FONT);

    actionsHeader.setFont(Globals.MID_FONT);

    optionsPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    optionsPanel.setBackground(Globals.LIGHT_GRAY);

    addPasswordButton.setFocusable(false);
    addPasswordButton.setPreferredSize(new Dimension(150, 25));
    addPasswordButton.setFont(Globals.MIN_FONT);

    searchField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        onTextChanged();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        onTextChanged();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        onTextChanged();
      }

      private void onTextChanged() {
        for (Component comp : passwordsPanel.getComponents()) {
          if (!(comp instanceof EntryPanel)) { continue; }

          EntryPanel entry = (EntryPanel) comp;
          String title = entry.data.getTitle().toLowerCase();

          if (!title.contains(searchField.getText().toLowerCase())) {
            entry.setVisible(false);
          } else {
            entry.setVisible(true);
          }
        }
      }
    });

    addPasswordButton.addActionListener(e -> {
      String title = getUserInput("Enter the entry title (identifier)");
      String username = getUserInput("Enter the username");
      String password = getUserInput("Enter the password");

      if (username == null || password == null || title == null) {
        sendMessageToUser("Invalid tokens");
        return;
      }

      Integer index = Globals.dataHash.size();
      Globals.dataHash.put(index, new EntryData(username, password, title, index));
      refreshPasswordEntries();
    });

    passwordsPanel.setLayout(new BoxLayout(passwordsPanel, BoxLayout.Y_AXIS));
    passwordsPanel.setBackground(Color.WHITE);

    refreshPasswordEntries(); // creates entries and removes the old ones

    JScrollPane scrollPane = new JScrollPane(passwordsPanel);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());

    optionsPanel.add(searchField, BorderLayout.CENTER);
    optionsPanel.add(addPasswordButton, BorderLayout.EAST);

    tableHeader.add(titleHeader);
    tableHeader.add(userHeader);
    tableHeader.add(passHeader);
    tableHeader.add(actionsHeader);

    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
    topPanel.add(optionsPanel);
    topPanel.add(tableHeader);

    mainFrame.add(topPanel, BorderLayout.NORTH);
    mainFrame.add(scrollPane, BorderLayout.CENTER);
  }

  public void refreshPasswordEntries() {
    passwordsPanel.removeAll();

    int index = 0;
    for (Map.Entry<Integer, EntryData> entry : Globals.dataHash.entrySet()) {
      EntryPanel entryPanel = new EntryPanel(index, entry.getValue());

      entryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
      passwordsPanel.add(entryPanel);

      index++;
    }

    passwordsPanel.revalidate();
    passwordsPanel.repaint();
  }
}
