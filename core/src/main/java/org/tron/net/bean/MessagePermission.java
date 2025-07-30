package org.tron.net.bean;

/**
 * Compatible with multi-type message notification open state
 */

public class MessagePermission {
    private boolean accountActivityOpenStatus = true;


    /**
     * @return Whether to open the account transfer notification
     */
    public boolean getAccountActivityOpenStatus() {
        return accountActivityOpenStatus;
    }

    public void setAccountActivityOpenStatus(boolean accountActivity) {
        this.accountActivityOpenStatus = accountActivity;
    }


}
