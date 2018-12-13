package com.incar.base.handler;

import java.util.Comparator;

/**
 * 接口的排序实现
 * @see Ordered
 */
public class OrderedComparator implements Comparator<OrderedHandler>{
    public final static Comparator<OrderedHandler> INSTANCE=new OrderedComparator();
    @Override
    public int compare(OrderedHandler o1, OrderedHandler o2) {
        return o1.getOrder()-o2.getOrder();
    }
}
