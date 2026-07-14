package classes;

import scripts.Globals;
import scripts.Window;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class EntryPanel extends JPanel {
  public JPanel panel = null;
  public EntryData data = null;
  
  public EntryPanel(int index, EntryData entryData) {
    Color rowColor = (index % 2 == 0) ? Color.WHITE : Globals.DARK_WHITE;

    setData(entryData);
    setLayout(new GridLayout(1, 3));
    setBackground(rowColor);
    setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Globals.GRAY));

    JLabel titleLabel = new JLabel(entryData.getTitle());
    JLabel userLabel = new JLabel(entryData.getUsername());
    JLabel passwordLabel = new JLabel(entryData.getPassword());

    titleLabel.setFont(Globals.MIN_FONT);
    userLabel.setFont(Globals.MIN_FONT);
    passwordLabel.setFont(Globals.MIN_FONT);

    titleLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
    userLabel.setBorder(BorderFactory.createEmptyBorder(6, 2, 6, 0));
    passwordLabel.setBorder(BorderFactory.createEmptyBorder(6, 2, 6, 0));

    add(titleLabel);
    add(userLabel);
    add(passwordLabel);

    defineActions();
  }

  public void setData(EntryData newData) {
    this.data = newData;
  }

  private void defineActions() {
    JPanel actionsPanel = new JPanel();
    actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.X_AXIS));
    actionsPanel.setOpaque(false);

    JButton copyButton = new JButton(new ImageIcon("resources/copy.png"));
    styleIconButton(copyButton);
    copyButton.setToolTipText("Copy password");
    copyButton.addActionListener(e -> {
      StringSelection passwordSelection = new StringSelection(this.data.getPassword());
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(passwordSelection, null);

      Window.sendMessageToUser("Copied to clipboard!");
    });

    JButton deleteButton = new JButton(Globals.DELETE_ICON);
    styleIconButton(deleteButton);
    deleteButton.setToolTipText("Delete entry");
    deleteButton.addActionListener(e -> {
      Integer option = Window.getUserConfirmation("Delete selected entry?");
      
      if (option == 0) {
        Globals.dataHash.remove(this.data.getIndex());
        Globals.WINDOW.refreshPasswordEntries();
      }
    });

    actionsPanel.add(copyButton);
    actionsPanel.add(Box.createHorizontalStrut(5));
    actionsPanel.add(deleteButton);
    add(actionsPanel);
  }

  private void styleIconButton(JButton button) {
    button.setFocusable(false);
    button.setOpaque(false);
    button.setContentAreaFilled(false);
    button.setMargin(new Insets(2, 2, 2, 2));

    button.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        button.setContentAreaFilled(true);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        button.setContentAreaFilled(false);
      }
    });
  }
}
