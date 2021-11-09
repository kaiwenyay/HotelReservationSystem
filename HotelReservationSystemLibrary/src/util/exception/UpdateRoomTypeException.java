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
public class UpdateRoomTypeException extends Exception {

    /**
     * Creates a new instance of <code>UpdateRoomTypeException</code> without
     * detail message.
     */
    public UpdateRoomTypeException() {
    }

    /**
     * Constructs an instance of <code>UpdateRoomTypeException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateRoomTypeException(String msg) {
        super(msg);
    }
}
