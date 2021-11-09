/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author kwpwn
 */
public class InvalidReportException extends Exception {

    /**
     * Creates a new instance of <code>InvalidReportException</code> without
     * detail message.
     */
    public InvalidReportException() {
    }

    /**
     * Constructs an instance of <code>InvalidReportException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidReportException(String msg) {
        super(msg);
    }
}
