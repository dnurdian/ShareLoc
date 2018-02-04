package com.kenjin.shareloc.model;

import java.io.Serializable;

/**
 * Created by kenjin on 31/12/17.
 */

public class Result implements Serializable {

    boolean Status;
    String Pesan;

    public boolean isStatus() {
        return Status;
    }

    public void setStatus(boolean status) {
        Status = status;
    }

    public String getPesan() {
        return Pesan;
    }

    public void setPesan(String pesan) {
        Pesan = pesan;
    }
}
