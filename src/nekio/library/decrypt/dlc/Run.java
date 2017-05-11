package nekio.library.decrypt.dlc;

import java.io.File;
import java.util.ArrayList;
import nekio.library.decrypt.dlc.core.DLCContent;
import nekio.library.decrypt.dlc.core.DLCDecrypter;
import nekio.library.decrypt.dlc.core.DLCException;
import nekio.library.decrypt.dlc.core.WebResource;

/**
 *
 * @author Nekio <nekio@outlook.com>
 */

public class Run {
    public static void main(String[] args) {
        try {
            String mainPath = "C:\\apps\\73w9\\D\\dlc\\";
            String dlcFile = "l0s s3cr3tos d3 la historia" + ".dlc";
            DLCContent dlcContent = DLCDecrypter.decrypt(new File(mainPath + dlcFile));

            //DLCContent dlcContent = DLCDecrypter.decrypt("DLC String");
            System.out.println("Generator DLC: " + dlcContent.getDlcPackage());
            System.out.println("Generator APP: " + dlcContent.getGeneratorApp());
            System.out.println("Generator Url: " + dlcContent.getGeneratorUrl());
            System.out.println("Generator Version: " + dlcContent.getGeneratorVersion() + "\n");
            System.out.println("Xml Version: " + dlcContent.getDlcXmlVersion() + "\n");
            System.out.println("Reading DLC File...\n");

            ArrayList<WebResource> dlcFiles = dlcContent.getDlcWebResources();

            for (int i = 0, c = dlcFiles.size(); i < c; i++) {
                System.out.println("URL: " + dlcFiles.get(i).getUrl());
                System.out.println("Filename: " + dlcFiles.get(i).getFilename() + " \n");
            }

        } catch (DLCException e) {
            e.printStackTrace();
        }
    }
}
