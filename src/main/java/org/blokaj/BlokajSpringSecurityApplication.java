package org.blokaj;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "BLokaj Spring Security API", version = "1.0",
        description = "This is an example for Spring Security"))
public class BlokajSpringSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlokajSpringSecurityApplication.class, args);
    }

}
