/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */
package test.Service;

import ioc.annotation.AutoWired;
import ioc.annotation.Component;

/**
 *
 * @author lanzhang
 * @version $Id: UserService.java, v 0.1 2019年02月09日 下午7:45 lanzhang Exp $
 */
@Component(value = "userService")
public class UserService {

    @AutoWired(value="userDao")
    private UserDao userDao;

    public void add(){
        userDao.add();
        System.out.println(this.getClass().getSimpleName()+",add");
    }
}