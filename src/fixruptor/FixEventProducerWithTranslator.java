package fixruptor;

import quickfix.Message;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.EventTranslatorOneArg;

public class FixEventProducerWithTranslator 
{
	private final RingBuffer<FixEvent> ringBuffer;

	public FixEventProducerWithTranslator(RingBuffer<FixEvent> ringBuffer)
	{
		this.ringBuffer = ringBuffer;
	}

	private static final EventTranslatorOneArg<FixEvent, Message> TRANSLATOR =
			new EventTranslatorOneArg<FixEvent, Message>()
	{
		public void translateTo(FixEvent event, long sequence, Message m)
		{
			event.set(m);
		}
	};

	public void onData(Message m)
	{
		ringBuffer.publishEvent(TRANSLATOR, m);
	}
}
