package com.lumem.hgest.model.Util;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;

public class Msg {
    private String body;
    private String code;

    public Msg(String body, String code) {
        this.body = body;
        this.code = code;
    }


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
