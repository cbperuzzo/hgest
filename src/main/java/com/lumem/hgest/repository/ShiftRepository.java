package com.lumem.hgest.repository;

import com.lumem.hgest.model.Shift;
import com.lumem.hgest.model.StoredUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift, Long> {



}
