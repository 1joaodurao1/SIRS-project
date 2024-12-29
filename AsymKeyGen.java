import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Arrays;
import java.util.List;

public class AsymKeyGen {
    

    private static final String ASYM_ALGO = "RSA";

	/** Asymmetric cryptography key size. */
	private static final int ASYM_KEY_SIZE = 2048;

    /** Symmetric cryptography algorithm. */

    public static void generateKeys(String publicKeyPath, String privateKeyPath, List<String> users) 
    throws Exception {

       // generate key pair
       KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ASYM_ALGO);
       keyGen.initialize(ASYM_KEY_SIZE);
       KeyPair key = keyGen.generateKeyPair();

       byte[] pubEncoded = key.getPublic().getEncoded();
       writeFile(publicKeyPath, pubEncoded);
       
       byte[] privEncoded = key.getPrivate().getEncoded();
       writeFile(privateKeyPath, privEncoded);
    }

    private static void writeFile(String path, byte[] content) throws FileNotFoundException, IOException {
		File file = new File(path);
		if (!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(content);
		fos.close();
	}

    public static void main(String[] args) {

        String public_path = getModuleBasePath(Integer.parseInt(args[0])) + "/resources/public";
        String private_path = getModuleBasePath(Integer.parseInt(args[0])) + "/resources/private";
        List<String> users = Arrays.asList("owner", "mechanic", "user" , "server");
        try {
            for (String user : users) {
                generateKeys(public_path + "/" + user + ".pubkey", private_path + "/" + user + ".privkey", users);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getModuleBasePath(Integer moduleId) {
        // Determine the base path of the module
        // Adjust this method to correctly locate the base path of your module
        if ( moduleId == 1 ) {
            return System.getProperty("user.dir") + "/secure-document/src/java";
        } else if ( moduleId == 2 ) {
            return System.getProperty("user.dir") + "/application-server/src/main/java";
        }
        else {
            return System.getProperty("user.dir") + "/client/src/main/";
        }

    }

}
