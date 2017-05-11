package nekio.library.decrypt.dlc.core;

/**
 *
 * @author Nekio <nekio@outlook.com>
 */

public class WebResource {
    private String url;
    private String filename;

    public WebResource() {}

    public WebResource(String url, String filename) {
        this.url = url;
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
