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
	
	<%
		String userID = null;
		if(session.getAttribute("userID") != null) { //세션을 가져와 회원이 로그인 된 상태라면
			userID = (String)session.getAttribute("userID");
		}
		
		if(userID == null) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "현재 로그인이 되어 있지 않습니다.");
			response.sendRedirect("login.jsp");
			return;
		}
		
		UserDTO user = new UserDAO().getUser(userID);
		if(!user.isUserEmailChecked()) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "이메일 인증을 하지 않은 회원 입니다.");
			response.sendRedirect("emailSendConfirm.jsp");
			return;
		}
	%>
	
	<script type="text/javascript">
		function findFunction() {
			var userName = $('#findName').val();
			
			$.ajax({
				type: 'POST',
				url: './UserFindServlet',
				data: {userName: userName},
				success: function(result) {
					if(result) {
						if(result == 0) {
							$('#checkMessage').html('해당 닉네임을 사용하는 학우가 없습니다.');
							$('#checkType').attr('class', 'modal-content panel-warning'); //모달창
							failFriend();
						}
						else{
							$('#checkMessage').html('학우를 찾았습니다.');
							$('#checkType').attr('class', 'modal-content panel-success'); //모달창
							getFriend(userName, result);
						}
					} 
					else {
						$('#checkMessage').html('빈 칸 없이 입력해 주세요.');
						$('#checkType').attr('class', 'modal-content panel-warning'); //모달창
						failFriend();
					}
					$('#checkModal').modal("show"); //모달창 띄우기, 코드는 바디 태그 끝에 코딩함
				}
			});
		}
		
		function getFriend(findName, result) {
			var result = result.split(',');
			
			$('#friendResult').html('<thead>' +
					'<tr>' +
					'<th><h4>검색 결과</h4></th>' +
					'</tr>' +
					'</thead>' +
					'<tbody>' +
					'<tr>' +
					'<td style="text-align: center;">' +
					'<img class="media-object img-circle" style="max-width:300px; margin : 0 auto;" src="'+ result[2] +'">' +
					'<h3>' + findName + '(' + result[1] + ')' + '</h3><a href="chat.jsp?toID=' +
							encodeURIComponent(result[0]) + '" class="btn btn-success form-control">' + '<span class="glyphicon glyphicon-envelope pull-center" aria-hidden="true"></span></a></td>' +
					'</tr>' +
					'</tbody>');
		}
		
		function failFriend() {
			$('#friendResult').html('');
		}
		
		
		//사용자가 읽지 않은 메시지 개수 출력
		function getUnread() {
			$.ajax({
				type: 'POST',
				url: './chatUnread',
				data: { 
					userID: encodeURIComponent('<%= userID %>'),
				},
				success: function(result) {
					if(result >= 1) {
						showUnread(result);
					} else {
						showUnread('');
					}
				}
			});
		}
		
		// 반복적으로 안읽은 메시지 보여주기, 바디코드 아래에서 실행시킴
		function getInfiniteUnread() {
			setInterval(function() {
				getUnread();
			}, 4000); //4초마다
		}
		
		function showUnread(result) {
			$('#unread').html(result);
		}
	</script>
</head>
<body>
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
				<li><a href="boardView.jsp">족보 공유</a></li>
				<li class="active"><a href="find.jsp">학우 찾기</a></li>
				<li><a href="box.jsp">메시지함<span id="unread" class="label label-info"></span></a></li>
				<!-- 메시지함 네브 바 옆에 라벨로 안읽은 메시지 보여줌 -->
				<li><a href="myActivity.jsp">내 활동</a></li>
				<li><a href="profileUpdate.jsp">내 정보</a></li>
			</ul>
			
			<!-- 드롭다운 "메뉴" 네브바를 오른쪽 정렬하고 클릭시 위치이동 (#) caret 이미지 넣기  -->
			<ul class="nav navbar-nav navbar-right">
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
					<span class="glyphicon glyphicon-globe pull-center" aria-hidden="true"></span><span class="caret"></span>
					</a>
					<ul class="dropdown-menu">
						<li><a href="#">개인정보처리방침</a></li>
						<%
							if(userID.equals("~~")) {
						%>
							<li><a href="userManagement.jsp">회원 관리</a></li>
						<%
							}
						%>
					</ul>
					
				</li>			
			</ul>
		</div>
	</nav>
	
	<!-- 학우 찾기 양식 -->
	<div class="container">
		<table class="table table-bordered table-hover" style="text-align: center; border: 1px solid #dddddd">
			<thead>
				<tr>
					<th colspan="2"><h4>학우 찾기</h4></th>
				</tr>
			</thead>
				
			<tbody>
				<tr>
					<td style="width: 110px;"><h4 class="glyphicon glyphicon-user pull-center" aria-hidden="true"></h4></td>
					<td><input class="form-control" type="text" id="findName" name="findName" maxlength="20" placeholder="학우의 닉네임을 입력하세요. 학우의 소속 학부는 입력하지 않으셔도 됩니다." required></td>
				</tr>
					
				<tr>
					<td colspan="2">
					<button class="btn btn-info form-control" onclick="findFunction();"><span class="glyphicon glyphicon-search pull-center" aria-hidden="true"></span></button>
					</td>		
				</tr>
			</tbody>
		</table>
	</div>
	
	<hr>
	<!-- 학우 찾기 결과 양식 -->
	<div class="container">
		<table id="friendResult" class="table table-bordered table-hover" style="text-align: center; border: 1px solid #dddddd">
			<!-- 위의 자바스크립트로 구현할 것 -->
		</table>
	</div>
	
	<%
		// 모달창
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
	
	<%
		if(userID != null) {
	%>
		<script type="text/javascript">
			$(document).ready(function() {
				getUnread(); //안읽은 메시지 4초마다 실행하기 전 바로 실행하기 위해
				getInfiniteUnread();
			});
		</script>
	<%
		}
	%>	
</body>
</html>
