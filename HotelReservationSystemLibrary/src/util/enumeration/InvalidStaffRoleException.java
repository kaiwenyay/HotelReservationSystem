/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.enumeration;

/**
 *
 * @author kwpwn
 */
public class InvalidStaffRoleException extends Exception {

    /**
     * Creates a new instance of <code>InvalidAccessRightException</code>
     * without detail message.
     */
    public InvalidStaffRoleException() {
    }

    /**
     * Constructs an instance of <code>InvalidAccessRightException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidStaffRoleException(String msg) {
        super(msg);
    }
}
