package com.example.backendglobaldirectory.repository;

import com.example.backendglobaldirectory.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    List<User> findByApproved(boolean approved);
    List<User> findByActive(boolean active);

    @Query("SELECT u FROM User u WHERE " +
            "(LOWER(CONCAT(u.firstName, CONCAT(' ', u.lastName))) =:searchData " +
            "or LOWER(CONCAT(u.lastName, CONCAT(' ', u.firstName))) =:searchData " +
            "or LOWER(u.lastName)=:searchData or LOWER(u.firstName) =:searchData " +
            "or LOWER(u.team)=:searchData or LOWER(u.department)=:searchData " +
            "or LOWER(u.jobTitle) =:searchData or LOWER(u.firstName) like :searchData% " +
            "or LOWER(u.lastName) like :searchData% or u.team like :searchData% " +
            "or LOWER(u.jobTitle) like :searchData% ) " +
            "and u.approved = true and u.role='user' " +
            "ORDER BY u.lastName limit :sizeLimit offset :startOffset")
    List<User> searchUsersData(String searchData, int sizeLimit, int startOffset);

    @Query("SELECT u FROM User u WHERE u.approved=true and u.role='user' " +
            "ORDER BY u.lastName limit :sizeLimit offset :startOffset")
    List<User> findAllUserSearch(int sizeLimit, int startOffset);

    @Query("SELECT COUNT(u.id)  FROM User u WHERE " +
            "(LOWER(CONCAT(u.firstName, CONCAT(' ', u.lastName))) =:searchData " +
            "or LOWER(CONCAT(u.lastName, CONCAT(' ', u.firstName))) =:searchData " +
            "or LOWER(u.lastName)=:searchData or LOWER(u.firstName) =:searchData " +
            "or LOWER(u.team)=:searchData or LOWER(u.department)=:searchData " +
            "or LOWER(u.jobTitle) =:searchData or LOWER(u.firstName) like :searchData% " +
            "or LOWER(u.lastName) like :searchData% or u.team like :searchData% " +
            "or LOWER(u.jobTitle) like :searchData% ) " +
            "and u.approved = true and u.role='user'")
    int countAllSearch(String searchData);

    @Query("SELECT COUNT(u.id) FROM User u WHERE u.approved=true and u.role='user' ")
    int countAll();
}
