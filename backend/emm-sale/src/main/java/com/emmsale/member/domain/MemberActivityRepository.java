package com.emmsale.member.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberActivityRepository extends JpaRepository<MemberActivity, Long> {

  @Query("select mc from MemberActivity mc where mc.member = :member")
  List<MemberActivity> findAllByMember(@Param("member") final Member member);

  @Query("select mc from MemberActivity mc "
      + "where mc.member = :member "
      + "and mc.activity.id in :deleteCareerIds")
  List<MemberActivity> findAllByMemberAndCareerIds(
      @Param("member") final Member member,
      @Param("deleteCareerIds") final List<Long> deleteCareerIds
  );
}
