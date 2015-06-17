package fixruptor;

import quickfix.Message;

/**
 * Value event
 * This is the value event we will use to pass data between threads
 */
public class FixEvent  
{
    protected Message message;
    
	public void set(Message m) 
	{
    	System.out.println(Utils.now() + "In FixEvent.set() with message: " + m.toString());
		this.message = m;
	}
}
