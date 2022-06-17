package parsing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListScanner<E> implements PausableProvider<E> {
	private final List<E> list;
	private int index = 0;
	private int maxIndex = 0;
	public Object getCheckpoint() {
		return index;
	}
	public void restoreCheckpoint(Object checkpoint) {
		if(!(checkpoint instanceof Integer)) {
			throw new UnsupportedOperationException("Can only revert ListScanner to an integer index");
		}
		index = (Integer)checkpoint;
	}
	
	public ListScanner (List<E> ls) {
		list = new ArrayList<>();
		list.addAll(ls);
	}
	@Override
	public E next() {
		if(hasNext()) {
			maxIndex = Math.max(maxIndex, index + 1);
			return list.get(index++);
		} else {
			throw new ArrayIndexOutOfBoundsException(index);
		}
	}
	@Override
	public E peek() {
		return list.get(index);
	}
	@Override
	public boolean hasNext() {
		return index < list.size();
	}
	@Override
	public Iterator<E> iterator() {
		final int currentIndex = index;
		return new Iterator<E>() {
			int index = currentIndex;
			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return index < list.size();
			}

			@Override
			public E next() {
				return list.get(index++);
			}
			
		};
	}
	@Override
	public E getExtent() {
		return list.get(maxIndex-1);
	}
	
}
