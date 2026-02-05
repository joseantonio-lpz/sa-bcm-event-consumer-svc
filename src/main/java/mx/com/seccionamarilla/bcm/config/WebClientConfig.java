package mx.com.seccionamarilla.bcm.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

	@Value("${bcm-event-properties.url-base}")
	private String urlBase;
	@Value("${bcm-event-properties.user-url-base}")
	private String user;
	@Value("${bcm-event-properties.pasz-url-base}")
	private String pasz;
	@Value("${bcm-event-properties.user-token}")
	private String userToken;
	@Value("${bcm-event-properties.pasz-token}")
	private String paszToken;

	@Bean(name = "basicAuthWebClient")
	WebClient webClient(WebClient.Builder builder) {
		HttpClient httpClient = HttpClient.create()
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
				.responseTimeout(Duration.ofSeconds(180))
				.doOnConnected(conn -> conn
						.addHandlerLast(new ReadTimeoutHandler(10))
						.addHandlerLast(new WriteTimeoutHandler(10)));

		return builder.baseUrl(urlBase)
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.defaultHeaders(headers -> headers.setBasicAuth(user, pasz))
				.build();
	}

	@Bean(name = "authTokenWebClient")
	WebClient authTokenWebClient(WebClient.Builder builder) {
		return builder.baseUrl(urlBase)
				.defaultHeaders(h -> {
			h.setBasicAuth(userToken, paszToken);
			h.setContentType(MediaType.APPLICATION_JSON);
		}).build();
	}

}
