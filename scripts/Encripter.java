package scripts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import classes.EntryData;

public class Encripter {
  private static byte[] salt = null; // random (16) byte table
  private static String masterPassword = null;
  private static int IV_LENGTH = 12;

  private static Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
    return Cipher.getInstance("AES/GCM/NoPadding");
  }

  public static void setMasterPassword(String password) {
    masterPassword = password;
  }

  public static void setSalt(byte[] newSalt) {
    salt = newSalt; 
  }

  public static byte[] getAESKey() throws Exception {
    if (masterPassword == null) {throw new Exception();}
    
    char[] pass = masterPassword.toCharArray();
    
    PBEKeySpec spec = new PBEKeySpec(pass, salt, 60000, 256); // encryption
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

    return factory.generateSecret(spec).getEncoded();
  }

  public static byte[] encode(String str) throws Exception {
    Cipher cipher = getCipher();
    
    byte[] iv = new byte[IV_LENGTH];
    byte[] key = getAESKey();
    new SecureRandom().nextBytes(iv);

    GCMParameterSpec spec = new GCMParameterSpec(128, iv);
    SecretKey secretKey = new SecretKeySpec(key, "AES");

    cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

    // creating the result byte table
    byte[] encoded = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));
    byte[] result = new byte[iv.length + encoded.length];

    System.arraycopy(iv, 0, result, 0, iv.length);
    System.arraycopy(encoded, 0, result, iv.length, encoded.length);

    return result;
  }

  public static String decode(byte[] data) throws Exception {
    byte[] key = getAESKey();
    byte[] iv = new byte[IV_LENGTH];
    byte[] encrypted = new byte[data.length - IV_LENGTH];

    System.arraycopy(data, 0, iv, 0, IV_LENGTH);
    System.arraycopy(data, IV_LENGTH, encrypted, 0, encrypted.length);

    GCMParameterSpec spec = new GCMParameterSpec(128, iv);
    SecretKey secretKey = new SecretKeySpec(key, "AES");
    Cipher cipher = getCipher();

    cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

    byte[] decrypted = null;
    try {
      decrypted = cipher.doFinal(encrypted);
    } catch (Exception e) {
      e.printStackTrace();
      Window.sendMessageToUser("Wrong Password");
      System.exit(0);

      return "";
    }

    return new String(decrypted, StandardCharsets.UTF_8);
  }

  // inits the currently EXISTING vault
  public static void initVault() throws IOException {
    File vault = new File("data/vault.dat");
    byte[] contents = Files.readAllBytes(vault.toPath());
    String masterPassword = Window.getUserInput("Enter the Master Password");

    if (masterPassword == null || masterPassword.equals("")) {
      System.exit(0);
    }

    setSalt(Arrays.copyOfRange(contents, 0, 16));
    setMasterPassword(masterPassword);
  }

  // creates and inits a NEW vault
  public static void setupVault() throws Exception {
    File vault = new File("data/vault.dat");

    String mstPass = Window.getUserInput("Create Master Password");

    if (mstPass == null || mstPass.equals("")) {
      System.exit(0); // exits the program
    }

    if (!vault.exists()) { vault.createNewFile(); }

    byte[] newSalt = new byte[16];
    new SecureRandom().nextBytes(newSalt);

    setMasterPassword(mstPass);
    setSalt(newSalt);
  }

  public static void saveIntoVault() throws Exception {
    byte[] iv_and_data = encode(Globals.toEntryString(Globals.dataHash));

    FileOutputStream out = new FileOutputStream("data/vault.dat");
    out.write(salt);
    out.write(iv_and_data);
    
    out.close();
  }

  public static HashMap<Integer, EntryData> getVaultHash() {
    HashMap<Integer, EntryData> vaultHash = new HashMap<>();

    byte[] file = getVaultBytes();

    if (file == null || file.length == 0) {return vaultHash;}

    byte[] salt = Arrays.copyOfRange(file, 0, 16);
    byte[] data = Arrays.copyOfRange(file, 16, file.length);

    setSalt(salt);

    try {
      String decoded = decode(data);
      vaultHash = Globals.toHashMap(decoded);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return vaultHash;
  }

  private static byte[] getVaultBytes() {
    File vault = new File("data/vault.dat");
    byte[] result = null;
    
    try {
      result = Files.readAllBytes(vault.toPath());
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return result;
  }
}
