/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */
package test.Service;

import ioc.annotation.Component;

/**
 *
 * @author lanzhang
 * @version $Id: UserDao.java, v 0.1 2019年02月10日 上午9:40 lanzhang Exp $
 */
@Component(value = "userDao")
public class UserDao {

    public void add(){
        System.out.println(this.getClass().getSimpleName()+",add");
    }
}