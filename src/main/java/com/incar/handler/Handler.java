package com.incar.handler;


public interface Handler<T> {

    String request(T param);

    String requestWow(T param);
}
