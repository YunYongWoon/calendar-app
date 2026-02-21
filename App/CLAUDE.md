# App CLAUDE.md

Flutter 네이티브 앱. Frontend 웹앱을 WebView로 렌더링하는 구조.

## 기술 스택

| 분류 | 기술 |
|------|------|
| Framework | Flutter |
| Language | Dart |
| Platform | Android, iOS |
| WebView | flutter_inappwebview 또는 webview_flutter (미확정) |

## 디렉토리 구조 (예정)

```
lib/
├── main.dart
├── app/          # 앱 진입점, 라우팅
├── webview/      # WebView 위젯, 브릿지 서비스
└── shared/       # 공통 위젯, 유틸리티
```

## 코딩 컨벤션

- 클래스 · 타입: `PascalCase`
- 변수 · 함수: `camelCase`
- 상수: `lowerCamelCase` (Dart 관례)
- WebView 관련 로직은 **별도 위젯/서비스로 분리** (lib/webview/ 하위)
- JavaScript Bridge 통신은 추상화 클래스로 감싸서 사용

## 현재 상태

- [ ] 프로젝트 초기 설정 예정 (Flutter 프로젝트 생성)
