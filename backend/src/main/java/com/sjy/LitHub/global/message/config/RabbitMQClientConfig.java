package com.sjy.LitHub.global.message.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sjy.LitHub.global.message.utils.RabbitMQConstants;

@Configuration
public class RabbitMQClientConfig {

	@Bean
	public Queue fanoutQueue() {
		return QueueBuilder.durable(RabbitMQConstants.FANOUT_QUEUE)
			.withArgument(RabbitMQConstants.ARG_DEAD_LETTER_EXCHANGE, RabbitMQConstants.FANOUT_DLX)
			.withArgument(RabbitMQConstants.ARG_DEAD_LETTER_ROUTING_KEY, RabbitMQConstants.FANOUT_DLQ_ROUTING_KEY)
			.build();
	}

	@Bean
	public FanoutExchange fanoutExchange() {
		return new FanoutExchange(RabbitMQConstants.FANOUT_EXCHANGE);
	}

	@Bean
	public Binding fanoutBinding(Queue fanoutQueue, FanoutExchange fanoutExchange) {
		return BindingBuilder.bind(fanoutQueue).to(fanoutExchange);
	}

	@Bean
	public Queue postDeletedQueue() {
		return QueueBuilder.durable(RabbitMQConstants.POST_DELETED_QUEUE).build();
	}

	@Bean
	public DirectExchange postDeletedExchange() {
		return new DirectExchange(RabbitMQConstants.POST_DELETED_EXCHANGE);
	}

	@Bean
	public Binding postDeletedBinding(Queue postDeletedQueue, DirectExchange postDeletedExchange) {
		return BindingBuilder.bind(postDeletedQueue)
			.to(postDeletedExchange)
			.with(RabbitMQConstants.POST_DELETED_ROUTING_KEY);
	}

	@Bean
	public Queue likeQueue() {
		return QueueBuilder.durable(RabbitMQConstants.LIKE_QUEUE).build();
	}

	@Bean
	public DirectExchange likeExchange() {
		return new DirectExchange(RabbitMQConstants.LIKE_EXCHANGE);
	}

	@Bean
	public Binding likeBinding(Queue likeQueue, DirectExchange likeExchange) {
		return BindingBuilder.bind(likeQueue)
			.to(likeExchange)
			.with(RabbitMQConstants.LIKE_ROUTING_KEY);
	}

	@Bean
	public Queue scrapQueue() {
		return QueueBuilder.durable(RabbitMQConstants.SCRAP_QUEUE).build();
	}

	@Bean
	public DirectExchange scrapExchange() {
		return new DirectExchange(RabbitMQConstants.SCRAP_EXCHANGE);
	}

	@Bean
	public Binding scrapBinding(Queue scrapQueue, DirectExchange scrapExchange) {
		return BindingBuilder.bind(scrapQueue)
			.to(scrapExchange)
			.with(RabbitMQConstants.SCRAP_ROUTING_KEY);
	}


	@Bean
	public Queue fanoutDLQ() {
		return QueueBuilder.durable(RabbitMQConstants.FANOUT_DLQ).build();
	}

	@Bean
	public DirectExchange fanoutDLX() {
		return new DirectExchange(RabbitMQConstants.FANOUT_DLX);
	}

	@Bean
	public Binding dlqBinding() {
		return BindingBuilder
			.bind(fanoutDLQ())
			.to(fanoutDLX())
			.with(RabbitMQConstants.FANOUT_DLQ_ROUTING_KEY);
	}
}