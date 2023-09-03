package be.ugent.gsr.financien.config;

import be.ugent.gsr.financien.domain.Gebruiker;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorConfig implements AuditorAware<String> {
    @Override
    public @NotNull Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(((Gebruiker) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getNaam());
    }
}
