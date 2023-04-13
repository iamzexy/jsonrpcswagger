package phoenixit.starter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import phoenixit.starter.customswagger.SwaggerUtils;

import java.util.Map;

@Configuration
@ComponentScan("phoenixit.starter.customswagger")
public class SwaggerConfig {

    @Bean
    public Map<String, Object> methodsDescription() {
        return SwaggerUtils.getMethodsDescription();
    }
}
