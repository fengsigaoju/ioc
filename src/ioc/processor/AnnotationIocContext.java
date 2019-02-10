/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */
package ioc.processor;

import ioc.annotation.AutoWired;
import ioc.annotation.Component;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * 注解器上下文
 *
 * @author lanzhang
 * @version $Id: AnnotationIocContext.java, v 0.1 2019年02月09日 上午11:26 lanzhang Exp $
 */
public class AnnotationIocContext {

    /**
     * 扫描包名
     */
    private String packageName;

    /**
     * 类工厂 key:类的注解value值,value:类的实例
     */
    private HashMap<String, Object> beanFactory=new HashMap<>();

    public AnnotationIocContext(String packageName) {
        this.packageName = packageName;
        scanPackage(packageName);
    }

    /**
     * 扫描指定包路径下的类
     *
     * @param packageName
     */
    private void scanPackage(String packageName) {
        // 包名对应的路径名称
        String packageDirName = packageName.replace('.', '/');
        try {
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    System.out.println(filePath + ":" + "file类型的扫描");
                    findClassInPackageByFile(packageName, filePath);
                } else if ("jar".equals(protocol)) {
                    System.out.println("jar类型的扫描");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * 扫描指定包下面的文件路径名称
     *
     * @param packageName
     * @param filePath
     */
    private void findClassInPackageByFile(String packageName, String filePath) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 在给定的目录下找到所有的文件，并且进行条件过滤
        File[] dirFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                boolean acceptDir = file.isDirectory();// 接受dir目录
                boolean acceptClass = file.getName().endsWith("class");// 接受class文件
                return acceptDir || acceptClass;
            }
        });
        if (dirFiles != null && dirFiles.length > 0) {
            for (File file : dirFiles) {
                //如果是文件则增加包路径的名称,同时递归搜索子目录
                if (file.isDirectory()) {
                    findClassInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath());
                } else {
                    try {
                        //class类型文件去除后缀
                        String className = file.getName().substring(0, file.getName().length() - 6);
                        Class loadClass = Class.forName(packageName + "." + className);
                        Object componentClass=loadClass.newInstance();
                        if (loadClass.isAnnotationPresent(Component.class)) {
                            //getField与getDeclaredFiled的区别是:
                            //getField子类public字段和父类public字段
                            //getDeclaredField子类所有的字段,但是不体现父类字段
                            Field fieldList[] = loadClass.getDeclaredFields();
                            for (Field field : fieldList) {
                                if (field.isAnnotationPresent(AutoWired.class)) {
                                    //这里还是有些问题的,如果待注入的bean后被扫描掉则无法注入
                                    Object object = beanFactory.get(field.getAnnotation(AutoWired.class).value());
                                    if (object != null) {
                                        field.setAccessible(true);
                                        field.set(componentClass, object);
                                    }
                                }
                            }
                            Component component=(Component)loadClass.getAnnotation(Component.class);
                            beanFactory.put(component.value(),componentClass);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public <T> T loadFromContext(String id,Class<T> clazz){
       Object object=beanFactory.get(id);
       if (object.getClass()==clazz){
           return (T)object;
       }
       throw new ClassCastException("类型转换失败");
    }

    /**
     * Getter method for property <tt>packageName</tt>.
     *
     * @return property value of packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Setter method for property <tt>packageName</tt>.
     *
     * @param packageName value to be assigned to property packageName
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Getter method for property <tt>beanFactory</tt>.
     *
     * @return property value of beanFactory
     */
    public HashMap<String, Object> getBeanFactory() {
        return beanFactory;
    }

    /**
     * Setter method for property <tt>beanFactory</tt>.
     *
     * @param beanFactory value to be assigned to property beanFactory
     */
    public void setBeanFactory(HashMap<String, Object> beanFactory) {
        this.beanFactory = beanFactory;
    }
}