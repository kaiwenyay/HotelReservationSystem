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
public class UpdateRoomException extends Exception {

    /**
     * Creates a new instance of <code>UpdateRoomException</code> without detail
     * message.
     */
    public UpdateRoomException() {
    }

    /**
     * Constructs an instance of <code>UpdateRoomException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateRoomException(String msg) {
        super(msg);
    }
}
