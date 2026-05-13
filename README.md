# StudyGroup

StudyGroup은 스터디 모집, 가입 신청, 멤버 운영, 일정/공지 관리를 하나의 흐름으로 연결한 스터디 운영 플랫폼입니다.  
백엔드는 Spring Boot 기반 REST API로 구현했고, 프론트엔드는 React와 RTK Query를 사용해 인증 상태와 서버 데이터를 관리합니다.

## 프로젝트 목표

- 스터디 생성부터 모집글 작성, 가입 신청, 승인/거절까지 이어지는 핵심 운영 플로우 구현
- OWNER, ADMIN, MEMBER 역할을 분리하고 관리자별 세부 권한을 적용
- JWT 기반 인증과 refresh token 재발급, 로그아웃 시 access token blacklist 처리
- 서비스 계층 통합 테스트로 주요 비즈니스 규칙 검증

## 기술 스택

### Backend

- Java 21
- Spring Boot 3.5.11
- Spring Web, Spring Security, Spring Data JPA, Validation
- JWT: `io.jsonwebtoken:jjwt`
- PostgreSQL
- H2 Database for test
- JUnit 5, AssertJ

### Frontend

- React 18
- TypeScript
- Vite
- Redux Toolkit, RTK Query
- Axios
- React Hook Form, Zod
- Tailwind CSS

## 주요 기능

### 인증

- 로컬 회원가입/로그인
- Google ID Token 로그인
- JWT access token / refresh token 발급
- refresh token 기반 access token 재발급
- 로그아웃 시 access token blacklist 저장
- `/api/auth/me` 사용자 정보 조회

### 스터디

- 스터디 생성, 목록 조회, 상세 조회, 수정, 삭제
- 모집 상태 검색
- 스터디 생성자를 OWNER 멤버로 자동 등록
- 내 스터디 역할 조회

### 모집/가입

- 모집글 생성, 조회, 수정, 삭제
- 가입 신청 생성
- 중복 신청 방지
- 거절된 신청 재신청
- 가입 승인/거절
- 정원 초과 방지

### 운영 관리

- OWNER가 ADMIN 지정 및 세부 권한 설정
- ADMIN 권한: 모집글 관리, 가입 승인, 멤버 관리, 일정 관리, 공지 관리
- 멤버 강퇴
- 블랙리스트 등록/해제
- 일정 CRUD
- 공지 CRUD
- 신고 접수 및 처리

## 비즈니스 규칙

- 스터디 생성자는 자동으로 OWNER가 됩니다.
- OWNER는 모든 운영 권한을 가집니다.
- ADMIN은 부여받은 세부 권한에 따라 일부 기능만 사용할 수 있습니다.
- 이미 가입한 사용자는 같은 스터디에 다시 신청할 수 없습니다.
- `PENDING` 또는 `APPROVED` 신청이 있으면 중복 신청을 막습니다.
- `REJECTED` 신청은 같은 신청 ID를 재사용해 재신청할 수 있습니다.
- 정원이 가득 찬 스터디는 추가 승인할 수 없습니다.
- 블랙리스트 사용자는 해당 스터디에 가입 신청할 수 없습니다.

## 테스트

서비스 계층 통합 테스트로 핵심 플로우를 검증합니다.

현재 포함된 테스트:

- 중복 가입 신청 차단
- 관리자 가입 승인
- 정원 초과 승인 차단
- 거절된 가입 신청 재신청
- 블랙리스트 사용자 가입 신청 차단
- 스터디 일정 CRUD
- 스터디 검색 조건
- Spring context load

실행:

```bash
./gradlew test
```

Windows PowerShell:

```powershell
.\gradlew.bat test
```

## 실행 방법

### 1. Backend

필수 환경 변수:

```bash
DB_URL=jdbc:postgresql://localhost:5432/studygroup
DB_USERNAME=postgres
DB_PASSWORD=password
JWT_SECRET=studygroup-studygroup-studygroup-studygroup-secret-key
GOOGLE_CLIENT_ID=your-google-client-id
```

실행:

```bash
./gradlew bootRun
```

Windows PowerShell:

```powershell
.\gradlew.bat bootRun
```

### 2. Frontend

```bash
cd frontend
npm install
npm run dev
```

환경 변수 예시:

```bash
VITE_API_BASE_URL=http://localhost:8080
VITE_GOOGLE_CLIENT_ID=your-google-client-id
```

## API 응답 형식

성공:

```json
{
  "status": 200,
  "code": "SUCCESS",
  "message": "Login completed.",
  "data": {}
}
```

실패:

```json
{
  "status": 403,
  "code": "STUDY_ACCESS_DENIED",
  "message": "Access denied.",
  "data": null
}
```

## 주요 API

### Auth

| Method | URL | Description |
| --- | --- | --- |
| POST | `/api/auth/local/signup` | 로컬 회원가입 |
| POST | `/api/auth/local/login` | 로컬 로그인 |
| POST | `/api/auth/google/login` | Google ID Token 로그인 |
| POST | `/api/auth/local/reissue` | 토큰 재발급 |
| POST | `/api/auth/logout` | 로그아웃 |
| GET | `/api/auth/me` | 내 정보 조회 |

### Study

| Method | URL | Description |
| --- | --- | --- |
| POST | `/api/studies` | 스터디 생성 |
| GET | `/api/studies` | 스터디 목록 조회 |
| GET | `/api/studies/{studyGroupId}` | 스터디 상세 조회 |
| PATCH | `/api/studies/{studyGroupId}` | 스터디 수정 |
| DELETE | `/api/studies/{studyGroupId}` | 스터디 삭제 |
| GET | `/api/studies/{studyGroupId}/role` | 내 스터디 역할 조회 |
| GET | `/api/studies/{studyGroupId}/members` | 멤버 목록 조회 |

### Recruitment

| Method | URL | Description |
| --- | --- | --- |
| POST | `/api/studies/{studyGroupId}/recruitment-posts` | 모집글 생성 |
| GET | `/api/studies/{studyGroupId}/recruitment-posts` | 모집글 목록 조회 |
| PATCH | `/api/studies/{studyGroupId}/recruitment-posts/{recruitmentPostId}` | 모집글 수정 |
| DELETE | `/api/studies/{studyGroupId}/recruitment-posts/{recruitmentPostId}` | 모집글 삭제 |
| POST | `/api/studies/{studyGroupId}/recruitment-posts/{recruitmentPostId}/applications` | 가입 신청 |
| GET | `/api/studies/{studyGroupId}/recruitment-posts/{recruitmentPostId}/applications` | 가입 신청 목록 |
| PATCH | `/api/studies/{studyGroupId}/recruitment-posts/{recruitmentPostId}/applications/{applicationId}/approve` | 가입 승인 |
| PATCH | `/api/studies/{studyGroupId}/recruitment-posts/{recruitmentPostId}/applications/{applicationId}/reject` | 가입 거절 |

## 프로젝트 구조

```text
StudyGroup
├── src/main/java/StudyGroup/StudyGroup
│   ├── domain
│   │   ├── study
│   │   │   ├── controller
│   │   │   ├── dto
│   │   │   ├── entity
│   │   │   ├── exception
│   │   │   ├── repository
│   │   │   └── service
│   │   └── user
│   └── global
│       ├── auth
│       ├── config
│       ├── exception
│       └── response
├── src/test/java/StudyGroup/StudyGroup
│   └── domain/study/service
└── frontend
    └── src
        ├── api
        ├── app
        ├── components
        ├── features
        ├── pages
        └── routes
```

## 포트폴리오 포인트

- 단순 CRUD를 넘어 스터디 운영 권한, 가입 승인, 정원 제한, 블랙리스트 같은 도메인 규칙을 구현했습니다.
- 정원 초과 승인 문제를 줄이기 위해 가입 승인 시 스터디 row에 pessimistic lock을 적용했습니다.
- API 응답 형식과 비즈니스 예외를 공통화해 프론트엔드에서 일관되게 처리할 수 있도록 구성했습니다.
- React에서는 RTK Query를 사용해 서버 상태 캐싱, mutation 후 invalidation, 인증 토큰 자동 첨부를 처리했습니다.
- 주요 서비스 플로우는 H2 기반 통합 테스트로 검증합니다.

## 개선 예정

- 테스트 범위 확대: 인증 컨트롤러, 권한 실패 케이스, 공지/신고 API 테스트
- 운영 환경 분리: profile 기반 설정, CORS origin 환경 변수화
- refresh token 저장 전략 개선 및 토큰 타입 검증 강화
- 프론트엔드 E2E 테스트 추가
- 배포 환경 구성
