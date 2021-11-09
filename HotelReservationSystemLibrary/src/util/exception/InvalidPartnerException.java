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
public class InvalidPartnerException extends Exception {

    /**
     * Creates a new instance of <code>InvalidPartnerException</code> without
     * detail message.
     */
    public InvalidPartnerException() {
    }

    /**
     * Constructs an instance of <code>InvalidPartnerException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidPartnerException(String msg) {
        super(msg);
    }
}
