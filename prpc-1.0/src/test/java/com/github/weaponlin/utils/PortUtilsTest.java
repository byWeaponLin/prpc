package com.github.weaponlin.utils;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class PortUtilsTest {

    @Test
    public void get_port() {
        System.out.println(PortUtils.getAvailablePort());
    }

    @Test
    public void test() {
        Assert.assertFalse(PortUtils.isAvailable("127.0.0.1", 2181));
        Assert.assertTrue(PortUtils.isAvailable("127.0.0.1", 21811));
    }
}