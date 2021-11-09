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
public class InvalidGuestException extends Exception {

    /**
     * Creates a new instance of <code>InvalidGuestException</code> without
     * detail message.
     */
    public InvalidGuestException() {
    }

    /**
     * Constructs an instance of <code>InvalidGuestException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidGuestException(String msg) {
        super(msg);
    }
}
