import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class UserManagement {

    private HashMap<String, String> users = new HashMap<>();
    private File userFile = new File("um.oszuser");
    private String password = "v$Eez6!BX&cA3ZJ%Ztw87%MwbpbsAkjz2PaECW&grSq@DGBHisgjJwC@Kq@R4Ufz";
    private SecureEnclave secureEnclave = new SecureEnclave();
    private FileHandler fileHandler = new FileHandler();

    public UserManagement() {
        loadUsers();
    }

    public void addUser(String username, String password) {
        users.put(username, password);
        saveUsers();
    }

    public void removeUser(String username) {
        users.remove(username);
        if (users.isEmpty()) {
            try {
                fileHandler.deleteFile(userFile);
            } catch (IOException e) {
                handleFileError(e);
            }
        } else {
            saveUsers();
        }
    }

    public boolean userExists(String username, String password) {
        String storedPassword = users.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }

    private void loadUsers() {
        try {
            byte[] encryptedData = fileHandler.readFile(userFile);
            String decryptedText = secureEnclave.decrypt(password, encryptedData);
            users = hashMapFromString(decryptedText);
        } catch (FileNotFoundException e) {
            System.err.println("User file not found: " + userFile.getAbsolutePath());
            users.clear(); // Clear the users HashMap
            // Handle the error gracefully (e.g., show an error message to the user)
        } catch (IOException e) {
            handleFileError(e);
        } catch (SecurityException e) {
            handleDecryptionError(e);
        }
    }



    private void saveUsers() {
        String serializedData = hashMapToString(users);
        byte[] encryptedData = secureEnclave.encrypt(password, serializedData);
        try {
            fileHandler.writeFile(userFile, encryptedData);
        } catch (IOException e) {
            handleFileError(e);
        }
    }

    private HashMap<String, String> hashMapFromString(String mapString) {
        HashMap<String, String> newMap = new HashMap<>();
        if (mapString != null) {
            String[] keyValuePairs = mapString.replaceAll("\\{|\\}", "").split(", ");
            for (String pair : keyValuePairs) {
                String[] entry = pair.split("=");
                if (entry.length == 2) {
                    newMap.put(entry[0].trim(), entry[1].trim());
                } else {
                    // Handle invalid format
                    System.err.println("Invalid format in mapString: " + mapString);
                }
            }
        }
        return newMap;
    }

    private String hashMapToString(HashMap<String, String> hashMap) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for (HashMap.Entry<String, String> entry : hashMap.entrySet()) {
            stringBuilder.append(entry.getKey())
                         .append("=")
                         .append(entry.getValue())
                         .append(", ");
        }
        if (!hashMap.isEmpty()) {
            stringBuilder.setLength(stringBuilder.length() - 2); // Remove the trailing comma and space
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    private void handleFileError(IOException e) {
        // Handle file-related errors
        e.printStackTrace();
    }

    private void handleDecryptionError(SecurityException e) {
        // Handle decryption-related errors
        e.printStackTrace();
    }
}
