package pl.edu.agh.cqm.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UnirestConfiguration {

    @Bean
    public UnirestInstance getDefaultUnirestInstance(ObjectMapper objectMapper) {
        UnirestInstance unirestInstance = Unirest.spawnInstance();
        unirestInstance.config().setObjectMapper(new JacksonObjectMapper(objectMapper));
        return unirestInstance;
    }
}
