package be.ugent.gsr.financien.config;

import be.ugent.gsr.financien.domain.Gebruiker;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
public class AuditorConfig implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(((Gebruiker) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getNaam());
    }
}
