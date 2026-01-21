# Spicy

<img width="1280" height="800" alt="Image" src="https://github.com/user-attachments/assets/bb1d168f-23c7-4149-b669-4cf1db02bad2" />

---


## 📑 목차
1. [팀소개](#1-팀소개-)

2. [프로젝트 개요](#2-프로젝트-개요-)

3. [Code Convention](#3-code-convention-)

4. [협업도구](#4-협업도구-)

5. [요구사항 정의서](#5-요구사항-정의서)

6. [테스트 케이스](#6-테스트-케이스)

7. [화면 흐름도](#7-화면-흐름도-)

8. [메뉴 구성도](#8-메뉴-구성도-)

9. [시스템 아키텍처](#9-시스템-아키텍처)

10. [ERD](#10-erd)

11. [젠킨스 스크립트](#11-젠킨스-스크립트-)

12. [CI/CD 테스트](#12-cicd-테스트-)

13. [회고](#13-회고)



---

## 1. 팀소개 👥

<table align="center">
  <tr>
    <td align="center" width="180">
      <img src="https://github.com/user-attachments/assets/3c83a708-a9ca-4e9d-b4a3-f4f7ef795d60"
           style="width:160px; height:160px; object-fit:cover;" /><br>
      <strong>김채우</strong><br>
      chaewookim
    </td>
    <td align="center" width="180">
      <img src="https://github.com/user-attachments/assets/7f35eca8-50ec-4fdb-b91f-f87ebccc03a2"
           style="width:160px; height:160px; object-fit:cover;" /><br>
      <strong>김윤경</strong><br>
      yk5096
    </td>
    <td align="center" width="180">
      <img src="https://github.com/user-attachments/assets/97654fad-65b1-4617-8891-89bd01215ad0"
           style="width:160px; height:160px; object-fit:cover;" /><br>
      <strong>김성은</strong><br>
      rlatjddms
    </td>
    <td align="center" width="180">
      <img src="https://github.com/user-attachments/assets/d548941f-bd2d-4a28-9e76-417e49cef87b"
           style="width:160px; height:160px; object-fit:cover;" /><br>
      <strong>유찬연</strong><br>
      Yoocy0
    </td>
    <td align="center" width="180">
      <img src="https://github.com/user-attachments/assets/2cb1e4bf-c136-404e-a8b7-237f4cfb07ec"
           style="width:160px; height:160px; object-fit:cover;" /><br>
      <strong>조윤호</strong><br>
      cho-yunho01
    </td>
  </tr>
</table>




---

## 2. 프로젝트 개요 📢 
SPICY는 프리미엄 떡볶이 가맹점을 위한 가맹점 관리 시스템으로, 가맹점이 매장 내 재고를 실시간으로 관리하고 부족한 식재료를 본사에 직접 주문할 수 있도록 설계된 서비스입니다. 본사는 각 가맹점의 재고 및 주문 현황을 통합적으로 관리함으로써 물류 운영 효율을 높이는 것을 목표로 합니다.

가맹점주(Store)는 매장별 재고 현황을 실시간으로 확인하고, 필요한 식자재를 간편하게 발주하며, 주문 및 배송 상태를 조회할 수 있습니다. 본사 관리자(HQ)는 전체 가맹점의 운영 현황을 모니터링하고 회원을 관리하며, 전반적인 물류 흐름을 제어하는 역할을 담당합니다.

SPICY의 핵심 기능으로는 품목별 재고 수량과 입·출고 내역을 관리하는 재고 관리 기능이 있으며, 이를 통해 적정 재고 대비 부족한 품목을 쉽게 파악할 수 있습니다. 또한 이미지 중심의 프리미엄 상품 리스트와 장바구니 기반 주문 프로세스를 제공하는 스마트 발주 시스템을 통해 가맹점주의 주문 편의성을 높였습니다. 주문 이후에는 가맹점별로 접수, 배송 중, 완료 단계의 주문 상태를 실시간으로 추적할 수 있습니다.

사용자 관리는 JWT 기반 인증 방식을 적용하여 Access Token과 Refresh Token을 활용한 보안 구조로 구현되었으며, 가맹점주 프로필 관리 기능과 본사 전용 회원 검색 기능을 통해 체계적인 계정 관리를 지원합니다.

---

## 3. Code Convention 📌

### 공통 사항
- 단위 테스트 작성(service 메소드 별로) : Junit 사용
- 다른 사람이 알아보기 쉽도록 주석처리해야 합니다.
    - javadoc 형식 https://jake-seo-dev.tistory.com/59
- 이슈 생성(지라 티켓 생성), 브랜치 생성 후 작업 시작합니다.
- 사용 내역 같은 로그 확인할 수 있도록 잘 남겨야 합니다.

<br>

### 개발규칙 

### ⭐ Code Convention

---

<details>
<summary style = " font-size:1.3em;">Naming</summary>
<div markdown="1">

- 패키지 : 언더스코어(`_`)나 대문자를 섞지 않고 소문자를 사용하여 작성합니다.
- 클래스 : 클래스 이름은 명사나 명사절로 지으며, 대문자 카멜표기법(Upper camel case)을 사용합니다.
- 메서드 : 메서드 이름은 동사/전치사로 시작하며, 소문자 카멜표기법(Lower camel case)를 사용합니다. 의도가 전달되도록 최대한 간결하게 표현합니다.
- 변수 : 소문자 카멜표기법(Lower camel case)를 사용합니다.
- ENUM, 상수 : 상태를 가지지 않는 자료형이면서 `static final`로 선언되어 있는 필드일 때를 상수로 간주하며, 대문자와 언더스코어(Upper_snake_case)로 구성합니다.
- DB 테이블: 소문자와 언더스코어로(lower_snake_case) 구성합니다.
- 컬렉션(Collection): **복수형**을 사용하거나 **컬렉션을 명시합니다**. (Ex. userList, users, userMap)
- LocalDateTime: 접미사에 **Date**를 붙입니다.


</div>
</details>
<details>
<summary style = " font-size:1.3em;">Comment</summary>
<div markdown="1">

### 1. 한줄 주석은 // 를 사용한다.

```java
// 하이~
```

### 2. Bracket 사용 시 내부에 주석을 작성한다.

```java
/*
   하이~!
*/
```

### 3. 주요 함수에 대한 주석
다른 사람들에게 공유될 중요한 비즈니스 로직을 구현한 메소드에는 반드시 추가
```java
/**
* 한 줄로 함수의 기능 설명
*
* @param methodParam1 메소드 파라미터 1
* @param methodParam2 메소드 파라미터 2
* @return returnValue 리턴 값
* @throws CustomException(ErrorCode.PRODUCT_NOT_FOUND) 발생 가능한 예외 종류 명시
 */
public User getUser(Long idx)
```

</div>
</details>
<details>
<summary style = " font-size:1.3em;">Import</summary>
<div markdown="1">

### 1. 소스파일당 1개의 탑레벨 클래스를 담기

> 탑레벨 클래스(Top level class)는 소스 파일에 1개만 존재해야 한다. ( 탑레벨 클래스 선언의 컴파일타임 에러 체크에 대해서는 [Java Language Specification 7.6](http://docs.oracle.com/javase/specs/jls/se7/html/jls-7.html#jls-7.6) 참조 )

### 2. static import에만 와일드 카드 허용

> 클래스를 import할때는 와일드카드(`*`) 없이 모든 클래스명을 다 쓴다. static import에서는 와일드카드를 허용한다.

### 3. 애너테이션 선언 후 새줄 사용

> 클래스, 인터페이스, 메서드, 생성자에 붙는 애너테이션은 선언 후 새줄을 사용한다. 이 위치에서도 파라미터가 없는 애너테이션 1개는 같은 줄에 선언할 수 있다.


### 4. 배열에서 대괄호는 타입 뒤에 선언

> 배열 선언에 오는 대괄호(`[]`)는 타입의 바로 뒤에 붙인다. 변수명 뒤에 붙이지 않는다.

### 5. `long`형 값의 마지막에 `L`붙이기

> long형의 숫자에는 마지막에 대문자 'L’을 붙인다. 소문자 'l’보다 숫자 '1’과의 차이가 커서 가독성이 높아진다.

</div>
</details>
<details>
<summary style = " font-size:1.3em;">URL</summary>
<div markdown="1">

### URL

URL은 RESTful API 설계 가이드에 따라 작성합니다.

- HTTP Method로 구분할 수 있는 get, put 등의 행위는 url에 표현하지 않습니다.
- 마지막에 `/` 를 포함하지 않습니다.
- `_` 대신 `-`를 사용합니다.
- 소문자를 사용합니다.
- 확장자는 포함하지 않습니다.


</div>
</details>

<br>

### ☀️ Commit Convention

---

<details>
<summary style = " font-size:1.3em;">Rules</summary>
<div markdown="1">

### 1. Git Flow

작업 시작 시 선행되어야 할 작업은 다음과 같습니다.

> 1. issue를 생성합니다.
> 2. feature branch를 생성합니다.
> 3. add → commit → push → pull request 를 진행합니다.
> 4. pull request를 develop branch로 merge 합니다.
> 5. 이전에 merge된 작업이 있을 경우 다른 branch에서 진행하던 작업에 merge된 작업을 pull 받아옵니다.
> 6. 종료된 issue와 pull request의 label을 관리합니다.

- commit을 할 때는 `git add -p`를 사용해 관련 있는 작업들을 하나로 모아 커밋합니다.

### 2. Etc

준수해야 할 규칙은 다음과 같습니다.

> 1. develop branch에서의 작업은 원칙적으로 금지합니다. 단, README 작성은 develop branch에서 수행합니다.
> 2. commit, push, merge, pull request 등 모든 작업은 오류 없이 정상적으로 실행되는 지 확인 후 수행합니다.

</div>
</details>

<details>
<summary style = " font-size:1.3em;">Branch</summary>
<div markdown="1">

### 1. Branch

branch는 작업 단위 & 기능 단위로 생성하며 이는 issue를 기반으로 합니다.

### 2. Branch Naming Rule

branch를 생성하기 전 issue를 먼저 작성합니다. issue 작성 후 생성되는 번호와 domain 명을 조합하여 branch의 이름을 결정합니다. `<Prefix>/DEV-<TicketNumber>-<Domain>-<Description>` 의 양식을 준수합니다.

### 3. Prefix

- `main` : 개발이 완료된 산출물이 저장될 공간입니다.
- `dev`: feature branch에서 구현된 기능들이 merge될 default branch 입니다.
- `feat` : 기능을 개발하는 branch 입니다. 이슈 별 & 작업 별로 branch를 생성 후 기능을 개발하며 naming은 소문자를 사용합니다.

### 4. Domain

- `order`, `inventory`, `purchase`, `settlement`, `core` 


### 5. Etc

- `feat/DEV-7-order-fresh`, `feat/DEV-5-purchase-auto-logic`


</div>
</details>

<details>
<summary style = " font-size:1.3em;">Issue</summary>
<div markdown="1">

### 1. Issue

작업 시작 전 issue 생성이 선행되어야 합니다. issue 는 작업 단위 & 기능 단위로 생성하며 생성 후 표시되는 issue number 를 참조하여 branch 이름과 commit message를 작성합니다.

issue 제목에는 기능의 대표적인 설명을 적고 내용에는 세부적인 내용 및 작업 진행 상황을 작성합니다.

### 2. Issue Naming Rule

`[<Prefix>] <Description>` 의 양식을 준수하되, prefix는 commit message convention을 따릅니다.

### 3. Etc

<aside>
[feat] 약속 잡기 API 구현
<br/>[chore] spring data JPA 의존성 추가

</aside>

---

</div>
</details>

<details>
<summary style = " font-size:1.3em;">Commit</summary>
<div markdown="1">

### 1. Commit Message Convention

`[<Prefix>] #<Issue_Number> <Description>` 의 양식을 준수합니다.

- **feat** : 새로운 기능 구현 `[feat] #11 구글 로그인 API 기능 구현`
- **fix** : 코드 오류 수정 `[fix] #10 회원가입 비즈니스 로직 오류 수정`
- **del** : 쓸모없는 코드 삭제 `[del] #12 불필요한 import 제거`
- **docs** : README나 wiki 등의 문서 개정 `[docs] #14 리드미 수정`
- **refactor** : 내부 로직은 변경 하지 않고 기존의 코드를 개선하는 리팩터링 `[refactor] #15 코드 로직 개선`
- **chore** : 의존성 추가, yml 추가와 수정, 패키지 구조 변경, 파일 이동 `[chore] #21 yml 수정`, `[chore] #22 lombok 의존성 추가`
- **test**: 테스트 코드 작성, 수정 `[test] #20 로그인 API 테스트 코드 작성`
- **style** : 코드에 관련 없는 주석 달기, 줄바꿈

</div>
</details>

<details>
<summary style = " font-size:1.3em;">Pull Request</summary>
<div markdown="1">

### 1. Pull Request

develop & main branch로 merge할 때에는 pull request가 필요합니다. pull request의 내용에는 변경된 사항에 대한 설명을 명시합니다.

### 2. Pull Request Naming Rule

`[<Prefix>] #<IssueNumber> <Description>` 의 양식을 준수하되, prefix는 commit message convention을 따릅니다.

### 3. Etc

[feat] #3약속 잡기 API 구현
<br/>[chore] #5 spring data JPA 의존성 추가

</div>
</details>


---

## 4. 협업도구 🤝 

### 🛠 개발 환경 및 기술 스택

![Java](https://img.shields.io/badge/Java-007396?style=flat-square&logo=java&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=flat-square&logo=javascript&logoColor=black)
![Vue.js](https://img.shields.io/badge/Vue.js-4FC08D?style=flat-square&logo=vuedotjs&logoColor=white)
![Spring Boot](https://img.shields.io/badge/SpringBoot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/SpringSecurity-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=flat-square&logo=mariadb&logoColor=white)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=flat-square&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=flat-square&logo=css3&logoColor=white)

### 🚀 DevOps · CI/CD

![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-326CE5?style=flat-square&logo=kubernetes&logoColor=white)
![Jenkins](https://img.shields.io/badge/Jenkins-D24939?style=flat-square&logo=jenkins&logoColor=white)
![ArgoCD](https://img.shields.io/badge/ArgoCD-EF7B4D?style=flat-square&logo=argo&logoColor=white)

### 💻 IDE & Tools

![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000?style=flat-square&logo=intellijidea&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=flat-square&logo=git&logoColor=white)

### 🤝 협업 도구

![Discord](https://img.shields.io/badge/Discord-5865F2?style=flat-square&logo=discord&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-000000?style=flat-square&logo=notion&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white)
![Google Drive](https://img.shields.io/badge/GoogleDrive-4285F4?style=flat-square&logo=googledrive&logoColor=white)
![Figma](https://img.shields.io/badge/Figma-F24E1E?style=flat-square&logo=figma&logoColor=white)


---

## 5. 요구사항 정의서📋

<img width="1343" height="703" alt="Image" src="https://github.com/user-attachments/assets/bb30e293-1514-4807-91b7-37ce595e6ed9" />

---
## 6. 테스트 케이스
<img width="1669" height="579" alt="Image" src="https://github.com/user-attachments/assets/eccfec39-6782-411a-866a-9189df7f8942" />
<img width="1664" height="265" alt="Image" src="https://github.com/user-attachments/assets/4610c80c-a566-4438-ac32-bf160e449822" />


## 7. 화면 흐름도 🔀 

#### 계정
<img width="1039" height="688" alt="Image" src="https://github.com/user-attachments/assets/e0914e76-d69e-4002-b1e3-e4567cbee6da" />

#### 입고 출고
<img width="1039" height="688" alt="Image" src="https://github.com/user-attachments/assets/1949d2e1-b542-47fd-81e4-f624814850f5" />

#### 주문
<img width="1039" height="688" alt="Image" src="https://github.com/user-attachments/assets/9c2ef62d-3c1f-4a88-b7f6-3ff13c524db9" />

#### 정보 수정
<img width="1039" height="688" alt="Image" src="https://github.com/user-attachments/assets/493c432e-6c63-4cf8-b94a-4ec56a8d4544" />

#### 정산 관리
<img width="1039" height="688" alt="Image" src="https://github.com/user-attachments/assets/3a50ec4b-84fe-4aae-88b5-564024356ad9" />

---

## 8. 메뉴 구성도 🗂

<img width="1668" height="701" alt="Image" src="https://github.com/user-attachments/assets/3d5d280f-c06d-4208-9623-7c0b38d0e4f9" />

---

## 9. 시스템 아키텍처
<img width="981" height="677" alt="Image" src="https://github.com/user-attachments/assets/669d27a2-dfc9-4636-ba72-e6d81444f01f" />

---

## 10. ERD
<img width="2038" height="4280" alt="Image" src="https://github.com/user-attachments/assets/1a3b95ef-d037-457c-b7c3-9d9afdbeb59a" />

---

## 11. 젠킨스 스크립트 🧪

<details>
  <summary>코드</summary>

  ```
pipeline {
    agent any

    environment {
        SOURCE_GITHUB_URL = 'https://github.com/rlatjddms/devops-1team.git'
        MANIFEST_GITHUB_URL = 'https://github.com/rlatjddms/devops-1team-manifest.git'
        GIT_USERNAME = 'rlatjddms'
        GIT_EMAIL = 'kseo_o@naver.com'
        
        BACKEND_IMAGE = 'tjddms/spicy-backend'
        FRONTEND_IMAGE = 'tjddms/spicy-frontend'
        
        DOCKER_API_VERSION = '1.44'
    }

    tools {
        dockerTool 'docker' 
    }

    stages {
        stage('Source Build') {
            steps {
                git branch: 'dev', url: "${env.SOURCE_GITHUB_URL}"
                script {
                    dir('backend') {
                        if (isUnix()) {
                            sh "chmod +x ./gradlew"
                            sh "./gradlew clean build -Dspring.profiles.active=test"
                        } else {
                            bat "gradlew.bat clean build -Dspring.profiles.active=test"
                        }
                    }
                }
            }
        }

        stage('Docker Build and Push') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'DOCKERHUB_PASSWORD', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        try {
                            dir('backend') {
                                sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}"
                                sh "docker build -t ${env.BACKEND_IMAGE}:${currentBuild.number} ."
                                sh "docker push ${env.BACKEND_IMAGE}:${currentBuild.number}"
                            }

                            dir('frontend') {
                                sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}"
                                sh "docker build -t ${env.FRONTEND_IMAGE}:${currentBuild.number} ."
                                sh "docker push ${env.FRONTEND_IMAGE}:${currentBuild.number}"
                            }
                        } finally {
                            sh "docker logout"
                        }
                    }
                }
            }
        }

        stage('K8S Manifest Update') {
            steps {
                dir('manifest-repo') {
                    deleteDir()
                    git credentialsId: 'github', url: "${env.MANIFEST_GITHUB_URL}", branch: 'dev'
                    
                    script { 
                        withCredentials([usernamePassword(credentialsId: 'github', usernameVariable: 'GH_USER', passwordVariable: 'GH_TOKEN')]) {
                            if (isUnix()) {
                                sh """
                                    YML_FILES=\$(find . -type f \\( -name "*.yml" -o -name "*.yaml" \\))
                                    for f in \$YML_FILES; do
                                        if grep -q "${env.BACKEND_IMAGE}" "\$f"; then
                                            echo "Updating Backend image in: \$f"
                                            sed -i "s|${env.BACKEND_IMAGE}:.*|${env.BACKEND_IMAGE}:${currentBuild.number}|g" "\$f"
                                        fi
                                        
                                        if grep -q "${env.FRONTEND_IMAGE}" "\$f"; then
                                            echo "Updating Frontend image in: \$f"
                                            sed -i "s|${env.FRONTEND_IMAGE}:.*|${env.FRONTEND_IMAGE}:${currentBuild.number}|g" "\$f"
                                        fi
                                    done
                                    
                                    git config user.name "${env.GIT_USERNAME}"
                                    git config user.email "${env.GIT_EMAIL}"
                                    git add .
                                    git commit -m "[UPDATE] image version ${currentBuild.number}" || echo "No changes"
                                    git push https://\${GH_USER}:\${GH_TOKEN}@github.com/rlatjddms/devops-1team-manifest.git dev
                                """
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo '백엔드와 프론트엔드 모두 빌드 및 Manifest 업데이트 성공!'
        }
        failure {
            echo '빌드 실패! 로그를 확인해 주세요.'
        }
    }
}
```
    
</details>

---

## 12. CI/CD 테스트 🔁

#### 젠킨스

<img width="949" height="527" alt="Image" src="https://github.com/user-attachments/assets/e4e4ad6f-d767-4819-808f-daa2eb281e54" />

#### ArgoCD

<img width="1365" height="905" alt="Image" src="https://github.com/user-attachments/assets/9fabaee2-9c1e-457b-bab1-74a30734aefa" />


## 13. 회고

| 이름 | 회고 |
| --- | --- |
| 김채우 | DEVOPS에 대한 개념을 어렴풋이 알고만 있었고 정확히 어떤 원리로 동작하는지는 잘 모르고 있었습니다. 이전까지 사용해 본 적이 없기 때문에 필요성 또한 직접적으로 느끼지 못 했습니다. 하지만 이번에 CI/CD가 얼마나 강력하고 개발을 편하게 해 주는지 깨달을 수 있었습니다. 초기에 적당히 구축을 해 놓으면 이후 협업을 하는 데 있어 확실히 편안함이 있었습니다. 팀원들과 같이 공부하고 오류를 잡아가는 과정이 쉽지는 않았습니다. 하지만 결국 해내고 자동화를 이루어내며 얻은 성취감이 오히려 힘들었기에 더 컸습니다. 하나씩 이러한 기술들을 배워가며 더 효율적으로 개발하고 싶습니다. |
| 김윤경 |  |
| 김성은 | 이번 프로젝트에서 유저 도메인과 인프라를 담당했습니다. 유저 파트에서는 기본적인 인증·인가 흐름을 구현하며, 서비스 전반에서 사용자 관리가 어떻게 이루어지는지 이해할 수 있었습니다. 인프라 영역에서는 Docker, Kubernetes, Jenkins를 활용해 배포 환경을 구축했습니다. 수업 시간에는  감이 잘 오지 않았던 기술들이었지만, 실제로 컨테이너를 빌드하고 파이프라인을 구성하며 발생한 오류를 해결하는 과정을 통해 각 도구의 역할과 전체적인 흐름을 이해할 수 있었습니다. 이번 프로젝트를 통해 새로운 기술을 직접 적용하고 문제를 해결해 나가며 많은 것을 배울 수 있었고, DevOps에 대한 이해도 또한 크게 향상되었습니다. 그리고 좋은 팀원들과의 협업 덕분에 프로젝트의 완성도를 높일 수 있었습니다 |
| 유찬연 |  이번 프로젝트에서는 주로 DevOps에 관한 부분들을 중점적으로 다루기 위해 그 외의 기능들을 축소하여 진행하였다. 그 과정에서 맡은 기능이 많지 않았지만 DepOps에 대해 이해하고 프로젝트에 적용되는 과정들을 함께 하며 많은 어려움을 겪었다. 또한 각 메소드들에 대해 단위 테스트들을 작성했는데 이전에 작성했을 때는 마냥 생소하고 어려웠지만 이번에는 하나하나 이해를 하며 작성해서였는지 꽤나 재밌었다. 이번에도 역시 협업을 통해 받아와야하는 정보들이 있었어서 소통을 하는 부분에서 신경 쓸 부분이 많았는데 이전 경험들이 도움이 되어 큰 어려움이 없었다. 마지막으로 배포와 관련된 부분들에 대해서는 아직도 생소하고 이해가 부족하여 추가적인 공부가 필요하다고 생각하기에 파이널 프로젝트에 들어간 뒤에도 꾸준하게 복습을 해야할 것 같다. |
| 조윤호 |  백엔드와 프론트엔드 개발은 이전보다 수월해졌지만, 데브옵스는 처음 접하는 영역이라 Docker, Kubernetes, Jenkins, ArgoCD 등 새로운 기술들을 이해하는 데 많은 어려움이 있었습니다. 특히 자바가 아닌 환경에서 작업하다 보니 익숙하지 않은 명령어들을 다시 익혀야 했고, 전체적인 개념도 한 번에 잡히지 않아 부담이 컸습니다. 수업이 끝난 이후에도 개인적으로 데브옵스를 다시 공부하며 개념을 하나씩 정리했고, 천천히 실습을 반복하며 이해도를 높여갔습니다. 마지막 결과물을 완성하고 나니 이전에는 자바로 코딩 테스트만 풀 수 있었던 제가 하나의 애플리케이션을 만들고 배포까지 경험했다는 점에서 큰 성취감을 느낄 수 있었습니다. |


