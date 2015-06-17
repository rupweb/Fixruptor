package fixruptor;

import quickfix.FieldNotFound;
import quickfix.Initiator;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.TargetCompID;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

/**
 * Business logic goes here
 */
public class DisruptorToProvidersLogic implements EventHandler<FixEvent> 
{
    final RingBuffer<FixEvent> ringbuffer;
    final Translator translator;
    final Initiator i;
	Session _session = null;

    public DisruptorToProvidersLogic(Disruptor<FixEvent> output, Initiator i) 
    {
    	System.out.println("In DisruptorToProvidersLogic()");
    	
    	// Initiator is used to do stuff
    	this.i = i;
    	
        // translator will be used to write events into the buffer
        this.translator = new Translator();
        
        // get a hold of the ringbuffer, we can't publish direct to Disruptor as the DSL doesn't
        // provide a garbage-free two-arg publishEvent method
        this.ringbuffer = output.getRingBuffer();
    }

    /// process events
    public void onEvent(FixEvent event, long sequence, boolean endOfBatch) throws FieldNotFound 
    {
    	// What this needs to do is get the event to the initiator...
    	System.out.println(Utils.now() + "EVENT: DisruptorToProviders " + event.message.toString() + ", sequence: " + sequence + ", endOfBatch: " + endOfBatch);
    	System.out.println("initiator: " + i.getSessions().get(0).toString());
    	
    	quickfix.Message m = event.message;
    	
    	// Replace the TargetCompID
    	m.getHeader().setField(new TargetCompID("A_FX"));
    	
        // Get the correct client session
        TargetCompID t = new TargetCompID();
    	SessionID id = new SessionID("FIX.4.4", "A_FX", m.getHeader().getField(t).toString());
    	
        _session = Session.lookupSession(id);

        // Don't just send on all message types
        
		String type = event.message.getClass().getTypeName();
			
	    switch (type)
	    {
	        case "quickfix.fix44.Heartbeat":
	        	// Heartbeat, do nothing
	        	System.out.println(Utils.now() + "HEARTBEAT: do nothing");
	        	break;        		
	        		
	        case "quickfix.fix44.Logout":
	        	// Heartbeat, do nothing
	        	System.out.println(Utils.now() + "LOGOUT: do nothing");
	        	break;
	        		
	        case "quickfix.fix44.Logon":
	        	// Logon, do nothing
	        	System.out.println(Utils.now() + "LOGON: do nothing");
	        	break;
	        			
	        default:
	            FIXSend(m);
	            break;		        
	    }
		
    	System.out.println("Out DisruptorToProvidersLogic.onEvent()");
    }

	public void FIXSend(Message fm)
	{
	    // Send the FIX message
	    try
	    {
	        if (_session != null)
	        {
	            // Logging.LogInfo("SESSION: {0}, INP: {1}", _session.SessionID.ToString(), fm.toString());
	            System.out.println(Utils.now() + "SEND: " + fm.toString());
	            _session.send(fm);
	        }
	        else
	        {
	            // Logging.LogInfo("Can't send message: FIX session not created.");
	            // Logging.LogInfo(fm.toString());
	            System.out.println(Utils.now() + "Can't send message: FIX session not created.");
	            System.out.println(Utils.now() + " " + fm.toString());
	        }
	    }
	    catch (Exception e)
	    {
			System.out.println(Utils.now() + "ERROR: FIXSend" );
	    	e.printStackTrace();
	    }
    		
        // then publish direct to buffer
        // final byte[] toSendBytes = toUpperCase(event.buffer, event.length);
        // ringbuffer.publishEvent(translator, toSendBytes, event.address);
    	
    	// ringbuffer.publishEvent(translator);
    }

    private byte[] toUpperCase(byte[] input, int length) 
    {
        return new String(input).toUpperCase().getBytes();
    }
}
