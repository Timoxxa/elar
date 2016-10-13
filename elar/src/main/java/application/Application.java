package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.regex.Pattern;

/**
 * Created by Timofey on 12.10.2016.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Pattern patternForParsePasswordLine() {
//        String regexp = "^(\\d{1,19})\\t([0-9a-fA-F]+)$";
        String regexp = "^(.+)\\t([0-9a-fA-F]+)$";
        return Pattern.compile(regexp);
    }

}
