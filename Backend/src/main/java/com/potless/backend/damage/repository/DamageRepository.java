package com.potless.backend.damage.repository;


import com.potless.backend.damage.dto.controller.response.DamageResponseDTO;
import com.potless.backend.damage.entity.enums.Status;
import com.potless.backend.damage.entity.road.DamageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface DamageRepository extends JpaRepository<DamageEntity, Long>, DamageRepositoryCustom, QuerydslPredicateExecutor<DamageEntity> {

    @Query("SELECT new com.potless.backend.damage.dto.controller.response.DamageResponseDTO(" +
            "d.id, d.severity, d.dirX, d.dirY, d.address, d.width, d.status, " +
            "area.areaGu, location.locationName, d.dtype, d.createdDateTime) " +
            "FROM DamageEntity d " +
            "LEFT JOIN d.areaEntity area " +
            "LEFT JOIN d.locationEntity location " +
            "WHERE d.id = :damageId")
    DamageResponseDTO findDamageDetailsByIdSimple(@Param("damageId") Long damageId);

    boolean existsByProjectIdAndStatus(Long projectId, Status status);
}