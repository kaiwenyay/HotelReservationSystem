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
public class UpdateRoomRateException extends Exception {

    /**
     * Creates a new instance of <code>UpdateRoomRateException</code> without
     * detail message.
     */
    public UpdateRoomRateException() {
    }

    /**
     * Constructs an instance of <code>UpdateRoomRateException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateRoomRateException(String msg) {
        super(msg);
    }
}
