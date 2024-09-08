package com.lachata.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class SafeTimer extends Timer {
	private final Logger logger = LoggerFactory.getLogger(SafeTimer.class);
	private boolean taskScheduled = false;
	@Override
	public synchronized void schedule(TimerTask task, long delay) {
		logger.info("Scheduled {} task: {} <= delay" , task , delay);
		taskScheduled = true; // 작업이 스케줄되었음을 표시
		super.schedule(new SafeTimerTask(task, this), delay);
	}

	@Override
	public synchronized void schedule(TimerTask task, long delay, long period) {
		logger.info("Scheduled {} task: {} <= delay, period = {}" , task , delay, period);
		taskScheduled = true; // 작업이 스케줄되었음을 표시
		super.schedule(new SafeTimerTask(task, this), delay, period);
	}

	@Override
	public synchronized void cancel() {
		logger.info("Cancelling timer");
		taskScheduled = false; // 작업이 취소되었음을 표시
		super.cancel();
	}

	public boolean isTaskScheduled() {
		return taskScheduled;
	}

	private static class SafeTimerTask extends TimerTask {
		private final Logger logger = LoggerFactory.getLogger(SafeTimerTask.class);
		private final TimerTask originalTask;
		private final SafeTimer safeTimer;

		public SafeTimerTask(TimerTask originalTask, SafeTimer safeTimer) {
			this.originalTask = originalTask;
			this.safeTimer = safeTimer;  // SafeTimer 참조를 전달
		}

		@Override
		public void run() {
			try {
				logger.info("Timer Task Run");
				originalTask.run();
			} catch (Exception e) {
				logger.error("TimerTask failed: {}", e.getMessage());
			} finally {
				// 작업이 끝났거나 실패했을 때, SafeTimer의 플래그를 false로 설정
				safeTimer.taskScheduled = false;
				logger.info("Timer Task Completed or Failed, taskScheduled set to false");
			}
		}
	}
}

