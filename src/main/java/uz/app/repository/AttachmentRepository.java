package uz.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.entity.Attachment;

import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
}
