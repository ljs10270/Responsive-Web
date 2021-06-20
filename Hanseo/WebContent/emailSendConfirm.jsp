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
		function btnDisabled() {
			document.getElementById("reBtn").disabled = true;
			$('#emailMessage').html('이메일 전송 중 입니다. 잠시만 기다려 주세요.');
		}
	
	</script>
	
</head>
<body>
	<%
		String userID = null;
		UserDTO user = null;
		if(session.getAttribute("userID") != null) { //세션을 가져와 회원이 로그인 된 상태라면
			userID = (String)session.getAttribute("userID");
		}
		
		if(userID == null || userID.equals("")) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "정상적인 접근이 아닙니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		
		user = new UserDAO().getUser(userID);
		
		if(user.isUserEmailChecked()) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "이미 이메일 인증을 하셨습니다.");
			response.sendRedirect("index.jsp");
			return;
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
				<li><a href="logoutAction.jsp">로그아웃</a></li>
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
		<form method="post" action="./userEmailCheck">
			<table class="table table-bordered table-hover" style="text-align: center; border: 1px solid #dddddd">
				<thead>
					<tr>
						<th colspan="3"><h4>이메일 인증</h4></th>
					</tr>
				</thead>
				
				<tbody>
					<tr>
						<td style="width: 110px;"><h5>인증 코드</h5></td>
						<td><input class="form-control" type="text" id="userEmailCheck" name="userEmailCheck" maxlength="64" placeholder="인증코드를 복사하여 붙여넣기 하세요." required>
						<input type="hidden" id="userID" name="userID" value=<%=userID %>></td>
					</tr>
					
					<tr>
						<td style="text-align: senter;" colspan="3">
						<input class="btn btn-success form-control" type="submit" value="인증하기">
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>

	<hr>
		
	<div class="container">
		<form method="post" action="./userEmailCheck" onSubmit="return btnDisabled();">
			<table class="table table-bordered table-hover" style="text-align: center; border: 1px solid #dddddd">
				<thead>
					<tr>
						<th colspan="3"><h4>회원 가입 할 때 이메일을 잘못 입력하셨나요?</h4></th>
					</tr>
				</thead>
				
				<tbody>
					<tr>
						<td style="width: 110px;"><h5>새로운 이메일</h5></td>
						<td colspan="2"><input class="form-control" type="email" id="userNewEmail" name="userNewEmail" maxlength="50" placeholder="인증 받을 새로운 이메일을 입력하세요." required>
						<input type="hidden" id="userID" name="userID" value=<%=userID %>></td>
					</tr>
					
					<tr>
						<td style="text-align: center;" colspan="3">
						<h5 style="color: red;" id="emailMessage"></h5>
						<input class="btn btn-info form-control" id="reBtn" type="submit" value="인증 코드 다시 받기">
						</td>
						
					</tr>
					
				</tbody>
			</table>
		</form>
	</div>	
					
	<%
	
		
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
	
</body>
</html>