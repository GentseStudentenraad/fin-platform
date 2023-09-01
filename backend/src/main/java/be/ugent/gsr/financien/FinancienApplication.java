package be.ugent.gsr.financien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//TODO overal waar er "veel" responses kunnen zijn pagination toevoegen
// zie: https://chat.openai.com/share/3df8c348-2755-4b42-bb36-77aef87fedca
@SpringBootApplication
public class FinancienApplication {

    public static void main(final String[] args) {
        SpringApplication.run(FinancienApplication.class, args);
    }

}
