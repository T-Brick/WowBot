package com.estrelsteel.wowbot.command.audio;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

import com.estrelsteel.wowbot.command.audio.queue.QueueReferenceData;
import com.estrelsteel.wowbot.command.audio.skip.SkipReferenceData;

public class AudioOperations {
	private Consumer<QueueReferenceData> queueReset;
	private Callable<Long> queueMessageId;
	private Consumer<SkipReferenceData> skip;

	public Consumer<QueueReferenceData> getQueueReset() {
		return queueReset;
	}

	public AudioOperations setQueueReset(Consumer<QueueReferenceData> queueReset) {
		this.queueReset = queueReset;
		return this;
	}
	
	public Callable<Long> getQueueMessageId() {
		return queueMessageId;
	}
	
	public AudioOperations setQueueMessageId(Callable<Long> queueMessageId) {
		this.queueMessageId = queueMessageId;
		return this;
	}

	public Consumer<SkipReferenceData> getSkip() {
		return skip;
	}

	public AudioOperations setSkip(Consumer<SkipReferenceData> skip) {
		this.skip = skip;
		return this;
	}
	
	
}
