import javax.crypto.Cipher;

public class CryptoExample {
    public static void main(String[] args) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        System.out.println("Cipher instance created successfully: " + cipher.getAlgorithm());
    }
}
