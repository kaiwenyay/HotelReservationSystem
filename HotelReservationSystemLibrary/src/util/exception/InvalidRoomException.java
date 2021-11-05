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
public class InvalidRoomException extends Exception {

    /**
     * Creates a new instance of <code>InvalidRoomException</code> without
     * detail message.
     */
    public InvalidRoomException() {
    }

    /**
     * Constructs an instance of <code>InvalidRoomException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidRoomException(String msg) {
        super(msg);
    }
}
