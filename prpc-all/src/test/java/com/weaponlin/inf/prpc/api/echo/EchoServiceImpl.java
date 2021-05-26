package com.weaponlin.inf.prpc.api.echo;

public class EchoServiceImpl implements EchoService {

    @Override
    public String echo(String msg) {
        return msg;
    }
}
