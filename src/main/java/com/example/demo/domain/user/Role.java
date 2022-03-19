package com.example.demo.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {//여기를 좀 수정해서 역할들 나눠야 될 것 같은데

    GUEST("ROLE_GUEST", "손님"),//로그인 전
    USER("ROLE_USER", "일반 사용자"),//로그인 후
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;
}
