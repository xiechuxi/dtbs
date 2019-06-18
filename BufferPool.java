package hw4;

import java.io.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import hw1.Database;
import hw1.HeapFile;
import hw1.HeapPage;
import hw1.Tuple;

/**
 * BufferPool manages the reading and writing of pages into memory from
 * disk. Access methods call into it to retrieve pages, and it fetches
 * pages from the appropriate location.
 * <p>
 * The BufferPool is also responsible for locking;  when a transaction fetches
 * a page, BufferPool which check that the transaction has the appropriate
 * locks to read/write the page.
 */
public class BufferPool {
    /** Bytes per page, including header. */
    public static final int PAGE_SIZE = 4096;
 
    /** Default number of pages passed to the constructor. This is used by
    other classes. BufferPool should use the numPages argument to the
    constructor instead. */
    public static final int DEFAULT_PAGES = 50;
    
    protected int numpg;
    protected HashMap<HeapPage,ArrayList<SimpleEntry<Integer,Permissions>>> cache= new HashMap<HeapPage, ArrayList<SimpleEntry<Integer, Permissions>>>();
    protected ArrayList<Integer> blocked=new ArrayList<Integer>();
    protected HashMap<SimpleEntry<Integer,Integer>,ArrayList<HeapPage>> temppage= new HashMap<SimpleEntry<Integer,Integer>,ArrayList<HeapPage>>();
    /**
     * Creates a BufferPool that caches up to numPages pages.
     *
     * @param numPages maximum number of pages in this buffer pool.
     */
    public BufferPool(int numPages) {
    	this.numpg=numPages;
    	
        // your code here
    }
    
    

    /**
     * Retrieve the specified page with the associated permissions.
     * Will acquire a lock and may block if that lock is held by another
     * transaction.
     * <p>
     * The retrieved page should be looked up in the buffer pool.  If it
     * is present, it should be returned.  If it is not present, it should
     * be added to the buffer pool and returned.  If there is insufficient
     * space in the buffer pool, an page should be evicted and the new page
     * should be added in its place.
     *
     * @param tid the ID of the transaction requesting the page
     * @param tableId the ID of the table with the requested page
     * @param pid the ID of the requested page
     * @param perm the requested permissions on the page
     */
    public HeapPage getPage(int tid, int tableId, int pid, Permissions perm)
            throws Exception {
    	// Problem: Don't read page if already in cache
    	HeapPage hp = Database.getCatalog().getDbFile(tableId).readPage(pid);
    	
    	if(hp==null) {
    		return null;
    	}
    	else {
    		if(gecahe().containsKey(hp)) {
    			for ( HeapPage p : gecahe().keySet() ) {
    				if(p.check(hp)) {
    					hp=p;
    					break;
    				}
    			}
    		}
    		
    	}
    	
    	if(hp.isDirty()) {
    		// page has been written
    		if(holdsLock(tid,tableId,pid)) {
    			// Problem: We did not check whether we have the given perm or not
    			// Solution: using the more complete lock class + acquire method that checks against perm 
    			//      and also acquires the lock if tid doesn't have permission yet
    			return hp;
    		}
    		else {
    			blocked.add(tid);
    			int count=0;
    			while(hp.isDirty()) {
    				try{
    					if(count>20) {
    					transactionComplete(tid,false);
    					return null;
    				}
    				Thread.sleep(10);
    				count++;}
    				catch(Exception e) {}
    			}
    			return getPage(tid,tableId,pid,perm);
    		}
    	}
    	else {
    		if(perm.permLevel==1) {
    			if(gecahe().containsKey(hp)) {
    				if(gecahe().get(hp).isEmpty()) {
    					addpool(tid,perm,hp);
    					return hp;
    				}
    				else {
    					if(gecahe().get(hp).size()==1) {
    						if(holdsLock(tid, tableId, pid)) {
								HashMap<HeapPage,ArrayList<SimpleEntry<Integer, Permissions>>> nMap = gecahe();
								ArrayList<SimpleEntry<Integer, Permissions>> l = new ArrayList<SimpleEntry<Integer, Permissions>>();
								l.add(new SimpleEntry<Integer, Permissions>(tid, perm));
								nMap.remove(hp);
								nMap.put(hp, l);
								setcache(nMap);
								return hp;
    						}
    					}
    					
    					blocked.add(tid);
    					int count=0;
    					while(gecahe().get(hp).size()>1) {
    						if(count>20) {
    							transactionComplete(tid,false);
    							return null;
    						}
    						count++;
    					}
    					return getPage(tid,tableId,pid,perm);
    					
    				}
    			}
    			else {
    				if(gecahe().size()==numpg) {
    					try{
    						evictPage();
    					}
    					catch (Exception e) {
    						transactionComplete(tid, false);
    						return null;
						}
    				}
    				hp.setDirty(true);
    				addpool(tid,perm,hp);
    				return hp;
    			}
    		}
    		//read only
    		else {
    			//add to cache
    			if(!gecahe().containsKey(hp)) {
    				if(gecahe().size()==numpg) {
    					try {
    						evictPage();
    					}
    					catch(Exception e) {
    						transactionComplete(tid,false);
    						return null;
    					}
    				}
    				
    			}
    			return hp;
    		}
    	}
            // your code here
        	
        	
    }
    
    
        
        
    public void addpool(int tid, Permissions perm, HeapPage hp) {
        	HashMap<HeapPage,ArrayList<SimpleEntry<Integer, Permissions>>> myMap = gecahe();
        	ArrayList<SimpleEntry<Integer, Permissions>> l = new ArrayList<SimpleEntry<Integer, Permissions>>();
        	if(myMap.get(hp)!=null) {
        		
        		l = myMap.get(hp);
        		if(l.contains(new SimpleEntry<Integer, Permissions>(tid, perm))) {
        			return;
        		}
        	}
        	l.add(new SimpleEntry<Integer,Permissions>(tid, perm));
        	myMap.remove(hp);
        	myMap.put(hp, l);
        	setcache(myMap);
    }

        /**
         * Releases the lock on a page.
         * Calling this is very risky, and may result in wrong behavior. Think hard
         * about who needs to call this and why, and why they can run the risk of
         * calling it.
         *
         * @param tid the ID of the transaction requesting the unlock
         * @param tableID the ID of the table containing the page to unlock
         * @param pid the ID of the page to unlock
         */
   public  void releasePage(int tid, int tableId, int pid) {
            // your code here
        	HeapPage page= Database.getCatalog().getDbFile(tableId).readPage(pid);
        	if(page==null) {
        		return;
        	}
        	else {
        		if(gecahe().containsKey(page)) {
        			for ( HeapPage key : gecahe().keySet() ) {
        				if(key.check(page)) {
        					page=key;
        					break;
        				}
        			}
        			
        		}
        	}
        	HashMap<HeapPage,ArrayList<SimpleEntry<Integer, Permissions>>> myMap = gecahe();
        	ArrayList<SimpleEntry<Integer, Permissions>> permis = myMap.get(page);
        	
        	for(int i=0;i<permis.size();i++) {
        		if(permis.get(i).getKey()==tid) {
        			if(permis.get(i).getValue().permLevel==1) {
        				page.setDirty(false);
        			}
        			permis.remove(i);
        			break;
        			
        		}
        	}
        	myMap.put(page, permis);
        	setcache(myMap);
        	
        	
    }

        /** Return true if the specified transaction has a lock on the specified page */
    public   boolean holdsLock(int tid, int tableId, int pid) {
            
        	HeapFile hf = Database.getCatalog().getDbFile(tableId);
        	HeapPage hp = hf.readPage(pid);
        	if(hp==null) {
        		return false;
        	}
        	
        	//HeapPage page = getPageFromCache(tableId, pid);
        	
        	
        	HashMap<HeapPage,ArrayList<SimpleEntry<Integer, Permissions>>> myMap = gecahe();
        	
        	if(myMap.containsKey(hp)){
        		for(int i = 0; i < myMap.get(hp).size(); i++){
        			if(myMap.get(hp).get(i).getKey() == tid){
        				return true;
        			}
        		}
        	}
            return false;
     }

        /**
         * Commit or abort a given transaction; release all locks associated to
         * the transaction. If the transaction wishes to commit, write
         *
         * @param tid the ID of the transaction requesting the unlock
         * @param commit a flag indicating whether we should commit or abort
         */
     public void transactionComplete(int tid, boolean commit)
            throws IOException {
           
        	ArrayList<HeapPage> plist=new ArrayList<HeapPage>();
        	ArrayList<SimpleEntry<Integer, Permissions>> l = new ArrayList<SimpleEntry<Integer, Permissions>>();
        	Iterator<Entry<HeapPage,ArrayList<SimpleEntry<Integer, Permissions>>>> it = gecahe().entrySet().iterator();
        	while (it.hasNext()) {
    	    	HashMap.Entry<HeapPage,ArrayList<SimpleEntry<Integer, Permissions>>> m = it.next();
    	        l = m.getValue();
    	        if (!l.isEmpty()) {
    	        	for (SimpleEntry<Integer, Permissions> t : l) {
    	        		if(t.getKey()==tid) {
    	        			plist.add(m.getKey());
    	        		}
    	    		}
    	        }
    	    }
        	
        	if(commit){
        		for(HeapPage p: plist) {
        			if(p.isDirty()){
        				flushPage(p.getTableId(), p.getId());
        			}
        			releasePage(tid, p.getTableId(), p.getId());
        			temppage.remove(new SimpleEntry<Integer, Integer>(tid, p.getTableId()));
        		}
        	}
        	else {
        			for(HeapPage p: plist) {
        				if(p.isDirty()){
            				this.cache.remove(p);
            				continue;
            			}
        			releasePage(tid, p.getTableId(), p.getId());
        			temppage.remove(new SimpleEntry<Integer, Integer>(tid, p.getTableId()));	
        			}
        			
        	}
     }

        /**
         * Add a tuple to the specified table behalf of transaction tid.  Will
         * acquire a write lock on the page the tuple is added to. May block if the lock cannot 
         * be acquired.
         * 
         * Marks any pages that were dirtied by the operation as dirty
         *
         * @param tid the transaction adding the tuple
         * @param tableId the table to add the tuple to
         * @param t the tuple to add
         */
     public  void insertTuple(int tid, int tableId, Tuple t)
            throws Exception {
        	
        	HeapFile f = Database.getCatalog().getDbFile(tableId);
        	HeapPage h = null;
        	boolean flag = true;
        	int cap = 0;
    		if(!temppage.containsKey(new SimpleEntry<Integer, Integer>(tid, tableId))) {
    	    	for (int i = 0; i < f.getNumPages(); i ++) {
    	    		HeapPage hp = f.readPage(i);
    	    		if(hp==null) {
    	        		return;
    	        	}
    	        	else {
    	        		if(gecahe().containsKey(hp)) {
    	        			for (HeapPage key : gecahe().keySet() ) {
    	        				if(key.check(hp)) {
    	        					hp=key;
    	        					break;
    	        				}
    	        			}
    	        		}
    	        	}
    	    		if(hp.getNumberOfEmptySlots()>0) {
    	    			try {
    						h = getPage(tid, tableId, hp.getId(), Permissions.READ_WRITE);
    						if(h!=null) {
    							h.addTuple(t);
    							cache.put(h, gecahe().get(h));
    						}
    						else {
    							transactionComplete(tid, false);
    						}
    						return;
    					}
    					catch(Exception e) {}
    	    		}
    	    	}
    		}
    		else {
    			ArrayList<HeapPage> pgs = temppage.get(new SimpleEntry<Integer, Integer>(tid, tableId));
    			h = pgs.get(pgs.size()-1);
    			cap = pgs.size();
    			if(h.getNumberOfEmptySlots()!=0) {
    				flag = false;
    			}
    		}
    		
        	if(flag) {
        		if(gecahe().size()==getNumPages()) {
    	    		try {
    	    			evictPage();
    	    		}
    	    		catch (Exception e) {
    	    			System.out.println("Could not evict");
    	    			transactionComplete(tid, false);
    	    			return;
    				}
    	    	}
        		h = new HeapPage(f.getNumPages()+cap, new byte[HeapFile.PAGE_SIZE], f.getId());
        	}
        	
        	ArrayList<HeapPage> pgs = new ArrayList<HeapPage>();
        	if(cap!=0) {
        		pgs.addAll(temppage.get(new SimpleEntry<Integer, Integer>(tid, tableId)));
        	}
    		h.setDirty(true);
    		pgs.add(h);
    		temppage.put(new SimpleEntry<Integer, Integer>(tid, tableId), pgs);
        	
        	h.addTuple(t);
        	ArrayList<SimpleEntry<Integer, Permissions>> l = new ArrayList<SimpleEntry<Integer, Permissions>>();
        	l.add(new SimpleEntry<Integer, Permissions>(tid, Permissions.READ_WRITE));
        	cache.remove(h);
        	cache.put(h, l);
        	return;
    }

        /**
         * Remove the specified tuple from the buffer pool.
         * Will acquire a write lock on the page the tuple is removed from. May block if
         * the lock cannot be acquired.
         *
         * Marks any pages that were dirtied by the operation as dirty.
         *
         * @param tid the transaction adding the tuple.
         * @param tableId the ID of the table that contains the tuple to be deleted
         * @param t the tuple to add
         */
    public  void deleteTuple(int tid, int tableId, Tuple t)
            throws Exception {
            // your code here
        	
        	HeapFile f = Database.getCatalog().getDbFile(tableId);
        	HeapPage h;
        	for (int i = 0; i < f.getNumPages(); i ++) {
        		HeapPage hp = f.readPage(i);
        		
        		Iterator<Tuple> it = hp.iterator();
        		while(it.hasNext()) {
        			if((it.next()).equals(t)) {
        				try {
        					h = getPage(tid, tableId, hp.getId(), Permissions.READ_WRITE);
        					
        					h.deleteTuple(t);
        					cache.put(h, gecahe().get(h));
        				}
        				catch(Exception e) {
        					e.printStackTrace();
        					transactionComplete(tid, false);
        				}
        			}
        		}
        	}
    }

        
    private synchronized void flushPage(int tableId, int pid) throws IOException {
        	HeapFile file = Database.getCatalog().getDbFile(tableId);
        	for (HashMap.Entry<HeapPage, ArrayList<SimpleEntry<Integer, Permissions>>> entry : gecahe().entrySet()) {
        		HeapPage repage=entry.getKey();
        		if(repage.getTableId()==tableId && repage.getId() ==pid) {
        			try {
        				file.writePage(repage);
        			}catch(Exception e) {
        				e.printStackTrace();
        			}
        		}
        	}
    }

        /**
         * Discards a page from the buffer pool.
         * Flushes the page to disk to ensure dirty pages are updated on disk.
         */
    private synchronized  void evictPage() throws Exception {
            // your code here
        	HashMap<HeapPage,ArrayList<SimpleEntry<Integer, Permissions>>> myMap = gecahe();
        	if(myMap.size()!=getNumPages()) {
        		return;
        	}else {
        	Iterator<Entry<HeapPage,ArrayList<SimpleEntry<Integer, Permissions>>>> pidit = myMap.entrySet().iterator();
        	while(pidit.hasNext()) {
        		HashMap.Entry<HeapPage,ArrayList<SimpleEntry<Integer, Permissions>>> pidi = (HashMap.Entry<HeapPage,ArrayList<SimpleEntry<Integer, Permissions>>>)pidit.next();
        		if(pidi.getValue().isEmpty()) {
        			pidit.remove();
        			setcache(myMap);
        			return;
        		}
        		else {
        			boolean remove=true;
        			for(SimpleEntry<Integer,Permissions> s:pidi.getValue()) {
        				if(s.getValue().permLevel==1) {
        					remove=false;
        					break;
        				}
        			}
        			if(remove) {
        				for(SimpleEntry<Integer,Permissions> s:pidi.getValue()) {
        					releasePage(s.getKey(), pidi.getKey().getTableId(), pidi.getKey().getId());
        				}
        			}
        		}
        		if(myMap.size()==getNumPages()) {
    	    		throw new Exception();
    	    	}
        		
        	}
        }
    }
    
    protected int getNumPages() {
    	return this.numpg;
    }
    
    protected void setNumPages(int p) {
    	this.numpg=p;
    }
    
    protected HashMap<HeapPage,ArrayList<SimpleEntry<Integer,Permissions>>> gecahe() {
    	return this.cache;
    	
    }
    
    protected void setcache(HashMap<HeapPage,ArrayList<SimpleEntry<Integer, Permissions>>> h) {
    	this.cache = h;
    }
    
    public ArrayList<Integer> getBlocked() {
    	return this.blocked;
    }
    
    public void setBlocked(ArrayList<Integer> b) {
    	this.blocked = b;
    }
    

}