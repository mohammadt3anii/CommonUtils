/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.services;

import java.util.ArrayList;
import java.util.List;

public class HttpPool {

	private static Class httpConnectionCls = null; // HttpJavaNet.class;

	private static final int POOL_SIZE = 5;
	private static final int WAIT_FOR_HOW_LONG = 5 * 60 * 1000; // 5 minutes
	
	private static ArrayList<HttpConnection> pool;
	
	private static HttpPool instance;
	
	private static int size = -1;

	public HttpPool() {
	}

	public static void setHttpConnectionClass(Class httpConnectionCls) {
		HttpPool.httpConnectionCls = httpConnectionCls;
	}

	public static boolean hasSetHttpConnectionClass() {
	    return null != httpConnectionCls;
    }

	public static void setSize(int size) {
        HttpPool.size = size;
    }

    public static void addHttpInstance(HttpConnection instance) {
		getPool().add(instance);
	}

	private static List getPool() {
        if (null == pool)
            pool = new ArrayList<>();
        return pool;
    }

	public void addHttpInstanceDefault() throws InstantiationException, IllegalAccessException {
        initialize();
	}

	public static HttpConnection createHttpInstanceDefault() throws IllegalAccessException, InstantiationException {
		if (null == httpConnectionCls)
			httpConnectionCls = HttpJavaNet.class;
        return (HttpConnection) httpConnectionCls.newInstance();
    }

    public static void initialize() throws InstantiationException, IllegalAccessException {
        if (size <= 0)
            size = POOL_SIZE;

        for (int i = 0; i < size; ++i)
            addHttpInstance(createHttpInstanceDefault());
	}

    /**
     *
     * @return
     */
	public synchronized static HttpPool getInstance() {
		if (instance == null)
			instance = new HttpPool();
		return instance;
	}

	/**
	 *
	 * @return
	 */
	public synchronized HttpConnection getIdleConnection() {
		return getConnectionInternal();
	}

    /**
     * After finishing with the connection, use connection.setEngaged(false) to return the connection to the pool
     *
     * @return
     */
	public static synchronized HttpConnection getConnection() {
//		if (null == pool || pool.size() == 0)
//			initialize();

		return getInstance().getConnectionInternal();
	}

    /**
     *
     * @return
     */
	private synchronized HttpConnection getConnectionInternal() {
		long start = System.nanoTime();
		HttpConnection available = null;
		while (available == null) {
            if (null == pool || pool.size() == 0)
                try {
                    addHttpInstanceDefault();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            for (HttpConnection conn : pool) {
				if (!conn.isEngaged() && !conn.isInUsed()) {
					available = conn;
					break;
				}
			}

			if (available == null && pool.size() < size) {
				available = new HttpJavaNet();
				pool.add(available);
			}

			if (available == null)
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			long end = System.nanoTime();
			long microseconds = (end - start) / 1000;
			if (microseconds > WAIT_FOR_HOW_LONG)
				break;
		}
		
		if (available != null) {
			available.setCaller(null);
            /*

            we shouldn't set it engaged here
			available.setEngaged(true);
            */
		}
		else {
            try {
                available = createHttpInstanceDefault();
                addHttpInstance(available);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
		return available;
	}

    public static int size() {
		return getInstance().getPool().size();
    }
}
