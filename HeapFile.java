package hw1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A heap file stores a collection of tuples. It is also responsible for managing pages.
 * It needs to be able to manage page creation as well as correctly manipulating pages
 * when tuples are added or deleted.
 * @author Sam Madden modified by Doug Shook
 *
 */
public class HeapFile {
	
public static final int PAGE_SIZE = 4096;
	
	/**
	 * Creates a new heap file in the given location that can accept tuples of the given type
	 * @param f location of the heap file
	 * @param types type of tuples contained in the file
	 */
	
	private File f;
	private TupleDesc type;
	
	public HeapFile(File f, TupleDesc type) {
		
		//your code here
		this.f = f;
		this.type = type;
	}
	
	public File getFile() {
		return f;
		//your code here
		
	}
	
	public TupleDesc getTupleDesc() {
		//your code here
		return type;
	}
	
	/**
	 * Creates a HeapPage object representing the page at the given page number.
	 * Because it will be necessary to arbitrarily move around the file, a RandomAccessFile object
	 * should be used here.
	 * @param id the page number to be retrieved
	 * @return a HeapPage at the given page number
	 */
	public HeapPage readPage(int id) {
		byte[] page = new byte[PAGE_SIZE];
		RandomAccessFile getpage = null;
		
		try {
			getpage = new RandomAccessFile(f, "r");
			getpage.seek(PAGE_SIZE * id);
			getpage.read(page, 0, PAGE_SIZE);
			getpage.close();
			return new HeapPage(id, page, getId());
			} catch (IOException m) 
		{m.printStackTrace();
			  }
		//your code here
		return null;
	}
	
	/**
	 * Returns a unique id number for this heap file. Consider using
	 * the hash of the File itself.
	 * @return
	 */
	public int getId() {
		//your code here
		return f.hashCode();
	}
	
	/**
	 * Writes the given HeapPage to disk. Because of the need to seek through the file,
	 * a RandomAccessFile object should be used in this method.
	 * @param p the page to write to disk
	 */
	public void writePage(HeapPage p) {
		RandomAccessFile page = null;
		try {
			page = new RandomAccessFile(f, "rw");
			page.seek(p.getId() * PAGE_SIZE);
			page.write(p.getPageData(), 0 , PAGE_SIZE);
			page.close();
		}catch (IOException e)
		{e.printStackTrace();}
		
	
		
		//your code here
	}
	
	/**
	 * Adds a tuple. This method must first find a page with an open slot, creating a new page
	 * if all others are full. It then passes the tuple to this page to be stored. It then writes
	 * the page to disk (see writePage)
	 * @param t The tuple to be stored
	 * @return The HeapPage that contains the tuple
	 * @throws Exception 
	 */
	
	
	public HeapPage addTuple(Tuple t) {
		int count = 0;
		int numPage = this.getNumPages();
		HeapPage currPage = null;
		for (int i=0; i<numPage; i++) {
			currPage = readPage(i);
			try {
				currPage.addTuple(t);
				this.writePage(currPage);
				return currPage;
			} catch (Exception e) {
				count++;
			}
		}
		if (numPage == count) {
			HeapPage newPage;
			try {
				newPage = new HeapPage(numPage, new byte[this.PAGE_SIZE], this.getId());
				try {
					newPage.addTuple(t);
					this.writePage(newPage);
				} catch (Exception e) {
				}
				return newPage;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
				return currPage;
		}
		return currPage;
	}
	
	/**
	 * This method will examine the tuple to find out where it is stored, then delete it
	 * from the proper HeapPage. It then writes the modified page to disk.
	 * @param t the Tuple to be deleted
	 */
	public void deleteTuple(Tuple t){
		HeapPage page=this.readPage(t.getId());
		page.deleteTuple(t);
		this.writePage(page);
		//your code here
	}
	
	/**
	 * Returns an ArrayList containing all of the tuples in this HeapFile. It must
	 * access each HeapPage to do this (see iterator() in HeapPage)
	 * @return
	 */
	public ArrayList<Tuple> getAllTuples() {
		
		ArrayList<Tuple> myList = new ArrayList<Tuple>();
        for (int i = 0; i < getNumPages(); i ++) {
        	HeapPage hp = readPage(i);
        	Iterator<Tuple> tupIterator = hp.iterator();
    		while (tupIterator.hasNext()) {
    			myList.add(tupIterator.next());
    		}
        }
		
		return myList;
		
		
		//your code here
		
	}
	
	/**
	 * Computes and returns the total number of pages contained in this HeapFile
	 * @return the number of pages
	 */
	public int getNumPages() {
		//your code here
		return (int)Math.ceil((double)(f.length()/PAGE_SIZE));
	}
}
