/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */
package test.Main;

import ioc.processor.AnnotationIocContext;
import test.Service.UserService;

/**
 *
 * @author lanzhang
 * @version $Id: Main.java, v 0.1 2019年02月09日 下午7:04 lanzhang Exp $
 */
public class Main {
    public static void main(String[] args) {
        AnnotationIocContext annotationIocContext=new AnnotationIocContext("test.Service");
        System.out.println(annotationIocContext.getBeanFactory());
        UserService userService=annotationIocContext.loadFromContext("userService",UserService.class);
        userService.add();
        }
}