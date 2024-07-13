package zju.cst.aces.api;

/**
 * Logger is an interface to log messages.
 */
public interface Logger {

    void info(String msg);
    void warn(String msg);
    void error(String msg);
    void debug(String msg);
}
