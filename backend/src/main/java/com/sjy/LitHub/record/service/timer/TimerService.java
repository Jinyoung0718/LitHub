package com.sjy.LitHub.record.service.timer;

import org.springframework.stereotype.Service;

import com.sjy.LitHub.record.service.timer.processor.TimerPauseProcessor;
import com.sjy.LitHub.record.service.timer.processor.TimerStartProcessor;
import com.sjy.LitHub.record.service.timer.processor.TimerStopProcessor;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TimerService {

	private final TimerStartProcessor timerStartProcessor;
	private final TimerStopProcessor timerStopProcessor;
	private final TimerPauseProcessor timerPauseProcessor;

	public void start(Long roomId, Long ownerId) {
		timerStartProcessor.process(roomId, ownerId);
	}

	public void stop(Long roomId, Long requesterId) {
		timerStopProcessor.process(roomId, requesterId);
	}

	public void pause(Long roomId, Long requesterId) {
		timerPauseProcessor.pause(roomId, requesterId);
	}

	public void resume(Long roomId, Long requesterId) {
		timerPauseProcessor.resume(roomId, requesterId);
	}
}