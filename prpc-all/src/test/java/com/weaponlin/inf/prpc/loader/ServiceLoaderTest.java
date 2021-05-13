package com.weaponlin.inf.prpc.loader;

import com.weaponlin.inf.prpc.exception.PRPCException;
import org.junit.Assert;
import org.junit.Test;

public class ServiceLoaderTest {

    @Test(expected = PRPCException.class)
    public void throw_exception_if_class_is_not_an_interface() {
        ServiceLoader.getService(D.class, "");
    }

    @Test(expected = PRPCException.class)
    public void throw_exception_if_extension_is_blank() {
        ServiceLoader.getService(A.class, "");
    }

    @Test(expected = PRPCException.class)
    public void throw_exception_if_extension_is_invalid() {
        ServiceLoader.getService(A.class, "D");
    }

    @Test
    public void get_service() {
        final A b = ServiceLoader.getService(A.class, "B");
        Assert.assertEquals(B.class, b.getClass());
    }

    interface A {

    }

    @Extension(name = "B")
    public static class B implements A {

    }

    @Extension(name = "C")
    public static class C implements A {

    }

    public static class D implements A {

    }
}