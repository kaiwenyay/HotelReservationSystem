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
public class InvalidEmployeeException extends Exception {

    /**
     * Creates a new instance of <code>InvalidEmployeeException</code> without
     * detail message.
     */
    public InvalidEmployeeException() {
    }

    /**
     * Constructs an instance of <code>InvalidEmployeeException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidEmployeeException(String msg) {
        super(msg);
    }
}
