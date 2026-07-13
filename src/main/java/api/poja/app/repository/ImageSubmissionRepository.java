package api.poja.app.repository;

import api.poja.app.entity.ImageSubmission;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageSubmissionRepository extends JpaRepository<ImageSubmission, UUID> {}
