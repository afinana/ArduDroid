package net.middleland.arduinodroid.pdu;

import java.nio.charset.Charset;

/**
 * Created by Antonio on 04/02/2018.
 */
public class BtPDU {

    private  BtPDUHeader header;
    private  byte[] body;

    public BtPDUHeader getHeader() {
        return header;
    }

    public void setHeader(BtPDUHeader header) {
        this.header = header;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setBody(String body) {
        this.body = body.getBytes(Charset.forName("ASCII"));
    }
}
