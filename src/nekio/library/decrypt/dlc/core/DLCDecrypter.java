package nekio.library.decrypt.dlc.core;

/**
 *
 * @author Nekio <nekio@outlook.com>
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.codec.binary.Base64;

public class DLCDecrypter {

    //private static final byte[] pKey = DatatypeConverter.parseHexBinary("9f0abe4c990992bec62c54043d08c26f");
    private static final byte[] pKey = DatatypeConverter.parseHexBinary("2a50ca6ed2677284c3844aa6b177b715");

    public DLCDecrypter() {}

    public static DLCContent decrypt(String dlc) throws DLCException {
        return DLCDecrypter.decryptDLC(dlc);
    }

    public static DLCContent decrypt(File file) throws DLCException {
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader bufferedReader = null;
        try {

            bufferedReader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

        } catch (IOException e) {
            throw new DLCException(e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {}
            }
        }

        return decryptDLC(stringBuffer.toString());
    }

    private static DLCContent decryptDLC(String dlc) throws DLCException {
        String[] rc = getDLCService(dlc);
        String key = decryptRc(rc);
        String c = decryptContent(dlc.substring(0, dlc.length() - 88).trim(), key);

        return new DLCContent(c);
    }

    private static String browser(String urlString, String post) throws DLCException {
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader in = null;
        OutputStreamWriter out = null;
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);

            out = new OutputStreamWriter(
                    conn.getOutputStream());

            out.write(post);
            out.flush();

            in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (MalformedURLException e) {
            throw new DLCException(e);
        } catch (IOException e) {
            throw new DLCException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {}
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {}
            }
        }

        return stringBuffer.toString();
    }

    private static String[] getDLCService(String dlc) throws DLCException {
        String dlcData = null;
        try {
            dlcData = dlc.substring(dlc.length() - 88).trim();
        } catch (Exception e) {
            throw new DLCException("Es handelt sich um keine DLC-Datei", e);
        }

        String content = browser(
                "http://service.jdownloader.org/dlcrypt/service.php",
                "destType=jdtc5&b=last09&p=2009&srcType=dlc&data=" + dlcData + "&v=9.581");

        if (content.isEmpty() || !content.matches("(.*)<rc>(.*)</rc>")) {
            return null;
        }

        String[] v = new String[2];
        Matcher matcher = Pattern.compile("(.*)<rc>(.*)</rc>").matcher(content);

        if (matcher.find()) {
            v[0] = matcher.group(2);
        }

        matcher = Pattern.compile("bla ([0-9a-fA-F]{32})").matcher(content);

        if (matcher.find()) {
            v[1] = matcher.group(1);
        }

        return v;
    }

    private static String decryptRc(String[] rc) throws DLCException {
        byte[] j = Base64.decodeBase64(rc[0]);
        byte[] key = pKey;

        if (rc[1] != null) {
            key = DatatypeConverter.parseHexBinary(rc[1]);
        }

        String r = null;
        try {

            Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
            byte[] o = c.doFinal(j);
            r = new String(Base64.decodeBase64(o)).substring(0, 16);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new DLCException(e);
        } catch (IndexOutOfBoundsException e) {
            throw new DLCException("DLC konnte nicht entschlüsselt werden", e);
        }

        return r;
    }

    private static String decryptContent(String a, String b) throws DLCException {
        byte[] c = Base64.decodeBase64(a);
        byte[] d = b.getBytes();
        String x = null;

        Cipher cip;
        try {
            cip = Cipher.getInstance("AES/CBC/NoPadding");
            cip.init(2, new SecretKeySpec(d, "AES"), new IvParameterSpec(d));
            x = new String(Base64.decodeBase64(cip.doFinal(c)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new DLCException(e);
        }

        return x;
    }
}
