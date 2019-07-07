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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.alibaba.demo.ConsumerApplication.MySink;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.binding.AbstractBindingTargetFactory;
import org.springframework.cloud.stream.binding.BindingTargetFactory;
import org.springframework.cloud.stream.binding.CompositeMessageChannelConfigurer;
import org.springframework.cloud.stream.binding.MessageChannelConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
@EnableBinding(MySink.class)
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    public interface MySink {
        @Input("input")
        PublishSubscribeChannel input();

        //@Input("pollInput")
        //PollableMessageSource pollInput();
    }

    public static final String TRANSFORM_NAME = "transform-channel";
    public static final String FILTER_NAME = "filter-channel";
    public static final String DISCARD_NAME = "discard-channel";

    @Bean(DISCARD_NAME)
    public MessageChannel discardChannel() {
        return new DirectChannel();
    }

    @Bean(FILTER_NAME)
    public MessageChannel filterChannel() {
        return new DirectChannel();
    }

    @Bean(TRANSFORM_NAME)
    public MessageChannel transformChannel() {
        return new DirectChannel();
    }


    class CustomBindingTargetFactory extends AbstractBindingTargetFactory<PublishSubscribeChannel> {

        private final MessageChannelConfigurer messageChannelConfigurer;

        public CustomBindingTargetFactory(MessageChannelConfigurer messageChannelConfigurer) {
            super(PublishSubscribeChannel.class);
            this.messageChannelConfigurer = messageChannelConfigurer;
        }

        @Override
        public PublishSubscribeChannel createInput(String name) {
            PublishSubscribeChannel channel = new PublishSubscribeChannel();
            this.messageChannelConfigurer.configureInputChannel(channel, name);
            return channel;
        }

        @Override
        public PublishSubscribeChannel createOutput(String name) {
            PublishSubscribeChannel channel = new PublishSubscribeChannel();
            this.messageChannelConfigurer.configureOutputChannel(channel, name);
            return channel;
        }
    }

    @Bean
    @Primary
    public BindingTargetFactory customBindingTargetFactory(
        CompositeMessageChannelConfigurer compositeMessageChannelConfigurer) {
        return new CustomBindingTargetFactory(compositeMessageChannelConfigurer);
    }

    //@Autowired
    //private MySink mySink;
    //@Bean
    //public CommandLineRunner runner() {
    //    return args -> {
    //        while (true) {
    //            mySink.pollInput().poll(m -> {
    //                String payload = (String) m.getPayload();
    //                System.out.println("pull msg: " + payload);
    //            }, new ParameterizedTypeReference<String>() {
    //            });
    //            Thread.sleep(2_000);
    //        }
    //    };
    //}

}
