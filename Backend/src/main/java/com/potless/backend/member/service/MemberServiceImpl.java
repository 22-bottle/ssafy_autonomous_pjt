package com.potless.backend.member.service;


import com.potless.backend.global.exception.member.*;
import com.potless.backend.damage.dto.controller.response.DamageResponseDTO;
import com.potless.backend.global.exception.member.DuplicateEmailException;
import com.potless.backend.global.exception.member.EmailNotFoundException;
import com.potless.backend.global.exception.member.InvalidLoginAttemptException;
import com.potless.backend.global.exception.member.PasswordMismatchException;
import com.potless.backend.global.jwt.TokenInfo;
import com.potless.backend.global.jwt.provider.TokenProvider;
import com.potless.backend.global.jwt.repository.RefreshTokenRepository;
import com.potless.backend.global.jwt.service.TokenService;
import com.potless.backend.global.util.CookieUtil;
import com.potless.backend.member.dto.*;
import com.potless.backend.member.entity.MemberEntity;
import com.potless.backend.member.repository.member.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Member;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final TokenService tokenService;
    private final CookieUtil cookieUtil;

    @Override
    @Transactional
    public Long signup(SignupRequestDto requestDto) {

        /* 비밀번호 불일치 */
        checkPasswordConfirmation(requestDto.getPassword(), requestDto.getPasswordConfirm());

        /* 이메일 중복 검증 */
        memberRepository.searchByEmail(requestDto.getEmail())
                .ifPresent(memberEntity -> {
                            throw new DuplicateEmailException();
                        }
                );

        MemberEntity newMember = MemberEntity.of(requestDto,
                                                passwordEncoder.encode(requestDto.getPassword()));
        memberRepository.save(newMember);

        return newMember.getId();
    }

    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto, HttpServletResponse response, int identify) {
        log.info("event=LoginAttempt, email={}", requestDto.getEmail());

        MemberEntity member = memberRepository.searchByEmail(requestDto.getEmail())
                                              .orElseThrow(EmailNotFoundException::new);
        //앱 로그인 경우에서 작업자 이외의 로그인 시도 또는 웹 로그인 경우에서 관리자 이외의 로그인 시도시 에러
        if((identify == 1 && member.getRole() != 1) || (identify == 0 && member.getRole() != 0)){
            throw new InvalidLoginAuthException();
        }

        isPasswordMatchingWithEncoded(requestDto.getPassword(), member.getPassword());
        removeOldRefreshToken(requestDto.getEmail(), member);

        // 웹/앱 요청 구별, refresh token web : 24시간 / app : 일주일
        TokenInfo tokenInfo = tokenProvider.generateTokenInfo(member.getEmail(), identify);
        int refreshTokenTime = (identify == 0) ? tokenProvider.getREFRESH_TOKEN_TIME_WEB() : tokenProvider.getREFRESH_TOKEN_TIME_APP();

        tokenService.saveToken(tokenInfo);
        if (identify == 0)
            cookieUtil.addCookie("RefreshToken", tokenInfo.getRefreshToken(), refreshTokenTime, response);

        return LoginResponseDto.builder()
                .token(tokenInfo.getAccessToken())
                .memberInfo(MemberInfo.builder()
                                      .Id(member.getId())
                                      .memberName(member.getMemberName())
                                      .role(member.getRole())
                                      .email(member.getEmail())
                                      .phone(member.getPhone())
                                      .region(member.getRegion())
                                      .build())
                .build();
    }

    @Override
    @Transactional
    public String logout(String email, HttpServletResponse servletResponse, int identify) {
        if (identify == 0) cookieUtil.removeCookie("RefreshToken", servletResponse);
        refreshTokenRepository.findById(email)
                              .ifPresent(refreshTokenRepository::delete);
        return email;
    }

    @Override
    @Transactional
    public String extendAppLogin(Authentication authentication, HttpServletResponse httpServletResponse, int identify) {
        MemberEntity member = findMember(authentication.getName());

        removeOldRefreshToken(member.getEmail(), member);
        TokenInfo tokenInfo = tokenProvider.generateTokenInfo(member.getEmail(), identify);
        tokenService.saveToken(tokenInfo);

        return tokenInfo.getAccessToken();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MemberEntity findMember(String email) {
        return memberRepository.searchByEmail(email).orElseThrow(MemberNotFoundException::new);
    }

    private void checkPasswordConfirmation(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new PasswordMismatchException();
        }
    }

    private void isPasswordMatchingWithEncoded(String input, String encoded) {
        if (!passwordEncoder.matches(input, encoded)) {
            throw new InvalidLoginAttemptException();
        }
    }

    private void removeOldRefreshToken(String email, MemberEntity member) {
        refreshTokenRepository.findById(email)
                .ifPresent(refreshTokenRepository::delete);
        log.info("event=DeleteExistingRefreshToken, email={}", email);
    }
}