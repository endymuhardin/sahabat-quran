package com.sahabatquran.repository;

import com.sahabatquran.domain.Mutabaah;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MutabaahRepository extends JpaRepository<Mutabaah, UUID> {
}
