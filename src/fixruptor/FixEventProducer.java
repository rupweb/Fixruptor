package fixruptor;

import quickfix.Message;

import com.lmax.disruptor.RingBuffer;

public class FixEventProducer {
	
	private final RingBuffer<FixEvent> ringBuffer;
	
	public FixEventProducer(RingBuffer<FixEvent> ringBuffer)
	{
		this.ringBuffer = ringBuffer;
	}
	
    public void onData(Message message)
    {
        long sequence = ringBuffer.next();  // Grab the next sequence
        try
        {
            FixEvent event = ringBuffer.get(sequence); // Get the entry in the Disruptor for the sequence
            event.set(message);  // Fill with data
        }
        finally
        {
            ringBuffer.publish(sequence);
        }
    }


}
