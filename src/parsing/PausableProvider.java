package parsing;


public interface PausableProvider<X> extends Iterable<X>{
	public X next();
	public X peek();
	public boolean hasNext();
	public Object getCheckpoint();
	public void restoreCheckpoint(Object o);
	public X getExtent();
}
