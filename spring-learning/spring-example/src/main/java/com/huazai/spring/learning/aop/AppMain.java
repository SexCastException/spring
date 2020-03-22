package com.huazai.spring.learning.aop;

import com.huazai.spring.learning.aop.dao.CityDao;
import com.huazai.spring.learning.aop.dao.Dao;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import sun.misc.ProxyGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.lang.reflect.Proxy;

/**
 * @author pyh
 * @date 2020/3/22 12:44
 */
public class AppMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
//		applicationContext.register(AppConfig.class);
//		applicationContext.refresh();

        Dao cityDao = (Dao) applicationContext.getBean("cityDao");
        /*

         */
        System.out.println("is Dao:" + (cityDao instanceof Dao));
        System.out.println("is CityDao:" + (cityDao instanceof CityDao));
        System.out.println("is java.lang.reflect.Proxy:" + (cityDao instanceof Proxy));
        System.out.println("------------------------------");
        cityDao.query("hello");
        System.out.println("------------------------------");
        cityDao.query();

        generatorClass("myProxy.class", Dao.class);

        System.out.println("______________________________");

        Dao orderDao = (Dao) applicationContext.getBean("orderDao");
        orderDao.query();
    }

    private static void generatorClass(String className, Class... clazzs) {
        byte[] bytes = ProxyGenerator.generateProxyClass(className, clazzs);
        try {
            OutputStream out = new FileOutputStream(new File("c:\\Users\\pyh\\Desktop\\" + className));
            out.write(bytes);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
