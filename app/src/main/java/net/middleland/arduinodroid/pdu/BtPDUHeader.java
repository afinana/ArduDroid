package net.middleland.arduinodroid.pdu;

/**
 * Created by Antonio on 04/02/2018.
 */

public class BtPDUHeader {


    private byte commandLength;
    private byte commandId;
    private byte status;
    private byte sequence;

    public byte getCommandLength() {
        return commandLength;
    }

    public void setCommandLength(byte commandLength) {
        this.commandLength = commandLength;
    }

    public byte getCommandId() {
        return commandId;
    }

    public void setCommandId(byte commandId) {
        this.commandId = commandId;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public byte getSequence() {
        return sequence;
    }

    public void setSequence(byte sequence) {
        this.sequence = sequence;
    }
}
