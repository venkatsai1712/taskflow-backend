package venkatsai.taskflow.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue messagingQueue(){
        return new Queue("messaging-queue", true);
    }

    @Bean
    public Queue analyticsQueue(){
        return new Queue("analytics-queue", true);
    }
    
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("direct-exchange");
    }

    @Bean
    public Binding messageBinding(){
        return BindingBuilder
                .bind(messagingQueue())
                .to(directExchange())
                .with("email");
    }

    @Bean
    public Binding analyticsBinding(){
        return BindingBuilder
                .bind(analyticsQueue())
                .to(directExchange())
                .with("analytics");
    }
}
