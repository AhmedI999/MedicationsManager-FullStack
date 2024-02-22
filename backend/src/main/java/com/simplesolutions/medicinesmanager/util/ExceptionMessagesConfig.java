package com.simplesolutions.medicinesmanager.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
@PropertySource("classpath:user-messages.properties")
public class ExceptionMessagesConfig {

    @Bean
    public ReloadableResourceBundleMessageSource exceptionMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/user-messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
