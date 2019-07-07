/*
 * Copyright (C) 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.alibaba.demo;

import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
public class MessageApplication {

    private static final Logger logger = LoggerFactory.getLogger(MessageApplication.class);

    public static void main(String[] args) {
        new SpringApplicationBuilder(MessageApplication.class).web(WebApplicationType.NONE).run(args);
    }

    @Bean
    public CommandLineRunner runner1() {
        return args -> {
            Thread.sleep(2000L);
            logger.info("runner1 =============");
            DirectChannel directChannel = new DirectChannel();
            directChannel.subscribe(msg -> {
                logger.info("handler1 -> " + msg.getPayload());
            });
            directChannel.subscribe(msg -> {
                logger.info("handler2 -> " + msg.getPayload());
            });

            directChannel.send(MessageBuilder.withPayload("simple msg 1").build());
            directChannel.send(MessageBuilder.withPayload("simple msg 2").build());
        };
    }

    @Bean
    public CommandLineRunner runner2() {
        return args -> {
            Thread.sleep(4000L);
            logger.info("runner2 =============");
            PublishSubscribeChannel directChannel = new PublishSubscribeChannel();
            directChannel.subscribe(msg -> {
                logger.info("handler1 -> " + msg.getPayload());
            });
            directChannel.subscribe(msg -> {
                logger.info("handler2 -> " + msg.getPayload());
            });

            directChannel.send(MessageBuilder.withPayload("simple msg 1").build());
            directChannel.send(MessageBuilder.withPayload("simple msg 2").build());
        };
    }

    @Bean
    public CommandLineRunner runner3() {
        return args -> {
            Thread.sleep(6000L);
            logger.info("runner3 =============");
            ExecutorChannel executorChannel = new ExecutorChannel(Executors.newFixedThreadPool(2));
            executorChannel.subscribe(msg -> {
                logger.info("handler1 -> " + msg.getPayload());
            });
            executorChannel.subscribe(msg -> {
                logger.info("handler2 -> " + msg.getPayload());
            });

            executorChannel.send(MessageBuilder.withPayload("simple msg 1").build());
            executorChannel.send(MessageBuilder.withPayload("simple msg 2").build());
        };
    }

    @Bean
    public CommandLineRunner runner4() {
        return args -> {
            Thread.sleep(8000L);
            logger.info("runner4 =============");
            DirectChannel directChannel = new DirectChannel();

            directChannel.addInterceptor(new ChannelInterceptor() {
                @Override
                public Message<?> preSend(Message<?> message, MessageChannel channel) {
                    logger.info("stop send");
                    return null;
                }
            });

            directChannel.subscribe(msg -> {
                logger.info("handler1 -> " + msg.getPayload());
            });

            directChannel.send(MessageBuilder.withPayload("simple msg 1").build());
            directChannel.send(MessageBuilder.withPayload("simple msg 2").build());
        };
    }

}
