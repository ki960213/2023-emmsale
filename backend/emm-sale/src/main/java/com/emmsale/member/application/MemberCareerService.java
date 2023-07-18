package com.emmsale.member.application;

import static java.util.stream.Collectors.toList;

import com.emmsale.career.domain.CareerRepository;
import com.emmsale.member.application.dto.MemberCareerAddRequest;
import com.emmsale.member.application.dto.MemberCareerDeleteRequest;
import com.emmsale.member.application.dto.MemberCareerInitialRequest;
import com.emmsale.member.application.dto.MemberCareerResponse;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberCareer;
import com.emmsale.member.domain.MemberCareerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCareerService {

  private final MemberCareerRepository memberCareerRepository;
  private final CareerRepository careerRepository;

  public void registerCareer(
      final Member member,
      final MemberCareerInitialRequest memberCareerInitialRequest
  ) {
    final List<Long> careerIds = memberCareerInitialRequest.getCareerIds();
    saveMemberCareers(member, careerIds);

    member.updateName(memberCareerInitialRequest.getName());
  }

  private void saveMemberCareers(final Member member, final List<Long> careerIds) {
    final List<MemberCareer> memberCareers = careerRepository.findAllById(careerIds)
        .stream()
        .map(it -> new MemberCareer(it, member))
        .collect(toList());
    memberCareerRepository.saveAll(memberCareers);
  }

  public List<MemberCareerResponse> addCareer(
      final Member member,
      final MemberCareerAddRequest memberCareerAddRequest
  ) {
    final List<Long> careerIds = memberCareerAddRequest.getCareerIds();
    saveMemberCareers(member, careerIds);

    return MemberCareerResponse.from(memberCareerRepository.findAllByMemberId(member.getId()));
  }

  public List<MemberCareerResponse> deleteCareer(
      final Member member,
      final MemberCareerDeleteRequest memberCareerDeleteRequest
  ) {
    final List<Long> deleteCareerIds = memberCareerDeleteRequest.getCareerIds();

    final List<Long> savedMemberCareerIds =
        memberCareerRepository.findAllByMemberAndCareerIds(member, deleteCareerIds)
            .stream()
            .map(MemberCareer::getId)
            .collect(toList());

    memberCareerRepository.deleteAllByIdInBatch(savedMemberCareerIds);

    return MemberCareerResponse.from(memberCareerRepository.findAllByMemberId(member.getId()));
  }
}

