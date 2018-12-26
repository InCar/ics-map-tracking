package com.incarcloud.base.dao;


import java.util.function.Function;

public interface DataAccess<T> {
    <R>R doInConnection(Function<T,R> function);
}
