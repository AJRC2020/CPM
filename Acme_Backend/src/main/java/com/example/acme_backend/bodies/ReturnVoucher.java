package com.example.acme_backend.bodies;

import java.sql.Date;

public class ReturnVoucher {
    public Boolean emitted;
    public Boolean used;
    public Date date;
    public String user;
    public String uuid;

    public ReturnVoucher(Boolean emitted, Boolean used, Date date, String user, String uuid) {
        this.emitted = emitted;
        this.used = used;
        this.date = date;
        this.user = user;
        this.uuid = uuid;
    }
}
