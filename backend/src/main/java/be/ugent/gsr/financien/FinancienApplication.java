package be.ugent.gsr.financien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// TODO alles van audit entity gegevens meesturen naar de frontend.
@SpringBootApplication
@EnableJpaAuditing
public class FinancienApplication {

    public static void main(final String[] args) {
        SpringApplication.run(FinancienApplication.class, args);
    }

}
