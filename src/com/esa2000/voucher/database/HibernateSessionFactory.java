package com.esa2000.voucher.database;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateSessionFactory {
	private static Logger logger = Logger.getLogger(HibernateSessionFactory.class);
    private static Configuration configuration = null;
    private static SessionFactory sessionFactory = null;
    
    static{
    	buildSessionFactory();
    }
    
	public static void buildSessionFactory() {
		try {
			logger.info("加载Hibernate配置文件");

			configuration = new Configuration().configure("/hibernate.cfg.xml");
			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		} catch (Exception e) {
			logger.info("%%%% Error Creating SessionFactory %%%%");
			logger.info(e);
		}
	}
	
    private static String getHibernateConfigPath() {
		String hibernateFileName = "/hibernate.cfg.xml";
		return hibernateFileName;
    }
    
    public static Session getSession() {
    	Session session = null;
    	if(sessionFactory != null) {
		} else {
			buildSessionFactory();
		}
    	session = sessionFactory.openSession();

		return session;
    }

    public void closeSession(Session session) {
		if(session != null && session.isOpen()) {
    		session.close();
    	}
	}
    
}