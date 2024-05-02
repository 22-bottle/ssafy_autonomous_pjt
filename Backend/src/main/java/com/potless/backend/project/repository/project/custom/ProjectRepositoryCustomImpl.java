package com.potless.backend.project.repository.project.custom;

import com.potless.backend.damage.dto.controller.response.DamageResponseDTO;
import com.potless.backend.damage.dto.controller.response.ImagesResponseDTO;
import com.potless.backend.damage.entity.enums.Status;
import com.potless.backend.damage.entity.road.QDamageEntity;
import com.potless.backend.damage.entity.road.QImageEntity;
import com.potless.backend.project.dto.request.ProjectListRequestDto;
import com.potless.backend.project.dto.response.ProjectDetailResponseDto;
import com.potless.backend.project.dto.response.ProjectListResponseDto;
import com.potless.backend.project.entity.ProjectEntity;
import com.potless.backend.project.entity.QProjectEntity;
import com.potless.backend.project.entity.QTaskEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Slf4j
public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProjectRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public ProjectDetailResponseDto getProjectDetail(Long projectId) {
        QProjectEntity project = QProjectEntity.projectEntity;
        QDamageEntity damage = QDamageEntity.damageEntity;
        QImageEntity image = QImageEntity.imageEntity;
        QTaskEntity task = QTaskEntity.taskEntity;

        ProjectEntity projectEntity = queryFactory
                .selectFrom(project)
                .where(project.id.eq(projectId))
                .fetchOne();

        log.info("projectEntity = {}", projectEntity);

        List<Long> damageIds = queryFactory
                .select(task.damageEntity.id)
                .from(task)
                .where(task.projectEntity.id.eq(projectId))
                .fetch();

        log.info("damageIds = {}", damageIds);

        List<DamageResponseDTO> damageResponses = queryFactory
                .select(Projections.constructor(DamageResponseDTO.class,
                        damage.id,
                        damage.severity,
                        damage.dirX,
                        damage.dirY,
                        damage.address,
                        damage.width,
                        damage.status,
                        damage.areaEntity.areaGu,
                        damage.locationEntity.locationName,
                        damage.dtype
                ))
                .from(damage)
                .where(damage.id.in(damageIds))
                .fetch();
        log.info("damageResponses = {}", damageResponses);
        for (DamageResponseDTO damageResponseDTO : damageResponses) {
            List<ImagesResponseDTO> imagesForDamage = queryFactory
                    .select(Projections.constructor(ImagesResponseDTO.class,
                            image.id,
                            image.url,
                            image.order))
                    .from(image)
                    .where(image.damageEntity.id.eq(damageResponseDTO.getId()))
                    .fetch();
            damageResponseDTO.setImagesResponseDTOS(imagesForDamage);
        }

        return new ProjectDetailResponseDto(
                projectEntity.getProjectName(),
                projectEntity.getManagerEntity().getMemberEntity().getMemberName(),
                projectEntity.getProjectSize(),
                damageResponses
        );
    }


    @Override
    public Page<ProjectListResponseDto> findProjectAll(ProjectListRequestDto projectListRequestDto, Pageable pageable) {
        QProjectEntity project = QProjectEntity.projectEntity;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(managerIdEquals(project, projectListRequestDto.getManagerId()))
                .and(betweenDates(project, projectListRequestDto.getStart(), projectListRequestDto.getEnd()))
                .and(equalToStatus(project, projectListRequestDto.getStatus()))
                .and(containsSearchWord(project, projectListRequestDto.getWord()))
                .and(areaGuEquals(project, projectListRequestDto.getAreaId()))
                .and(project.deleted.eq(false));


        List<ProjectListResponseDto> results = queryFactory
                .select(Projections.constructor(ProjectListResponseDto.class,
                        project.projectName,
                        project.managerEntity.memberEntity.memberName.as("managerName"),
                        project.projectDate,
                        project.projectSize
                ))
                .from(project)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long countResult = queryFactory
                .select(project.count())
                .from(project)
                .where(builder)
                .fetchOne();

        long total = (countResult != null) ? countResult : 0;
        return new PageImpl<>(results, pageable, total);
    }

    //매니저
    private BooleanExpression managerIdEquals(QProjectEntity project, Long managerId) {
        if (managerId != null) {
            return project.managerEntity.id.eq(managerId);
        }
        return null;
    }

    //날짜
    private BooleanExpression betweenDates(QProjectEntity project, LocalDate start, LocalDate end) {
        if (start != null && end != null) {
            return project.createdDateTime.between(start.atStartOfDay(), end.atTime(23, 59, 59));
        }
        return null;
    }

    //상태
    private BooleanExpression equalToStatus(QProjectEntity project, Status status) {
        if (status != null) {
            return project.status.eq(status);
        }
        return null;
    }

    //검색어
    private BooleanExpression containsSearchWord(QProjectEntity project, String word) {
        if (word != null) {
            BooleanExpression managerNameMatch = project.managerEntity.memberEntity.memberName.contains(word);
            BooleanExpression projectTitleMatch = project.projectName.contains(word);
            return managerNameMatch.or(projectTitleMatch);
        }
        return null;
    }

    private BooleanExpression areaGuEquals(QProjectEntity project, Long areaId) {
        if (areaId != null) {
            return project.areaEntity.id.eq(areaId);
        }
        return null;
    }


}