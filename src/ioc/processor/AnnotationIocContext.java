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
 * ע����������
 *
 * @author lanzhang
 * @version $Id: AnnotationIocContext.java, v 0.1 2019��02��09�� ����11:26 lanzhang Exp $
 */
public class AnnotationIocContext {

    /**
     * ɨ�����
     */
    private String packageName;

    /**
     * �๤�� key:���ע��valueֵ,value:���ʵ��
     */
    private HashMap<String, Object> beanFactory=new HashMap<>();

    public AnnotationIocContext(String packageName) {
        this.packageName = packageName;
        scanPackage(packageName);
    }

    /**
     * ɨ��ָ����·���µ���
     *
     * @param packageName
     */
    private void scanPackage(String packageName) {
        // ������Ӧ��·������
        String packageDirName = packageName.replace('.', '/');
        try {
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    System.out.println(filePath + ":" + "file���͵�ɨ��");
                    findClassInPackageByFile(packageName, filePath);
                } else if ("jar".equals(protocol)) {
                    System.out.println("jar���͵�ɨ��");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * ɨ��ָ����������ļ�·������
     *
     * @param packageName
     * @param filePath
     */
    private void findClassInPackageByFile(String packageName, String filePath) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // �ڸ�����Ŀ¼���ҵ����е��ļ������ҽ�����������
        File[] dirFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                boolean acceptDir = file.isDirectory();// ����dirĿ¼
                boolean acceptClass = file.getName().endsWith("class");// ����class�ļ�
                return acceptDir || acceptClass;
            }
        });
        if (dirFiles != null && dirFiles.length > 0) {
            for (File file : dirFiles) {
                //������ļ������Ӱ�·��������,ͬʱ�ݹ�������Ŀ¼
                if (file.isDirectory()) {
                    findClassInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath());
                } else {
                    try {
                        //class�����ļ�ȥ����׺
                        String className = file.getName().substring(0, file.getName().length() - 6);
                        Class loadClass = Class.forName(packageName + "." + className);
                        Object componentClass=loadClass.newInstance();
                        if (loadClass.isAnnotationPresent(Component.class)) {
                            //getField��getDeclaredFiled��������:
                            //getField����public�ֶκ͸���public�ֶ�
                            //getDeclaredField�������е��ֶ�,���ǲ����ָ����ֶ�
                            Field fieldList[] = loadClass.getDeclaredFields();
                            for (Field field : fieldList) {
                                if (field.isAnnotationPresent(AutoWired.class)) {
                                    //���ﻹ����Щ�����,�����ע���bean��ɨ������޷�ע��
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
       throw new ClassCastException("����ת��ʧ��");
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