package com.cadre.server.core.cache;

import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CCache<K,V> implements CacheInterface, Map<K, V>, Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(CCache.class);
	
	public static int MAX_SIZE = 1000;

	private Map<K, V> cache = null;
	
	private Set<K> nullList = null;

	private String m_tableName;
	
	private int m_maxSize = 0;
	
	
	
	/**	Name						*/
	private String				m_name = null;
	/** Expire after minutes		*/
	private int					m_expire = 0;
	/** Time						*/ 
	private volatile long		m_timeExp = 0;
	/**	Just reset - not used		*/
	private boolean				m_justReset = true;
	
	/** Vetoable Change Support			*/
	private VetoableChangeSupport	m_changeSupport = null;
	
	/** Vetoable Change Support	Name	*/
	private static String		PROPERTYNAME = "cache"; 
	
	public CCache (String name, int initialCapacity)
	{
		this(name, name, initialCapacity);
	}
	
	public CCache (String name, int initialCapacity, int expireMinutes)
	{
		this(name, initialCapacity, expireMinutes, false);
	}
	
	public CCache (String name, int initialCapacity, int expireMinutes, boolean distributed)
	{
		this(name, name, initialCapacity, expireMinutes, distributed);
	}
	
	public CCache (String name, int initialCapacity, int expireMinutes, boolean distributed, int maxSize)
	{
		this(name, name, initialCapacity, expireMinutes, distributed, maxSize);
	}
	
	/**
	 * 	Adempiere Cache - expires after 2 hours
	 * 	@param name (table) name of the cache
	 * 	@param initialCapacity initial capacity
	 */
	public CCache (String tableName, String name, int initialCapacity)
	{
		this (tableName, name, initialCapacity, false);
	}	//	CCache

	public CCache (String tableName, String name, int initialCapacity, boolean distributed)
	{
		this (tableName, name, initialCapacity, 60, distributed);
	}		
	
	public CCache (String tableName, String name, int initialCapacity, int expireMinutes, boolean distributed)
	{
		this(tableName, name, initialCapacity, expireMinutes, distributed, MAX_SIZE);
	}
	
	/**
	 * 	Adempiere Cache
	 * 	@param name (table) name of the cache
	 * 	@param initialCapacity initial capacity
	 * 	@param expireMinutes expire after minutes (0=no expire)
	 *  @param distributed
	 *  @param maxSize ignore if distributed=true
	 */
	public CCache (String tableName, String name, int initialCapacity, int expireMinutes, boolean distributed, int maxSize)
	{
		m_name = name;
		m_tableName = tableName;
		setExpireMinutes(expireMinutes);
		m_maxSize = maxSize; 
		cache = Collections.synchronizedMap(new MaxSizeHashMap<K, V>(MAX_SIZE));
		
		if (nullList == null) {
			nullList = Collections.synchronizedSet(new HashSet<K>());
		}
	}	//	CCache
	
	/**
	 * 	Get (table) Name
	 *	@return name
	 */
	public String getName()
	{
		return m_name;
	}	//	getName
	
	/**
	 * 	Set Expire Minutes and start it
	 *	@param expireMinutes minutes or 0
	 */
	public void setExpireMinutes (int expireMinutes)
	{
		if (expireMinutes > 0)
		{
			m_expire = expireMinutes;
			long addMS = 60000L * m_expire;
			m_timeExp = System.currentTimeMillis() + addMS;
		}
		else
		{
			m_expire = 0;
			m_timeExp = 0;
		}
	}	//	setExpireMinutes

	/**
	 * 	Get Expire Minutes
	 *	@return expire minutes
	 */
	public int getExpireMinutes()
	{
		return m_expire;
	}	//	getExpireMinutes
	

	/**
	 * 	Cache was reset
	 *	@return true if reset
	 */
	public boolean isReset()
	{
		return m_justReset;
	}	//	isReset

	/**
	 * 	Resets the Reset flag
	 */
	public void setUsed()
	{
		m_justReset = false;
	}	//	setUsed
	
	/**
	 *	Reset Cache
	 * 	@return number of items cleared
	 *	@see org.compiere.util.CacheInterface#reset()
	 */
	public int reset()
	{
		int no = cache.size()+nullList.size();
		clear();
		return no;
	}	//	reset

	/**
	 * 	Expire Cache if enabled
	 */	
	private void expire()
	{
		if (m_expire != 0 && m_timeExp < System.currentTimeMillis())
		{
		//	System.out.println ("------------ Expired: " + getName() + " --------------------");
			reset();
		}
	}	//	expire

	/**
	 * 	String Representation
	 * 	@return info
	 */
	public String toString()
	{
		return "CCache[" + m_name 
			+ ",Exp=" + getExpireMinutes()  
			+ ", #" + cache.size() + "]";
	}	//	toString
	
	/**
	 * 	Clear cache and calculate new expiry time
	 *	@see java.util.Map#clear()
	 */
	public void clear()
	{
		if (m_changeSupport != null)
		{
			try
			{
				m_changeSupport.fireVetoableChange(PROPERTYNAME, cache.size(), 0);
			}
			catch (Exception e)
			{
				LOGGER.error("clear()", e);
				return;
			}
		}
		//	Clear
		cache.clear();
		nullList.clear();
		if (m_expire != 0)
		{
			long addMS = 60000L * m_expire;
			m_timeExp = System.currentTimeMillis() + addMS;
		}
		m_justReset = true;
	}	//	clear
	
	/**
	 *	@see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key)
	{
		expire();
		return cache.containsKey(key) || nullList.contains(key);
	}	//	containsKey

	/**
	 *	@see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value)
	{
		expire();
		return cache.containsValue(value);
	}	//	containsValue

	/**
	 *  The return entry set exclude entries that contains null value
	 *	@see java.util.Map#entrySet()
	 */
	public Set<Map.Entry<K,V>> entrySet()
	{
		expire();
		return cache.entrySet();
	}	//	entrySet

	/**
	 *	@see java.util.Map#get(java.lang.Object)
	 */
	public V get(Object key)
	{
		expire();
		return cache.get(key);
	}	//	get
	
	/**
	 * 	Put value
	 *	@param key key
	 *	@param value value
	 *	@return previous value
	 */
	public V put (K key, V value)
	{
		expire();
		m_justReset = false;
		if (value == null) {
			cache.remove(key);
			nullList.add(key);
			return null;
		} else if (!nullList.isEmpty()) {
			nullList.remove(key);
		}
		return cache.put (key, value);
	}	// put

	/**
	 * 	Put All
	 *	@param m map
	 */
	public void putAll (Map<? extends K, ? extends V> m)
	{
		expire();
		m_justReset = false;
		cache.putAll (m);
	}	//	putAll
	
	/**
	 *	@see java.util.Map#isEmpty()
	 */
	public boolean isEmpty()
	{
		expire();
		return cache.isEmpty() && nullList.isEmpty();
	}	// isEmpty

	/**
	 * The return key set excludes key that map to null value
	 *	@see java.util.Map#keySet()
	 */
	public Set<K> keySet()
	{
		expire();
		return cache.keySet();
	}	//	keySet

	/**
	 *	@see java.util.Map#size()
	 */
	public int size()
	{
		expire();
		return cache.size()+nullList.size();
	}	//	size

	/**
	 * 	Get Size w/o Expire
	 *	@see java.util.Map#size()
	 *	@return size
	 */
	public int sizeNoExpire()
	{
		return cache.size()+nullList.size();
	}	//	size

	/**
	 *  The return values collection exclude null value entries
	 *	@see java.util.Map#values()
	 */
	public Collection<V> values()
	{
		expire();
		return cache.values();
	}	//	values
	
	/**
	 * 	Add Vetoable Change Listener
	 *	@param listener listner
	 */
	public void addVetoableChangeListener (VetoableChangeListener listener)
	{
		if (m_changeSupport == null)
			m_changeSupport = new VetoableChangeSupport (this);
		if (listener != null)
			m_changeSupport.addVetoableChangeListener(listener);
	}	//	addVetoableChangeListener

	/**
	 * 	Remove Vetoable Change Listener
	 *	@param listener listener
	 */
    public void removeVetoableChangeListener (VetoableChangeListener listener) 
    {
		if (m_changeSupport != null && listener != null)
			m_changeSupport.removeVetoableChangeListener(listener);
    }	//	removeVetoableChangeListener


	@Override
	public V remove(Object key) {
		if (!nullList.isEmpty()) {
			if (nullList.remove(key)) return null;
		}
		return cache.remove(key);
	}

	@Override
	public int reset(int recordId) {
		if (recordId <= 0)
			return reset();
				
		if (!nullList.isEmpty()) {
			if (nullList.remove(recordId)) return 1;
		}
		V removed = cache.remove(recordId);
		return removed != null ? 1 : 0;
	}

	@Override
	public void newRecord(int record_ID) {
	}

	public int getMaxSize() {
		return m_maxSize;
	}
	
	private static class MaxSizeHashMap<K, V> extends LinkedHashMap<K, V> {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/**
		 * generated serial id
		 */
		private final int maxSize;

	    public MaxSizeHashMap(int maxSize) {
	        this.maxSize = maxSize;
	    }

	    @Override
	    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
	        return maxSize <= 0 ? false : size() > maxSize;
	    }
	}
}
