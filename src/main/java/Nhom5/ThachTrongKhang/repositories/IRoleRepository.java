package Nhom5.ThachTrongKhang.repositories;

import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Repository;

import Nhom5.ThachTrongKhang.entities.Role; 
 
@Repository 
public interface IRoleRepository extends JpaRepository<Role, Long>{ 
    Role findRoleById(Long id); 
} 
