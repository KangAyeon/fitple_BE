package com.fitple.fitple.service;

import com.fitple.fitple.domain.Member;
import com.fitple.fitple.repository.MemberRepository;
import com.fitple.fitple.dto.request.ProfileGenerateRequest;
import com.fitple.fitple.dto.request.ProfileRegenerateRequest;
import com.fitple.fitple.dto.request.ProfileUpdateRequest;
import com.fitple.fitple.dto.response.MessageResponse;
import com.fitple.fitple.dto.response.ProfileDetailResponse;
import com.fitple.fitple.dto.response.ProfileResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitple.fitple.dto.request.FileRequest;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProfileService {

    private final OpenAiService openAiService;
    private final MemberRepository memberRepository;

    public ProfileService(OpenAiService openAiService, MemberRepository memberRepository) {
        this.openAiService = openAiService;
        this.memberRepository = memberRepository;
    }

    private static final String SYSTEM_PROMPT = """
        당신은 사용자의 입력 정보와 첨부파일 정보를 기반으로 프로필 소개글을 작성하는 AI 핏봇입니다.
        반드시 아래의 정형화된 형식을 엄격히 지켜서 출력하세요. 다른 서론이나 결론 문구는 포함하지 마세요.

        <AI가 작성한 프로필>
        프로젝트 경험
        [요약 내용]
        담당 역할
        [요약 내용]
        사용 툴
        [요약 내용]
        협업 스타일
        [요약 내용]
        """;

    @Transactional
    public ProfileResponse generateProfile(
            Long memberId,
            ProfileGenerateRequest request
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 회원입니다.")
                );

        String userPrompt = String.format(
                "사용자 작성 소개: %s\n참고 파일: %s\n편집 가능 여부: %s",
                request.getProfileSummary(),
                formatFiles(request.getUsedFiles()),
                request.isEditable()
        );

        String aiResult =
                openAiService.callGpt(SYSTEM_PROMPT, userPrompt);

        // AI가 생성한 프로필을 DB에 저장
        member.updateIntroduction(aiResult);

        return new ProfileResponse(aiResult);
    }

    public ProfileResponse regenerateProfile(
            ProfileGenerateRequest request
    ) {

        String userPrompt = String.format(
                "사용자가 제공한 프로필 내용: %s\n참고 파일: %s\n편집 가능 여부: %s\n" +
                        "위 정보를 바탕으로 프로필을 다시 작성하세요.",
                request.getProfileSummary(),
                formatFiles(request.getUsedFiles()),
                request.isEditable()
        );

        String aiResult =
                openAiService.callGpt(SYSTEM_PROMPT, userPrompt);

        return new ProfileResponse(aiResult);
    }

    private String formatFiles(List<FileRequest> files) {

        if (files == null || files.isEmpty()) {
            return "없음";
        }

        return files.stream()
                .map(file ->
                        String.format(
                                "파일ID: %s, 파일명: %s, URL: %s",
                                file.getFileId(),
                                file.getOriginalName(),
                                file.getFileUrl()
                        )
                )
                .collect(java.util.stream.Collectors.joining("\n"));
    }

//    public ProfileResponse regenerateProfile(ProfileRegenerateRequest request) {
//        String userPrompt = String.format("기존 프로필:\n%s\n\n추가 요구사항:\n%s",
//                request.previousProfile(),
//                request.additionalPrompt());
//
//        String aiResult = openAiService.callGpt(SYSTEM_PROMPT, userPrompt);
//        return new ProfileResponse(aiResult);
//    }

    public ProfileDetailResponse getProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        return new ProfileDetailResponse(
                member.getProfileImage(),
                member.getName(),
                member.getIntroduction() != null ? member.getIntroduction().getContent() : null
        );
    }

    @Transactional
    public MessageResponse updateProfile(Long memberId, ProfileUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 프사 업데이트
        member.updateProfileImage(request.profileImage());
        // 이름은 회원가입 시 설정된 이름(request.name()) 사용 및 검증
        // 소개글(profileSummary) 저장/수정 처리
        member.updateIntroduction(request.profileSummary());

        return new MessageResponse("프로필이 수정되었습니다.");
    }
}