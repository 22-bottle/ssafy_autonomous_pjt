package HarryPort.Backend.member.entity;

import HarryPort.Backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "worker")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkerEntity extends BaseEntity {

    @Id
    @Column(name = "worker_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinColumn(name = "manager_id")
    private ManagerEntity managerEntity;

    @Builder
    public WorkerEntity(Long id, MemberEntity memberEntity, ManagerEntity managerEntity) {
        this.id = id;
        this.memberEntity = memberEntity;
        this.managerEntity = managerEntity;
    }

    public void changeMangaer(ManagerEntity managerEntity) {
        this.managerEntity = managerEntity;
    }
}
