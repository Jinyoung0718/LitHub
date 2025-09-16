package com.sjy.LitHub.global.redis.pubsub;

import static com.sjy.LitHub.record.service.timer.util.TimerConstants.*;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import com.sjy.LitHub.record.service.group.wave.WaitingRoomEventSubscriber;
import com.sjy.LitHub.record.service.timer.wave.TimerEventSubscriber;

@Configuration
@Profile({"dev", "prod"})
public class RedisPubSubConfig {

	private final RedisConnectionFactory cacheRedisConnectionFactory;
	private final TimerEventSubscriber timerEventSubscriber;
	private final WaitingRoomEventSubscriber waitingRoomEventSubscriber;

	public RedisPubSubConfig(
		@Qualifier("CacheRedisConnectionFactory") RedisConnectionFactory cacheRedisConnectionFactory,
		TimerEventSubscriber timerEventSubscriber,
		WaitingRoomEventSubscriber waitingRoomEventSubscriber
	) {
		this.cacheRedisConnectionFactory = cacheRedisConnectionFactory;
		this.timerEventSubscriber = timerEventSubscriber;
		this.waitingRoomEventSubscriber = waitingRoomEventSubscriber;
	}

	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer() {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(cacheRedisConnectionFactory);
		container.addMessageListener(timerEventSubscriber, new ChannelTopic(TIMER_EVENT_TOPIC));
		container.addMessageListener(waitingRoomEventSubscriber, new ChannelTopic(WAITING_ROOM_EVENT_TOPIC));
		return container;
	}
}