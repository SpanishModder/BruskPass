package classes;

public class EntryData {
  private String username = null;
  private String password = null;
  private String title = null; // like the website, dominion or usage (ex: GitHub, Facebook, Instagram, etc...)
  private Integer index = null;

  public EntryData(String user, String password, String title, Integer index) {
    setUsername(user);
    setPassword(password);
    setTitle(title);
    setIndex(index);
  }

  // setters
  public void setUsername(String newUsername) {
    this.username = newUsername;
  }
  public void setPassword(String newPassword) {
    this.password = newPassword;
  }
  public void setTitle(String newTitle) {
    this.title = newTitle;
  }
  public void setIndex(Integer newIndex) {
    this.index = newIndex;
  }

  // getters
  public String getUsername() {
    return this.username;
  }
  public String getPassword() {
    return this.password;
  }
  public String getTitle() {
    return this.title;
  }
  public Integer getIndex() {
    return this.index;
  }
}
