package nekio.library.decrypt.dlc.core;

/**
 *
 * @author Nekio <nekio@outlook.com>
 */

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DLCContent {

    private ArrayList<WebResource> webResource = new ArrayList<>();
    private String generatorApp;
    private String generatorVersion;
    private String generatorUrl;
    private String dlcXmlVersion;
    private String dlcPackage;

    public DLCContent(String xml) throws DLCException {
        parseXML(xml);
    }

    public ArrayList<WebResource> getDlcWebResources() {
        return webResource;
    }

    private void parseXML(String xml) throws DLCException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setValidating(false);

        DocumentBuilder builder;
        try {

            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));

            NodeList cNodes = doc.getFirstChild().getChildNodes();

            for (int i = 0, c = cNodes.getLength(); i < c; i++) {
                Node n = cNodes.item(i);

                if (n.getNodeName().equalsIgnoreCase("header")) {
                    NodeList header = n.getChildNodes();

                    for (int j = 0, c2 = header.getLength(); j < c2; j++) {
                        if (header.item(j).getNodeName().equalsIgnoreCase("generator")) {
                            NodeList generator = header.item(j).getChildNodes();

                            for (int k = 0, c3 = generator.getLength(); k < c3; k++) {
                                Node generatorAttr = generator.item(k);

                                if (generatorAttr.getNodeName().equalsIgnoreCase("app")) {
                                    generatorApp = new String(Base64.decodeBase64(generatorAttr.getTextContent()));
                                } else if (generatorAttr.getNodeName().equalsIgnoreCase("version")) {
                                    generatorVersion = new String(Base64.decodeBase64(generatorAttr.getTextContent()));
                                } else if (generatorAttr.getNodeName().equalsIgnoreCase("url")) {
                                    generatorUrl = new String(Base64.decodeBase64(generatorAttr.getTextContent()));
                                }
                            }
                        } else if (header.item(j).getNodeName().equalsIgnoreCase("dlcxmlversion")) {
                            dlcXmlVersion = new String(Base64.decodeBase64(header.item(j).getTextContent()));
                        }
                    }
                } else if (n.getNodeName().equalsIgnoreCase("content")) {
                    Node _package = doc.getElementsByTagName("package").item(0);
                    NodeList content = _package.getChildNodes();

                    dlcPackage = new String(Base64.decodeBase64(_package.getAttributes().getNamedItem("name").getNodeValue()));

                    for (int j = 0, c2 = content.getLength(); j < c2; j++) {
                        if (content.item(j).getNodeName().equalsIgnoreCase("file")) {
                            NodeList file = content.item(j).getChildNodes();
                            WebResource resource = new WebResource();

                            for (int k = 0, c3 = file.getLength(); k < c3; k++) {
                                Node fileNode = file.item(k);

                                if (fileNode.getNodeName().equalsIgnoreCase("url")) {
                                    resource.setUrl(new String(Base64.decodeBase64(fileNode.getTextContent())));
                                    webResource.add(resource);
                                } else if (fileNode.getNodeName().equalsIgnoreCase("filename")) {
                                    resource.setFilename(new String(Base64.decodeBase64(fileNode.getTextContent())));
                                }
                            }
                        }
                    }
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new DLCException(e);
        }
    }

    public String getGeneratorApp() {
        return generatorApp;
    }

    public String getGeneratorVersion() {
        return generatorVersion;
    }

    public String getGeneratorUrl() {
        return generatorUrl;
    }

    public String getDlcXmlVersion() {
        return dlcXmlVersion;
    }

    public String getDlcPackage() {
        return dlcPackage;
    }
}
