<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="user.UserDTO" %>
<%@ page import="user.UserDAO" %>

<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width-device-width, initial-scale=1"> <!-- 반응형 웹을 위해 -->
	<link rel="stylesheet" href="css/bootstrap.css">
	<link rel="stylesheet" href="css/custom.css">
	
	<title>한서대학교 족보 공유 커뮤니티</title>
	
	<!-- Ajax를 위해 제이쿼리를 URL 링크로 가져와 쓸 것 -->
	<script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
	<script src="js/bootstrap.js"></script>
	
	<script type="text/javascript">
		function registerCheckFunction() {
			var userID = $("#userID").val();
			
			//ID예외처리
			//20자 이하, 숫자, 알파벳만 포함가능 (공백도 같이 걸러진다.)
			var userID_pattern=/^[a-zA-Z0-9]{1,20}$/;
			if(!userID_pattern.test(userID)){
				$('#checkMessage').html('아이디는 숫자와 알파벳 포함, 1~20자 이내여야 사용할 수 있습니다.');
				$('#checkType').attr('class','modal-content panel-warning');
				document.getElementById("idCheck").value="0"; //중복확인을 하지 않았다는 flag
				$('#checkModal').modal("show");
				return;
			}
			
			$.ajax({
				type: 'POST',
				url: './UserRegisterCheckServlet',
				data: {userID: userID},
				success: function(result) {
					if(result == 1) { //서블릿 반환 값으로 아이디 중복 체크 성공
						$('#checkMessage').html('사용할 수 있는 아이디 입니다.');
						$('#checkType').attr('class', 'modal-content panel-success'); //모달창
						document.getElementById("idCheck").value="1"; //중복확인을 했다는 flag
					} else if(result == 2){
						$('#checkMessage').html('아이디가 빈 칸 입니다.');
						$('#checkType').attr('class', 'modal-content panel-warning'); //모달창
					} else {
						$('#checkMessage').html('이미 존재하는 아이디 입니다.');
						$('#checkType').attr('class', 'modal-content panel-warning'); //모달창
					}
					$('#checkModal').modal("show"); //모달창 띄우기, 코드는 바디 태그 끝에 코딩함
				}
			});
		}
		
		//idCheck flag를 초기화 해주는 함수
		function init_idCheck(){
			document.getElementById("idCheck").value="0";
		}
		
		//idCheck flag를 초기화 해주는 함수
		function init_nameCheck(){
			document.getElementById("nameCheck").value="0";
		}
		
		function nameCheckFunction() {
			var userName = $("#userName").val();
			var blank_pattern = /[\s]/g; //공백인 것들 정규식
			
			if(blank_pattern.test(userName)){
				$('#checkMessage').html('닉네임에 공백이 있으면 안됩니다.');
				$('#checkType').attr('class','modal-content panel-warning');
				document.getElementById("nameCheck").value="0"; //중복확인을 하지 않았다는 flag
				$('#checkModal').modal("show");
				return;
			}
			
			$.ajax({
				type: 'POST',
				url: './UserRegisterCheckServlet',
				data: {userName: userName},
				success: function(result) {
					if(result == 1) { //서블릿 반환 값으로 닉네임 중복 체크 성공
						$('#checkMessage').html('사용할 수 있는 닉네임 입니다.');
						$('#checkType').attr('class', 'modal-content panel-success'); //모달창
						document.getElementById("nameCheck").value="1"; //중복확인을 했다는 flag
					} else if(result == 2){
						$('#checkMessage').html('닉네임을 입력하세요.');
						$('#checkType').attr('class', 'modal-content panel-warning'); //모달창
					} else {
						$('#checkMessage').html('이미 존재하는 닉네임 입니다.');
						$('#checkType').attr('class', 'modal-content panel-warning'); //모달창
					}
					$('#checkModal').modal("show"); //모달창 띄우기
				}
			});
		}
		
		function passwordCheckFunction() {
			var userPassword1 = $('#userPassword1').val();
			var userPassword2 = $('#userPassword2').val();
			var password_pattern = /^(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9])(?=.*[0-9]).{6,20}$/;
			
			//password 조건 확인
			if(!password_pattern.test(userPassword1)){
				$('#passwordCheckMessage').html('비밀번호는 알파벳,숫자,특수문자 포함 6-20자 여야합니다.');
				return ;
			}else{
				$('#passwordCheckMessage').html('');
				
				//password서로 일치하는지 확인
				if(userPassword1 != userPassword2){
					$('#passwordCheckMessage').html('비밀번호가 서로 일치하지 않습니다.');
				}else{
					$('#passwordCheckMessage').html('');
				}
			}
		}
		
		//submit전에 모든 예외처리를 확인하는 함수
		function finalCheck(){
			var idCheck = $('#idCheck').val();     //ID중복체크 여부
			var nameCheck = $('#nameCheck').val();     //ID중복체크 여부
			var userPassword1 = $('#userPassword1').val(); //Password 같은지 여부
			var userPassword2 = $('#userPassword2').val();
			var userEmail = $('#userEmail').val(); //사용자 이메일
			var password_pattern = /^(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9])(?=.*[0-9]).{8,20}$/; //비밀번호 조건
			var email_pattern = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/;
			
			//ID 중복체크를 하였는지 검사
			if(idCheck !="1"){
				$('#checkMessage').html('아이디 중복체크 해주세요');
				$('#checkType').attr('class','modal-content panel-warning');
				$('#checkModal').modal("show");
				return false;
			}
			
			//닉네임 중복체크를 하였는지 검사
			if(nameCheck != "1"){
				$('#checkMessage').html('닉네임 중복체크 해주세요');
				$('#checkType').attr('class','modal-content panel-warning');
				$('#checkModal').modal("show");
				return false;
			}
			
			if(userPassword1 != userPassword2 || !(password_pattern.test(userPassword1))){
				$('#checkMessage').html('비밀번호를 확인해주세요');
				$('#checkType').attr('class','modal-content panel-warning');
				$('#checkModal').modal("show");
				return false;
			}
			
			if(!email_pattern.test(userEmail)){
				$('#checkMessage').html('이메일 형식에 맞게 입력해주세요.');
				$('#checkType').attr('class','modal-content panel-warning');
				$('#checkModal').modal("show");
				return false;
			}
			
			document.getElementById("joinBtn").disabled = true;
			$('#passwordCheckMessage').html('회원가입 처리 중 입니다. 잠시만 기다려 주세요.');
		}
	</script>
	
</head>
<body>
	<%
		String userID = null;
		if(session.getAttribute("userID") != null) { //세션을 가져와 회원이 로그인 된 상태라면
			userID = (String)session.getAttribute("userID");
		}
		
		if(userID != null) {
			UserDTO user = new UserDAO().getUser(userID);
			boolean emailChecked = user.isUserEmailChecked();
			
			if(emailChecked){
				session.setAttribute("messageType", "오류 메시지");
				session.setAttribute("messageContent", "현재 로그인이 되어 있는 상태입니다.");
				response.sendRedirect("index.jsp");
				return;
			}
			else {
				session.setAttribute("messageType", "오류 메시지");
				session.setAttribute("messageContent", "이메일 인증을 진행해 주세요.");
				response.sendRedirect("emailSendConfirm.jsp");
				return;
			}
		}
	%>

	<!-- 맨 위 상단 네비게이션 -->
	<nav class="navbar navbar-default">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expended="false">
			
			<span class="icon-bar"></span>
			<span class="icon-bar"></span>
			<span class="icon-bar"></span>
			</button>
			
			<a class="navbar-brand" href="index.jsp">한서대학교 족보 공유 커뮤니티</a>
		</div>
		
		<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
			<ul class="nav navbar-nav">
				<li><a href="index.jsp">HOME</a>
				<li><a href="login.jsp">로그인</a></li>
				<li class="active"><a href="join.jsp">회원가입</a></li>
			</ul>
			
			<!-- 드롭다운 "메뉴" 네브바를 오른쪽 정렬하고 클릭시 위치이동 (#) caret 이미지 넣기  -->
			<ul class="nav navbar-nav navbar-right">
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
					<span class="glyphicon glyphicon-globe pull-center" aria-hidden="true"></span><span class="caret"></span>
					</a>
					
					<ul class="dropdown-menu">
						<li><a href="#">개인정보처리방침</a></li>
					</ul>
					
				</li>			
			</ul>
		</div>
	</nav>
	
	<div class="container">
		<form method="post" action="./userRegister" onSubmit="return finalCheck();">
			<table class="table table-bordered table-hover" style="text-align: center; border: 1px solid #dddddd">
				<thead>
					<tr>
						<th colspan="3"><h4>회원 가입</h4></th>
					</tr>
				</thead>
				
				<tbody>
					<tr>
						<td style="width: 110px;"><h5>아이디</h5></td>
						<td><input onkeyup="init_idCheck();" class="form-control" type="text" id="userID" name="userID" maxlength="20" placeholder="아이디를 입력하세요." required></td>
						<td style="width: 110px;"><button class="btn btn-danger" onclick="registerCheckFunction();" type="button">중복체크</button>
						<input type="hidden" name="idCheck" id="idCheck" value="0"></td>
					</tr>
					
					<tr>
						<td style="width: 110px;"><h5>비밀번호</h5></td>
						<td colspan="2"><input onKeyup="passwordCheckFunction();" class="form-control" type="password" id="userPassword1" name="userPassword1" maxlength="20" placeholder="비밀번호는 알파벳,숫자,특수문자 포함 6-20자 여야합니다." required></td>
					</tr>
					
					<tr>
						<td style="width: 110px;"><h5>비밀번호 확인</h5></td>
						<td colspan="2"><input onKeyup="passwordCheckFunction();" class="form-control" type="password" id="userPassword2" name="userPassword2" maxlength="20" placeholder="비밀번호는 알파벳,숫자,특수문자 포함 6-20자 여야합니다." required></td>
					</tr>
					
					<tr>
						<td style="width: 110px;"><h5>닉네임</h5></td>
						<td><input onkeyup="init_nameCheck();" class="form-control" type="text" id="userName" name="userName" maxlength="20" placeholder="닉네임에 공백을 포함되어 있으면 안됩니다." required></td>
						<td style="width: 110px;"><button class="btn btn-danger" onclick="nameCheckFunction();" type="button">중복체크</button>
						<input type="hidden" name="nameCheck" id="nameCheck" value="0"></td>
					</tr>
					
					<tr>
						<td style="width: 110px;"><h5>학부 </h5></td>
						<td colspan="2">
							<div class="form-group" style="text-align: senter; margin: 0 auto;">
								<div class="btn-group" data-toggle="buttons">
									
									<label class="btn btn-info active">
										<input type="radio" name="userDepartment" autocomplete="off" value="항공학부" checked>항공학부
									</label>
									
									<label class="btn btn-info">
										<input type="radio" name="userDepartment" autocomplete="off" value="항공융합학부">항공융합학부
									</label>
									
									<label class="btn btn-info">
										<input type="radio" name="userDepartment" autocomplete="off" value="보건학부">보건학부
									</label>
									
									<label class="btn btn-info">
										<input type="radio" name="userDepartment" autocomplete="off" value="디자인-엔터미디어학부">디자인-엔터미디어학부
									</label>
									
									<label class="btn btn-info">
										<input type="radio" name="userDepartment" autocomplete="off" value="융합교양학부">융합교양학부
									</label>
								</div>
							</div>
								
						</td>
					</tr>
					
					<tr>
						<td style="width: 110px;"><h5>이메일</h5></td>
						<td colspan="2"><input class="form-control" type="email" id="userEmail" name="userEmail" maxlength="50" placeholder="(이메일 인증 필수)정확히 입력해 주세요." required></td>
					</tr>
			
					<tr>
						<td style="width: 110px;"><h5>성별</h5></td>
						<td colspan="2">
							<div class="form-group" style="text-align: center; margin: 0 auto;">
								<div class="btn-group" data-toggle="buttons">
									<label class="btn btn-info active">
										<input type="radio" name="userGender" autocomplete="off" value="남자" checked>남자
									</label>
									
									<label class="btn btn-info">
										<input type="radio" name="userGender" autocomplete="off" value="여자">여자
									</label>
								</div>
							</div>
								
						</td>
					</tr>
					
					<tr>
						<td style="text-align: center;" colspan="3">
						<h5 style="color: red;" id="passwordCheckMessage"></h5>
						<input class="btn btn-success form-control" id="joinBtn" type="submit" value="가입 하기">
						</td>
						
					</tr>
				</tbody>
			</table>
		</form>
	</div>
	<%
		
		//UserRegisterServlet 에서 messageContent와 Type 세션 키에 대한 값을 정의 하였음
	
		String messageContent = null;
		if(session.getAttribute("messageContent") != null) {
			messageContent = (String) session.getAttribute("messageContent");
		}
		
		String messageType = null;
		if(session.getAttribute("messageType") != null) {
			messageType = (String) session.getAttribute("messageType");
		}
		
		if(messageContent != null) {
	%>
	
	<div class="modal fade" id="messageModal" tabindex="-1" role="dialog" aria-hidden="true">
		<div class="vertical-alignment-helper">
			<div class="modal-dialog vertical-align-center">
				<div class="modal-content 
				<% 
				if(messageType.equals("오류 메시지")) 
					out.println("panel-warning"); 
				else
					out.println("panel-success");
					%>">
					
					<div class="modal-header panel-heading">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times</span>
							<span class="sr-only">Close</span>
						</button>
						<h4 class="modal-title">
							<%= messageType %>
						</h4>
					</div>
					
					<div class="modal-body">
						<%= messageContent %>
					</div>
					
					<div class="modal-footer">
						<button type="button" class="btn btn-primary" data-dismiss="modal">확인</button>
					</div>
				</div>
							
			</div>
		</div>
	</div>
	
	<script>
		$('#messageModal').modal("show");
	</script>
	<%
		session.removeAttribute("messageContent"); //모달창 세션 파기
		session.removeAttribute("messageType");
		}
	%>
	
	<!-- id, 닉네임 중복 체크 모달 -->
	<div class="modal fade" id="checkModal" tabindex="-1" role="dialog" aria-hidden="true">
		<div class="vertical-alignment-helper">
			<div class="modal-dialog vertical-align-center">
				<div id="checkType" class="modal-content panel-info">
					
					<div class="modal-header panel-heading">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times</span>
							<span class="sr-only">Close</span>
						</button>
						<h4 class="modal-title">
							확인 메시지
						</h4>
					</div>
					
					<div id="checkMessage" class="modal-body">
					</div>
					
					<div class="modal-footer">
						<button type="button" class="btn btn-primary" data-dismiss="modal">확인</button>
					</div>
				</div>
							
			</div>
		</div>
	</div>
	
</body>
</html>