package com.sjy.LitHub.global.message;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQQueueConfig {

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