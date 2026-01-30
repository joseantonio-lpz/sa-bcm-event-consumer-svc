package mx.com.seccionamarilla.bcm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    exclude = {
        org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration.class
    }
)
public class BCMApplication {

	public static void main(String[] args) {
		SpringApplication.run(BCMApplication.class, args);
	}

}
