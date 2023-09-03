package be.ugent.gsr.financien.domain;

import be.ugent.gsr.financien.model.GebruikerType;
import be.ugent.gsr.financien.repos.SubBudgetPostRepository;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Gebruiker implements UserDetails {


    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(name = "primary_sequence", sequenceName = "primary_sequence", allocationSize = 1, initialValue = 10000)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "primary_sequence")
    private Integer id;

    @Column(nullable = false)
    private String naam;

    @Column(nullable = false)
    private String telNr;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String wachtwoord;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GebruikerType type;

    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "gebruiker_id"), inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private Set<Rol> rol;

    @ManyToMany
    @JoinTable(name = "user_organisatie", joinColumns = @JoinColumn(name = "gebruiker_id"), inverseJoinColumns = @JoinColumn(name = "organisatie_id"))
    private Set<Organisatie> organisatie;

    @OneToMany(mappedBy = "gebruiker")
    private Set<Kost> costs;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.type.name()));
    }

    @Override
    public String getPassword() {
        return this.wachtwoord;
    }

    @Override
    public String getUsername() {
        return this.naam;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public boolean isBeheerder() {
        return this.type.equals(GebruikerType.BEHEERDER);
    }

    public boolean isVoorzitter() {
        return this.type.equals(GebruikerType.VOORZITTER);
    }

    public boolean isVoorzitterOrBeheerder() {
        return isBeheerder() || isVoorzitter();
    }

    public List<SubBudgetPost> visibleBudgetPosts(SubBudgetPostRepository subBudgetPostRepository, Integer jaar) {
        if (this.isVoorzitterOrBeheerder()) {
            return subBudgetPostRepository.findAll().stream().filter(
                    //Filter so that only the subBudgetPosts of the given year get added
                    subBudgetPost -> Objects.equals(subBudgetPost.getBudgetPost().getBoekjaar().getJaartal(), jaar)).toList();
        } else {
            // All subBudgetPosts by organisation roles
            Set<Rol> rollen = this.organisatie.stream().flatMap(org -> org.getRol().stream()).collect(Collectors.toSet());
            // All subBudgetPosts by this users roles
            rollen.addAll(this.rol);
            return rollen.stream().flatMap(rol -> rol.getSubBudgetPost().stream()).filter( //Filter so that only the subBudgetPosts of the given year get added
                    subBudgetPost -> Objects.equals(subBudgetPost.getBudgetPost().getBoekjaar().getJaartal(), jaar)).toList();
        }
    }

    public List<SubBudgetPost> visibleBudgetPosts(SubBudgetPostRepository subBudgetPostRepository, SubBudgetPost subBudgetPost) {
        return this.visibleBudgetPosts(subBudgetPostRepository, subBudgetPost.getBudgetPost().getBoekjaar().getJaartal());
    }
}