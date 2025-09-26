package com.interview.dvi.config;

import com.interview.dvi.config.properties.ApprovalProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ApprovalProperties.class)
public class DviApplicationConfig {

}
