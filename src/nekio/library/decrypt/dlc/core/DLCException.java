package nekio.library.decrypt.dlc.core;

/**
 *
 * @author Nekio <nekio@outlook.com>
 */

public class DLCException extends Exception {

    private static final long serialVersionUID = 1902475548077544031L;

    public DLCException() {}

    public DLCException(String msg) {
        super(msg);
    }

    public DLCException(Throwable arg0) {
        super(arg0);
    }

    public DLCException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public DLCException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }
}
