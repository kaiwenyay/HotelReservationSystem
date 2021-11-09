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
public class InvalidRoomTypeException extends Exception {

    /**
     * Creates a new instance of <code>InvalidRoomTypeException</code> without
     * detail message.
     */
    public InvalidRoomTypeException() {
    }

    /**
     * Constructs an instance of <code>InvalidRoomTypeException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidRoomTypeException(String msg) {
        super(msg);
    }
}
