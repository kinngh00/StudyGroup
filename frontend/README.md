# StudyGroup Frontend

StudyGroup 프론트엔드 애플리케이션입니다.

## 실행
```bash
npm install
npm run dev
```

## 빌드
```bash
npm run build
npm run preview
```

## 환경 변수
프로젝트 루트의 `.env` 또는 `frontend/.env`에 설정합니다.

```bash
VITE_API_BASE_URL=http://localhost:8080
```

## 구조
- `src/app`: 스토어, 훅, 라우터
- `src/api`: Axios/RTK Query API 레이어
- `src/features`: Redux Slice
- `src/components`: UI 컴포넌트(원자/분자/조직)
- `src/pages`: 페이지 단위 화면
- `src/routes`: 권한 가드

## 구현 범위
- 로그인/회원가입/구글 콜백
- 스터디 목록/상세/생성
- 스터디 내부 탭(대시보드/일정/회원/모집글/공지)
- 가입 신청 승인/거절
- 회원 강퇴/관리자 권한/블랙리스트 관리

## 주의
프론트 API 경로는 백엔드 명세(`docs/backend`)와 동일하게 유지해야 합니다.
