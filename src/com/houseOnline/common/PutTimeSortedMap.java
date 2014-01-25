/*
 * PutTimeSortedMap.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.common;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * <p>ClassName: PutTimeSortedMap</p>
 * 
 * <p>Abstract:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Dec 15, 2013
 */
public class PutTimeSortedMap<K, V> extends HashMap<K, V> {

  private List<K> order = new ArrayList<K>();
  private Set<K> keySet = null;

  public V put(K key, V value) {
    if (order.contains(key)) {
      order.remove(key);
    }
    order.add(key);
    return super.put(key, value);
  }

  public V remove(Object key) {
    order.remove(key);
    return super.remove(key);
  }

  public Set<K> keySet() {
    Set<K> ks = keySet;
    return (ks != null ? ks : (keySet = new KeySet()));
  }

  private class KeySet extends AbstractSet<K> {
    public Iterator<K> iterator() {
      return order.iterator();
    }

    public int size() {
      return order.size();
    }

    public boolean contains(Object o) {
      return order.contains(o);
    }

    public boolean remove(Object o) {
      return order.remove(o);
    }

    public void clear() {
      order.clear();
    }
  }

}
