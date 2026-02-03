package mx.com.seccionamarilla.bcm.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

	@Bean
	WebClient webClient(WebClient.Builder builder) {
		HttpClient httpClient = HttpClient.create()
	            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
	            .responseTimeout(Duration.ofSeconds(180))
	            .doOnConnected(conn ->
	                    conn.addHandlerLast(new ReadTimeoutHandler(10))
	                        .addHandlerLast(new WriteTimeoutHandler(10)));

	    return builder
	            .baseUrl("http://10.34.7.172:4300")
	            .clientConnector(new ReactorClientHttpConnector(httpClient))
	            .defaultHeaders(headers ->
	                    headers.setBasicAuth("bcm_db_user", "X9#%Su5tbe"))
	            .build();
	}

}
