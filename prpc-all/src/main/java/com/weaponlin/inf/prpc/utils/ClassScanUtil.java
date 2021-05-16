package com.weaponlin.inf.prpc.utils;

import com.google.common.collect.Lists;
import com.weaponlin.inf.prpc.exception.PRPCException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * TODO refactor
 */
@Slf4j
public class ClassScanUtil {
    private static ClassLoader cl = ClassScanUtil.class.getClassLoader();

    /**
     * 获取指定包下的所有字节码文件的全类名
     */
    public static List<String> getFullyQualifiedClassNameList(String basePackage) throws IOException {
        log.info("start scanning package: {}", basePackage);
        return doScan(basePackage, new ArrayList<>());
    }

    public static List<Class<?>> getInterface(String basePackage) {
        try {
            List<String> classes = doScan(basePackage, new ArrayList<>());
            List<Class<?>> res = Lists.newArrayList();
            for (String className : classes) {
                Class<?> clazz = Class.forName(className);
                Optional.ofNullable(clazz).filter(Class::isInterface).ifPresent(res::add);
            }
            return res;
        } catch (Exception e) {
            throw new PRPCException("load base package class failed, package: {}" + basePackage, e);
        }
    }


    /**
     * doScan函数
     *
     * @param basePackage
     * @param nameList
     * @return
     * @throws IOException
     */
    private static List<String> doScan(String basePackage, List<String> nameList) throws IOException {
        String packagePath = basePackage.replaceAll("\\.", "/");
        //file:/D:/WorkSpace/java/ScanTest/target/classes/com/scan
        URL url = cl.getResource(packagePath);
        String filePath = getRootPath(url);
        // contains the name of the class file. e.g., Apple.class will be stored as "Apple"
        List<String> names = null;
        // 先判断是否是jar包，如果是jar包，通过JarInputStream产生的JarEntity去递归查询所有类
        if (isJarFile(filePath)) {
            if (log.isDebugEnabled()) {
                log.debug("{} 是一个JAR包", filePath);
            }
            names = readFromJarFile(filePath, packagePath);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("{} 是一个目录", filePath);
            }
            names = readFromDirectory(filePath);
        }
        for (String name : names) {
            if (isClassFile(name)) {
                nameList.add(toFullyQualifiedName(name, basePackage));
            } else {
                doScan(basePackage + "." + name, nameList);
            }
        }
        if (log.isDebugEnabled()) {
            for (String n : nameList) {
                log.debug("找到{}", n);
            }
        }
        return nameList;
    }

    private static String toFullyQualifiedName(String shortName, String basePackage) {
        StringBuilder sb = new StringBuilder(basePackage);
        sb.append('.');
        sb.append(shortName.substring(0, shortName.lastIndexOf('.')));
        return sb.toString();
    }

    private static List<String> readFromJarFile(String jarPath, String splashedPackageName) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("从JAR包中读取类: {}", jarPath);
        }
        JarInputStream jarIn = new JarInputStream(new FileInputStream(jarPath));
        JarEntry entry = jarIn.getNextJarEntry();
        List<String> nameList = new ArrayList<String>();
        while (null != entry) {
            String name = entry.getName();
            if (name.startsWith(splashedPackageName) && isClassFile(name)) {
                nameList.add(name);
            }

            entry = jarIn.getNextJarEntry();
        }

        return nameList;
    }

    private static String getRootPath(URL url) {
        String fileUrl = url.getFile();
        int pos = fileUrl.indexOf('!');

        if (-1 == pos) {
            return fileUrl;
        }

        return fileUrl.substring(5, pos);
    }

    private static List<String> readFromDirectory(String path) {
        File file = new File(path);
        String[] names = file.list();

        if (null == names) {
            return null;
        }

        return Arrays.asList(names);
    }

    private static boolean isClassFile(String name) {
        return name.endsWith(".class");
    }

    private static boolean isJarFile(String name) {
        return name.endsWith(".jar");
    }

    /**
     * For test purpose.
     */
    public static void main(String[] args) throws Exception {
        List<Class<?>> interfacees = ClassScanUtil.getInterface("com.weaponlin.inf.prpc");
        interfacees.forEach(e -> System.out.println(e.getName()));

    }
}
