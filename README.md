## SPRING-BOOT-REST-API

### 2020.06.15
💡 Build tool은 Gradle을 사용해보기로 했다!
 * 좀 더 간결하고 확장성 있게 프로젝트를 구성할 수 있다.
 * [Maven을 넘어 Gradle로 가자.](http://egloos.zum.com/kwon37xi/v/4747016) 를 참고.
   
💡 spring boot 2.3.1.RELEASE  

💡 JDK 1.8  

💡 서버를 undertow로 변경했다.   

 * 상용이 아닌 was는 tomcat밖에 선택권이 없는 줄 알았는데, undertow의 존재를 알게 됐다.
 * Netty라는 뛰어난 Java Network Library를 사용해서 Servlet Container를 구성했기 때문에, Tomcat에 비해 성능이 뛰어나다.
 * Spring Boot에서 기본 옵션으로 제공하여 코드 몇 줄만 수정하면 적용할 수 있다.
 * [Spring Boot 공식 지원 내장 WAS인 undertow를 씁시다.](https://zepinos.tistory.com/35).  
 
💡 h2 database를 사용했다.
 * 데이터베이스는 oracle밖에 안 써봤기 때문에 완전 초면이다.
 * h2는 in-memory 데이터베이스다. 프로그램을 종료하면 데이터가 손실된다. 하지만 매우 가볍다. 테스트하기에 좋을 것 같다.
 * spring-boot-starter-jdbc 에서 기본적으로 Spring JDBC와 Hikari Connection Pool을 제공한다.  
 
💡 scheme와 data를 추가했다.
 * 간단한 소셜 기능을 구현할 예정이기 때문에 USERS, POSTS, COMMENTS, CONNECTIONS, LIKES 로 구성했다.  
 
![간단한 구조](https://drive.google.com/uc?export=download&id=1ld9hJtknulHbcDL8--fh2STrs452U2fs)
 
 
### 2020.06.16

💡 logback.xml 작성했다.
  * 로깅 패턴과 레벨을 설정했다.
  * 콘솔에 로그를 남기도록 했고 레벨은 info 로 했다.(debug로 바뀔 것 같다), undertow 로깅은 off했다.
  
💡 User 엔티티를 작성했다.
  * 빌더 패턴을 사용했다. 
    * email, name, passwd 필드가 모두 String이라 뒤바뀌어도 구별이 안 갈 것 같아서다.
  * toString()을 재정의했다.
    * commons.lang3 라이브러리의 toStringStyle을 이용했다.
  * hashCode(), equals()를 재정의했다.
  

### 2020.06.17
  
💡 사용자 조회 기능을 만들었다.  

  * ✔️ user repository 구성  
    * UserRepository 인터페이스를 만들고, 이를 구현한 JdbcUserRepository를 작성했다.
    * JdbcTemplate을 사용해서 쿼리를 수행했다.
    * result를 받을 때 RowMapper를 User 엔티티로 선언해서 재사용할 수 있게 했다.
    * timestamp -> localDateTime, 혹은 그 반대로 변환하는 작업을 제공하는 DateTimeUtils 클래스를 만들었다.
  * ✔️ user service 구성  
  * ✔️ user controller 구성  
  * ✔️ user controller Test 구성  

### 2020.06.18

💡 API 공통 응답 클래스인 ApiResult를 만들었다.  

💡 API 에러를 반환하는 ApiError 클래스를 만들었다.
  * ApiError는 ApiResult에 담아 응답한다.
  * 메세지와 HTTP STATUS를 알려 준다.
  
💡 사용자 가입 기능을 만들었다.
  * Controller에서는 JoinRequest DTO로 필요한 가입정보를 받았다.
  * keyHolder를 사용해 새 사용자 정보를 반환했다.

💡 TEST DB 설정 완료.  

💡 user service Test를 작성했다.
  * @TestInstance 를 사용해 서비스별로 독립적으로 테스트할 수 있도록 했다. 

💡 logJdbc 의존성을 추가했다.  
  
### 2020.06.19
 
💡 Service Exception class와 Message 설정을 추가했다.
  * ✔️ Exception 구성  
    * ServiceRuntimeException 클래스를 만들고, 이를 상속받아 사용한다.
    * 먼저 NotFoundException을 만들었다.
        * 클래스명이나 따로 넘어온 타겟명, 예외 발생한 쿼리 조회값를 알려 준다.
  * ✔️ message 설정
    * application.properties에 설정 추가
    * MessageSourceAccessor를 생성, 반환하는 MessageUtils 클래스를 만들었다.
    * ServiceConfigure에 MessageSourceAccessor 빈을 추가했다.
    * 🔥 싱글톤 패턴을 적용해보는 건 어떨까 ?
    
💡 Id 클래스를 생성했다.
  * 플젝트 구조 상 seq 가 많아 공통으로 표현할 수 있는 Id 클래스를 만들었다.
  * Reference(Class)와 Id 를 담는다.
  
  
💡 가입 시 email 중복 체크 기능을 만들었다.  

💡 @ControllerAdvice 를 사용하여 GeneralExceptionHandler 기능을 만들었다.
  * 이제 컨트롤러에서 예외 발생 시 여기로 넘어오기 때문에 공통으로 관리할 수 있다.

### 2020.06.20

💡 Spring Security를 적용해보고 있다.
  * 최대 난관이다... 인증 과정 이해가 안 된다
  * 당장 JWT를 적용해보려는 욕심을 버리고 기본 설정 과정을 살펴보기로 했다. 

### 2020.06.21

💡 Role Enum을 만들었다.
  * 일반 사용자와 어드민을 구분하는 역할이다.
  * DB, User 엔티티에 Role을 추가했다.
  * Security 설정에서  uri 접근 권한을 따로 두었다.

### 2020.06.22

💡 Authentication Controller를 만들었다.  
  * AuthenticationRequest에서 사용자 정보를 가져와 UsernamePasswordAuthenticationToken을 리턴한다.
💡 UserNamePasswordAuthenticationFilter를 상속한 JwtAuthenticationFilter를 만들었다. 
  * attemptAuthentication()을 구현했다.
  * AuthenticationRequest에서 사용자 정보를 추출하고, UsernamePasswordAuthenticationToken에 담아 setDetails를 호출했다.
  * 그리고 authenticationManager.authenticate()를 리턴했는데...
  * **호출이 안 된다...** 
  * 근데 내가 구현한 건 인증 시도고.. 이미 set한 인증정보는 어디서 검증하는 거지 ?
  * 멘붕이다...
💡 DaoAuthenticationProvider 빈, AuthenticationManager 빈, JwtAuthenticationFilter 빈을 추가했다.  

💡 UserDetails을 상속한 UserPrincipal 클래스를 만들었다.
  * Authentication에서 principal을 받아올 때 사용한다.  
  * authority를 추가한다.
  
  
💡 Jwt 관리 클래스를 만들었다.
  * 새로운 토큰을 발급한다.
    * Claims 로 들어온 사용자 정보를 적절하게 조합하여 새로운 토큰을 만든다.
  * 토큰의 유효성을 검증한다.
    * 토큰의 유효성을 검증하고, 해석하여 Claims를 반환한다.

💡 Jwt 구성 클래스를 빈으로 등록했다.
### 2020.06.24
💡 /auth(인증), /user/me(인증 후 내 정보) 까지
  * UserNamePasswordAuthenticationFilter를 상속하지 않고 상위 클래스인 GenericFilterBean을 상속해서 AddFilterBefore 했다. 
    * 여기서 들어온 사용자 인증정보를 검증한다.
    * 새로운 인증 시도는 Provider에서 한다.
  * (이게 보편적인 방법인 것 같다.)
  * /auth 에서 리턴값을 못 받아 온다..  

💡 로그 레벨 DEBUG로 변경  

💡 JWT issuer social-server 로 변경
  * 임의설정 X, 따로 발급받아야 하나보다..

💡 /auth 리턴값 못 받아오는 문제 수정
 * 로그인 비즈니스 로직, AuthenticationResult 셋팅이 빠져 있었다.  
 
💡 UserDetailsService를 구현한한 UserPrincipalDetailsService 클래스를 만들었다.
  * 요청이 들어온 사용자 정보가 DB와 매치되는지 확인하는 역할이다.

💡 AuthenticationProvider를 새로 만들었다.
 * DaoAuthenticationProvider를 사용하게 되면 무조건 UserDetail과 UserDetailService를 만들어야 하기 때문에.. 이를 상속받지 않고 AuthenticationProvider를 상속받는 편이 나을 것 같다.
 
 
💡 Authentication Exception Handler를 추가했다.
  * AuthenticationEntryPoint를 구현했다.

### 2020.06.25
💡 로그인 로직을 추가했다.
  * /api/users 는 전체 회원 조회로 ADMIN 만 접근 가능하다.
  * /api/** 는 인증한 사용자만 접근 가능하다.
  * UserPrincipalDetailsService, UserService 의 기능이 겹치는 것 같다... 
    * 하나는 인증단계, 다른 하나는 비즈니스 로직 단계로 시점이 다르긴 하다.
  * 인증 후에 passwd는 왜 따로 확인할까
  

💡 Authorization 을 수행하기 위한 ConnectionBasedVoter를 추가했다.
  * AccessDecisionVoter를 구현했다.
  * 요청 사용자가 접근 uri의 사용자와 어떤 관계인지 판별하여 인가를 진행한다.
  
💡 recursive dependency injection이 발생했다.
  * SecurityConfigure에서 불필요하게 UserDetailService를 주입받고 있었다.  
  
💡 UserPrincipal에서 정보를 따로 받아서 나중에 User로 합치도록 리팩토링했다. 
  * 기존에는 UserPrincipal이 User를 담고 있어서 이름만 다른 클래스이지 user를 계속 호출하고 있었다.
  * 그럴 바엔 아예 User로 합쳐서 빼버리자는 생각으로 toUser()를 만들었다.
  
  
### 2020.06.26 - 28
💡 Kakao oauth2 로그인 처리를 해 보았다.
  * 의존성을 추가하고, OAuth2AuthorizedClientService와 AuthenticationSuccessHandler를 구현했다.
  * 🔥 프론트 구현이 미흡한 것이 이 프로젝트의 한계다...
    * 시간 날 때 React를 배워야겠다.
    * 일단 지금은 화면은 두고 Postman으로 기능 확인만 하고 있다.

### 2020.06.29
 💡 포스트 기능을 추가했다.
   * 작성, 조회, 좋아요, 코멘트 기능을 한 번에 ...!
   * Pagable과 그에따른 HandlerMethodArgumentResolver 클래스를 추가했다.
 
### 2020.06.30
 💡 PageableHandlerArgumentResolver Bean 생성 누락으로 수정
   * 역시 한꺼번에 하면 꼭 하나씩 빼먹는다.  
   
 💡 Jwt secret을 변경했다.
 
 
### 2020.07.01
 💡 AWS s3 설정을 추가했다.
   * 프로필 이미지와 포스트 첨부파일을 넣을 예정이다.
   * 이제 별로 고민 없었던 과정(의존성 추가, 빈 등록, accessKey 등을 담은 구성 클래스, 테스트 클래스 생성 같은...) 은 기록을 안 하게 된다...
     * 나태해진 걸까.. 
   * AmazonS3 라이브러리를 사용해서 S3에 이미지를 업로드, 삭제하는 S3Client 클래스를 만들었다.
 
 💡 가입 시 프로필 이미지 업로드.
   * MultiPartFile을 받아 검증하고, 파일명과 내용, 타입 등을 반환하는 AttachedFile 클래스를 만들었다.
     * s3client의 upload를 호출할 때 편리하도록
   * User DB와 엔티티 수정.
   * UserService에 프로필 이미지 가져오는 로직 추가
     * 이미지가 없어도 가입 가능하다.

### 2020.07.03
  💡 사용자 이름과 프로필 이미지를 수정하는 기능을 만들었다.  
  
  💡 사용자 비밀번호를 수정하는 기능을 만들었다.
    * 패스워드를 한 번 더 입력해서 본인 확인하는 과정이 있기 때문에 메소드를 분리했다.

### 2020.07.06
  💡 event와 push 설정을 했다.
    * EventConfigure
    * Event / EventListener
    * Exception Handler
    * PushConfigure
  💡 SubScribeController를 만들었다.
    * NotificationService
    * PushMessage
    * Subscription
    
### 2020.07.07-12
  * 프론트를 내가 직접 구현하고자 했으나... 계획한 만큼 실력이 따라주지 않고 프로젝트 목적이 주객전도가 될 것 같아서 이미 구현된 프론트를 사용하려고 한다...
  * 꼭 수정할 거다. 프론트를 이렇게 놔 두면 의미가 없어.. 
### 2020.07.13
  
💡 Comment 등록 시 Post 작성자에게 push가 되도록 했다.
 
### 2020.07.14
💡 Post, Comment 수정 기능을 만들었다.


### 2020.07.17
💡 친구 요청 기능을 만들었다.

### 2020.07.18
💡 친구 승인 기능을 만드는 중이다.
  * 내게 요청했으나 내가 승인하지 않은 친구 목록을 만들었다.
  * Connection Service를 만들었다. Controller는 User를 그대로 쓴다.

💡 친구 요청 시 타겟에게 push 알림하는 기능을 만들었다.


### 2020.07.24
💡 친구 요청 승인 기능을 만들었다.

### 2020.08.05
💡 친구 구독 취소 기능을 만들었다.

### 2020.08.06
💡 포스트, 코멘트 삭제 기능을 만들었다.
### 2020.08.07-08
💡 포스트에 이미지를 추가하는 기능을 만들었다.
  * 컨트롤러에서 본문과 파일을 각가 다른 변수로 받아오고 있는데 DTO 같은 걸로 안 합쳐도 될까..

### 2020.08.10
💡 내가 구독한 모든 사용자의 글을 한꺼번에 조회하는 기능을 만들었다.  


### 2020.08.13
💡 진행중인 친구요청 리스트, 나를 구독한 친구 목록, 내가 승인하지 않은 친구요청 리스트
  * 세 메소드를 구분
  * 비슷한 기능인데, 통합할 수는 없을까
  

### 2020.08.16
💡 포스트 내용을 조회하는 기능을 만들었다.
  * 내 포스트 + 내가 구독한 친구의 포스트만 검색된다.
  

### 2020.08.21
💡 push와 별개로 알림 목록을 만들고 있다.
  * push를 보낼 때 Notification DB에 저장한다. 
  * NotiController 를 새로 만들었다. (NotificationService는 그대로 쓴다.)
    * 약간 개념이 헷갈린다.. push(Notification)와 Noti service를 구분해야 하나? 근데 쌓는 시점은 같단 말이지..

### 2020.08.23
💡 승인하지 않은 친구 목록 버그 수정
  * 요청 사용자를 타겟 사용자로 받아오고 있었다..  
  
💡 Noti 수정
  * PushMessage의 title을 noti message로 받아오게 했다.
  
💡 Noti 삭제 기능을 구현했다.
  
### 2020.08.24
💡 comment 알림에서 새로 달린 댓글 갯수까지 보이도록 수정했다.
  * XX글에 댓글이 달렸습니다! -> XX글에 X개의 댓글이 달렸습니다!  
  
💡 Noti 목록을 최근 순으로 수정했다.
