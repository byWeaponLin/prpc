package com.github.weaponlin.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ZkUtilsTest {

    @Test
    public void test() {
        String path = ZkUtils.analysisZkPath("/base/aaa/bbb");
        assertEquals("/base/aaa/bbb", path);

        path = ZkUtils.analysisZkPath("base/aaa/bbb");
        assertEquals("/base/aaa/bbb", path);

        path = ZkUtils.analysisZkPath("/base/aaa/bbb/");
        assertEquals("/base/aaa/bbb", path);

        path = ZkUtils.analysisZkPath("base/aaa/bbb/");
        assertEquals("/base/aaa/bbb", path);

        path = ZkUtils.analysisZkPath("base");
        assertEquals("/base", path);

        path = ZkUtils.analysisZkPath("/base");
        assertEquals("/base", path);

        path = ZkUtils.analysisZkPath("base/");
        assertEquals("/base", path);

        path = ZkUtils.analysisZkPath("/base/");
        assertEquals("/base", path);
    }
}