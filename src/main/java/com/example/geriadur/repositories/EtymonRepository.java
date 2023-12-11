package com.example.geriadur.repositories;

import com.example.geriadur.domain.Etymon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface EtymonRepository extends JpaRepository<Etymon, Long> {
    Optional<Etymon> findByEtymonName(String etymonName);
}
