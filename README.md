# Responsive-Web
* 대학교 족보 공유 반응형 웹

![image](https://user-images.githubusercontent.com/59761622/131461675-da03751e-6d26-4951-8ebe-51f058ebd0c9.png)


* 구현 기능
= MVC Pattern, 로그인, 회원가입, 아이디/닉네임 중복체크, 이메일 인증 및 신고/문의사항(SMTP), 자동 로그인 및 조회수 중복처리(Cookie), 게시글 작성/수정/삭제, 댓글 및 대댓글 작성/삭제, 게시글 및 댓글 추천(IP), 인기글, 파일 업로드(게시글 첨부파일, 프로필 이미지) 및 다운로드, 채팅과 메시지함, 안읽은 메시지 알림, 학우 찾기, 페이지네이션, 검색, 모달

* 보안
= 스크립트 특수문자 치환으로 XSS 공격 방지, Session 검증으로 URL 비정상 접근 방지, 비밀번호/인증코드 해시화(SHA-256), 비밀번호 정규식(JavaScript), SQL 인젝션 방지(prepareStatement()), 경로 순회 공격 방지, 웹셸(Web Shell) 방지, 파일 이름 중복 보안(DefaultFileRenamePolicy()), Null Pointer 역참조 방지, URL Encoding/Decoding


1. 개발 환경 및 사용 도구와 패턴
- IDE: Eclipse Java EE
- 개발 언어: JSP & Servlet, HTML, CSS, JavaScript, SQL
- Middleware: Apache Tomcat 8.5.46
- DBMS: MySQL 5.7.26
- 디자인 패턴: MVC Pattern
- 통신 방식과 활용 프로토콜: JSON-Ajax, HTTP GET/POST, SMTP, IP

2. 개발 Kit 및 Plug-In(외부 라이브러리) 
- JDK 1.8.0_211
- JDBC 5.1.48 (mysql-connector-java-5.1.48-bin.jar)
- jQuery 3.3.1 (https://code.jquery.com/jquery-3.3.1.min.js)
- Bootstrap 3.3.7 (bootstrap.css)
 -Bootbox 4.4.0 (https://cdnjs.cloudflare.com/ajax/libs/bootbox.js/4.4.0/bootbox.min.js)
- javamail 1.4.7 (mail.jar)
- activation 1.1.1 (activation.jar)
- cos 20.08 (cos.jar)
- Google Font(jejugothic.css, nanumgothic.css)

![image](https://user-images.githubusercontent.com/59761622/130317071-5803491a-1364-49bd-af88-53bb451b8d6b.png)


![image](https://user-images.githubusercontent.com/59761622/130317062-122162be-9664-4de4-8a02-54f748134ef1.png)


![image](https://user-images.githubusercontent.com/59761622/130317089-532f0aa2-bcf7-4cf1-8376-1a59760bc6e0.png)


![image](https://user-images.githubusercontent.com/59761622/130317142-73d96f67-02ff-4e88-a384-98b1300fd3c3.png)


![image](https://user-images.githubusercontent.com/59761622/130317152-d1238944-1fe5-49ad-903c-7a256a1476d6.png)


![image](https://user-images.githubusercontent.com/59761622/130317161-8f1f6ce0-a867-4c1a-8569-7e668f6a46d4.png)
