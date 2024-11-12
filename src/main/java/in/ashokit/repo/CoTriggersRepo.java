package in.ashokit.repo;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import in.ashokit.entity.CoTriggersEntity;

public interface CoTriggersRepo extends JpaRepository<CoTriggersEntity, Serializable>{

}
