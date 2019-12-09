DelFood
=============
전국에 있는 배달음식점과 소비자를 연결하는 중개 플랫폼 서버 입니다.<br>
Rest API형 서버로써 클라이언트는 프로토타입으로 제작하여 서버 공부에 좀 더 집중할 수 있도록 하고 있습니다.<br>
고객 - 매장 - 라이더를 중개하는 기능을 구현할 예정입니다.<br><br>

### 프로젝트의 주요 관심사
**공통사항**<br>
- 지속적인 성능 개선
- 나쁜 냄새가 나는 코드에 대한 리팩토링
<br><br>
<b>코드 컨벤션</b><br>
- Google code Style을 준수
- STS Check Style 플러그인을 적용하여 코드 컨벤션을 유지 
https://google.github.io/styleguide/javaguide.html
<br><br>
<b>장애 방지</b><br>
- 서버 부하를 줄이기 위해 캐싱 서버 적극 활용
- DB서버와의 통신을 최소화(당연한 이야기지만 N+1 쿼리를 지양)
- 클라이언트에서 주는 데이터를 한번 더 검사하여 데이터의 무결성을 보장
<br>
<b>그 외</b><br>
- Project Wiki를 참고해주세요!
<br><br>


## 사용 기술 및 환경
Spring boot, Maven, Mybatis, Redis, Docker, MariaDB, Jenkins, Java8, Naver Cloud Platform
<br>

## Wiki
Tistory - <https://github.com/f-lab-edu/food-delivery/wiki><br>
Wiki에 기술 이슈에 대한 고민과 해결 방법을 포스팅한 개인 블로그의 url이 포함되어있습니다.<br>
<br>
## CI
Jenkins : <http://106.10.51.119:8080/job/Delfood/><br>
PR시마다 자동 Build 및 Test 적용<br>
비로그인 상태로도 확인이 가능합니다.<br>
<br>
## 화면 설계
kakao oven - <https://ovenapp.io/view/OI44WSGwSZRSMcyiveGcSWGvw38YKizI/bco8b\>   

### 고객 화면 프로토타입
![image](https://user-images.githubusercontent.com/46917538/66744438-6b694e00-eeb7-11e9-82b7-246f569a74a6.png)

### 사장님 화면 프로토타입
![화면정의서_사장님](https://user-images.githubusercontent.com/46917538/68458111-4f9e6100-0245-11ea-9118-0ca891eab044.png)


## 프로젝트 DB ERD
2019-11-08 수정 (쿠폰 스키마 추가)
![배달의민족_20191108_33_53](https://user-images.githubusercontent.com/46917538/68458221-9429fc80-0245-11ea-9cc3-92f7a35fd534.png)

### 이전 DB ERD
2019-10-27 수정(건물정보 TB를 추가하여 주소를 외래키로 관리)
![배달의민족_20191027_28_25](https://user-images.githubusercontent.com/46917538/67629389-4235be00-f8b8-11e9-9ba5-abfec4c9d7b9.png)
