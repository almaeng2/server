package com.api.almaeng2.domain.member.entity;

import com.api.almaeng2.domain.base.BaseTimeEntity;
import com.api.almaeng2.domain.medicine.entity.PrescriptionList;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "MEMBER_TABLE")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Level level;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private LocalDate birth;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member", orphanRemoval = true)
    private List<PrescriptionList> prescriptionLists = new ArrayList<>();
}
