package org.jboss.fuse;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named("someBean")
public class SomeBean {

    private int counter;

    public String someMethod(String body) {
        return "Grenoble " + ++counter;
    }

}
