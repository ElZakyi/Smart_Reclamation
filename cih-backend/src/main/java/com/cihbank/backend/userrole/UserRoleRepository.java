package com.cihbank.backend.userrole;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole,UserRoleId> {
    List<UserRole> findByUserIdUser(Integer userId);
    List<UserRole> findByRoleIdRole(Integer roleId);
    void deleteByUser_IdUser(Integer idUser);
}
