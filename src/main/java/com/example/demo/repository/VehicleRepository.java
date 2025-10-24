package com.example.demo.repository;

import com.example.demo.model.User;
import com.example.demo.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByCreatedBy (User user);
}
