package org.jboss.fuse;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Singleton
@Named("someBean")
public class SomeBean {

    static List<String> sales;

    public SomeBean() {
        sales = new ArrayList<String>();
        sales.add("James Strachan");
        sales.add("Claus Ibsen");
        sales.add("Hiram Chirino");
        sales.add("Jeff Bride");
        sales.add("Chad Darby");
        sales.add("Rachel Cassidy");
        sales.add("Bernard Tison");
        sales.add("Nandan Joshi");
        sales.add("Rob Davies");
        sales.add("Guillaume Nodet");
        sales.add("Marc Little");
        sales.add("Mario Fusco");
        sales.add("James Hetfield");
        sales.add("Kirk Hammett");
        sales.add("Steve Perry");
    }

    private int counter;

    public static String getRandomSales() {
        //0-11
        int index = new Random().nextInt(sales.size());
        return sales.get(index);
    }

}
