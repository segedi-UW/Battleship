/**
 * An interface to allow pluggable listener objects
 * 
 * @author youngAgFox
 *
 */
public interface ConnectionListener {

	/**
	 * Is notified when a connection is first established.
	 */
	public void connected();
}
