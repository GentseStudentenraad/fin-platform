package be.ugent.gsr.financien.repos;

import be.ugent.gsr.financien.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
}
