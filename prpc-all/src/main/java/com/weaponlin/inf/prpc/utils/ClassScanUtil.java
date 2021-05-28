package com.weaponlin.inf.prpc.utils;

import com.google.common.collect.Lists;
import com.weaponlin.inf.prpc.exception.PRPCException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
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

        Enumeration<URL> resources = cl.getResources(packagePath);
        List<String> classes = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            String filePath = getRootPath(url);
            List<String> names = null;
            if (isJarFile(filePath)) {
                names = readFromJarFile(filePath, packagePath);
            } else {
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
            if (CollectionUtils.isNotEmpty(nameList)) {
                classes.addAll(nameList);
            }
        }
        return classes;
    }

    private static String toFullyQualifiedName(String shortName, String basePackage) {
        StringBuilder sb = new StringBuilder(basePackage);
        sb.append('.');
        sb.append(shortName.substring(0, shortName.lastIndexOf('.')));
        return sb.toString();
    }

    private static List<String> readFromJarFile(String jarPath, String splashedPackageName) throws IOException {
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

    public static List<Class> loadClassByLoader() throws Exception {
        return loadClassByLoader(cl);
    }

    /**
     * 通过loader加载所有类
     *
     * @param classLoader
     * @return
     * @throws Exception
     */
    public static List<Class> loadClassByLoader(ClassLoader classLoader) throws Exception {
        Enumeration<URL> urls = classLoader.getResources("");
        List<Class> classes = new ArrayList<Class>();
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            // 文件类型（其实是文件夹）
            if (url.getProtocol().equals("file")) {
                loadClassByPath(null, url.getPath(), classes, classLoader);
            }
        }
        return classes;
    }

    // 通过文件路径加载所有类 root 主要用来替换path中前缀（除包路径以外的路径）

    private static void loadClassByPath(String root, String path, List<Class> list, ClassLoader load) {
        File f = new File(path);
        if (root == null) root = f.getPath();
        // 判断是否是class文件
        if (f.isFile() && f.getName().matches("^.*\\.class$")) {
            try {
                String classPath = f.getPath();
                //截取出className 将路径分割符替换为.（windows是\ linux、mac是/）
                String className = classPath.substring(root.length() + 1, classPath.length() - 6).replace('/', '.').replace('\\', '.');
                list.add(load.loadClass(className));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            File[] fs = f.listFiles();
            if (fs == null) return;
            for (File file : fs) {
                loadClassByPath(root, file.getPath(), list, load);
            }
        }
    }

    /**
     * For test purpose.
     */
    public static void main(String[] args) throws Exception {
//        List<Class<?>> interfacees = ClassScanUtil.getInterface("com.weaponlin.inf.prpc");
//        interfacees.forEach(e -> System.out.println(e.getName()));

//        String path = ClassScanUtil.class.getClassLoader().getResource("").getPath();
//        path = path.substring(0, path.length() - 1);
//        path = path.substring(0, path.lastIndexOf("/"));
//        ClassScanUtil.getFullyQualifiedClassNameList(path).forEach(e -> System.out.println(e));

        loadClassByLoader(ClassScanUtil.class.getClassLoader()).forEach(e -> System.out.println(e.getName()));


    }
}
