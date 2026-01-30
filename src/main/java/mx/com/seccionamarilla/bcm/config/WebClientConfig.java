package mx.com.seccionamarilla.bcm.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

	@Bean
	WebClient webClient(WebClient.Builder builder) {
		HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofSeconds(10));
		
		return WebClient.builder()
                .baseUrl("https://wscap.dev.seccionamarilla.com")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
	}
}
