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
public class InvalidRoomRateException extends Exception {

    /**
     * Creates a new instance of <code>InvalidRoomRateException</code> without
     * detail message.
     */
    public InvalidRoomRateException() {
    }

    /**
     * Constructs an instance of <code>InvalidRoomRateException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidRoomRateException(String msg) {
        super(msg);
    }
}
