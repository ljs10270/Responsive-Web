<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="user.UserDTO" %>
<%@ page import="user.UserDAO" %>
<%@ page import="board.BoardDAO" %>
<%@ page import="board.BoardDTO" %>
<%@ page import="reply.ReplyDAO" %>
<%@ page import="java.util.ArrayList" %>

<!DOCTYPE html>
<html>
<%
	String userID = null;
	if(session.getAttribute("userID") != null) { //세션을 가져와 회원이 로그인 된 상태라면
		userID = (String)session.getAttribute("userID");
	
		UserDTO user = new UserDAO().getUser(userID);
		if(!user.isUserEmailChecked()) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "이메일 인증을 하지 않은 회원 입니다.");
			response.sendRedirect("emailSendConfirm.jsp");
			return;
		}
	}
	
	// 쿠키로 자동 로그인
	if(userID == null) {
		Cookie[] cookies = request.getCookies() ;

		if(cookies != null && cookies.length > 0) {
			for (int i = 0; i < cookies.length; i++) {
				if(cookies[i].getName().equals("userCookie")) {
					userID = cookies[i].getValue();
					session.setAttribute("userID", userID);
					break;
				}
			}
		}
	}
	
	ArrayList<BoardDTO> boardList = new BoardDAO().getLikeList();
%>
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
			document.getElementById("reportBtn").disabled = true;
			$('#emailMessage').html('접수 중 입니다. 잠시만 기다려 주세요.');
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
				<li class="active"><a href="index.jsp">HOME</a>
			<%
				if(userID != null) {
			%>	
				<li><a href="boardView.jsp">족보 공유</a></li>
				<li><a href="find.jsp">학우 찾기</a></li>
				<li><a href="box.jsp">메시지함<span id="unread" class="label label-info"></span></a></li>
				<!-- 메시지함 네브 바 옆에 라벨로 안읽은 메시지 보여줌 -->
				<li><a href="myActivity.jsp">내 활동</a></li>
				<li><a href="profileUpdate.jsp">내 정보</a></li>
			<%
				} else {
			%>	
				<li><a href="login.jsp">로그인</a></li>
				<li><a href="join.jsp">회원가입</a></li>
			<%
				}
			%>
			</ul>
			
			<!-- 드롭다운 "메뉴" 네브바를 오른쪽 정렬하고 클릭시 위치이동 (#) caret 이미지 넣기  -->
			<ul class="nav navbar-nav navbar-right">
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
					<span class="glyphicon glyphicon-globe pull-center" aria-hidden="true"></span><span class="caret"></span>
					</a>
					<ul class="dropdown-menu">
						<li><a href="#">개인정보취급방침</a></li>
						<%
							if(userID != null && userID.equals("ljs10270")) {
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
	
	<div class="container">
		<table class="table table-bordered table-hover" style="text-align: center; border: 1px solid #dddddd">
			<thead>
				<tr>
					<th colspan="4"><h4>인기글</h4></th>
				</tr>	
			</thead>
		</table>
	</div>
	
	<div class = "container bootstrap snippet">
		<div class="row">
			<div class="col-xs-12">	
				<div class="panel-collapse collapse in">
			
			<%
				if(boardList != null)
					for(int i = 0; i < boardList.size(); i++) {
						if(i == 5) break; //인기글은 5개까지만
						BoardDTO board = boardList.get(i);
						UserDTO user = new UserDAO().getUser(board.getUserID());
			%>
					
					<div class="portlet-body chat-widget" style="overflow-y: hidden; overflow-x: hidden; width: auto; height: auto;">
					<div class="row" onclick="location.href='boardShow.jsp?boardID=<%= board.getBoardID() %>';">
						<div class="col-lg-12">
							<div class="media">
								<a class="pull-left">
									<img class="media-object img-circle" style="width: 30px; height: 30px;" src="<%= new UserDAO().getProfile(user.getUserID()) %>" alt="">
								</a>
							
								<div class="media-body">
									<h5 class="media-heading"> <%= user.getUserName() %>(<%= user.getUserDepartment() %>)
										<span class="small pull-right"> <%= board.getBoardDate() %> 
										
										<h5 style="text-align: right;">
										<span class="glyphicon glyphicon-sunglasses pull-center" aria-hidden="true"></span> <%= board.getBoardHit() %>
										&nbsp<span class="glyphicon glyphicon-comment pull-center" aria-hidden="true"></span> <%= new ReplyDAO().replyCount(board.getBoardID()) %>
										&nbsp<span style="color: red;" class="glyphicon glyphicon-thumbs-up pull-center" aria-hidden="true"></span> <span style="color: red;"><%= board.getLikeCount() %></span>
										</h5>
										
										</span>
									
									</h5>
									
									<div>
									<h5 style="color: red;">[<%= board.getLectureType() %>]</h5>			
									<h4><%= board.getBoardTitle() %></h4>
									</div>
									
								</div>
							</div>
						</div>
					</div>
					</div>
					<hr>
			<%
				}
			%>		
					
				</div>
			</div>
		</div>
	</div>
	
	<%
		if(boardList.size() == 0) {
	%>
	<section class="container" style="max-width: 560px;"> <!-- 창 최대 크기 설정 -->
		<div class = "alert alert-warning" role="alert" style="text-align: center;">
			<h4>인기글이 없습니다.</h4>
		</div>
	</section>
	<%
		}
	%>
	
	
	<div class = "container">
		<div class = "jumbotron">
			<div class = "container">
				<h2>* 한서대학교 족보 공유 웹앱</h2>
  				
  				<p>버그 및 오류, 문의사항을 아래 버튼을 눌러 접수해 주세요.</p>
  				<a class="btn btn-success form-control" data-toggle="modal" href="#reportModal" role="button">접수</a>
  				
  				<hr>
  				
  				<h3>* 한서대에 관한 다른 앱</h3>
  				<div class="row">
 					 <div class="col-sm-6 col-md-6">
    					<div class="thumbnail">
      						<img class="media-object img-rounded" style="width: 500px; height: 200px;" src="./images/game.jpg" alt="호환되지 않는 브라우저">
      							<div class="caption">
        							<h4>한서대 게임공장</h4>
        								<h5>한서대학교를 주제로 개발한 모바일 게임. 게임을 즐기고 학교에 대한 정보를 획득해 보세요</h5>
        								<a href="https://play.google.com/store/apps/details?id=com.ljs10270.HanseoGameFactory" target="_blank" class="btn btn-info form-control" role="button">Go</a>
      							</div>
    					</div>
  					</div>
  					
  					<div class="col-sm-6 col-md-6">
    					<div class="thumbnail">
      						<img class="media-object img-rounded" style="width: 500px; height: 200px;" src="./images/search.jpg" alt="호환되지 않는 브라우저">
      							<div class="caption">
        							<h4>한서대 강의 검색 앱</h4>
        								<h5>한서대학교에 입학을 희망하는 중고등학생, 편입 예정자들을 위한 전공 및 교양과목 검색 애플리케이션.</h5>
        								<a href="https://play.google.com/store/apps/details?id=com.ljs10270.registeration" target="_blank" class="btn btn-info form-control" role="button">Go</a>
      							</div>
    					</div>
  					</div>
				</div>
				
				<div class="text-center" style="color: #FFFFFF; background-color: #000000;">
					<!-- 만든이 이름 들어가는 곳 -->
					<p>Copyright &copy; 
					2020. Jaeseon Lee. 
					All rights reserved.</p>
				</div>
				
			</div>
		</div>
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
	
	<div class="modal fade" id="reportModal" tabindex="-1" role="dialog" aria-labelledby="modal" aria-hidden="true">
	<!-- 신고하기 버튼을 눌렀을 때 다이어로그 형식인 모달 창이 띄어진다. -->
		<div class="modal-dialog">
			<div class="modal-content"> <!-- 모달 창의 내용을 정해준다 모달은 해더(제목),바디(내용),푸터(제출,취소 버튼)로 나눠진다. -->
				
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<!-- 제목(신고하기) 옆에 닫기 지정 -->
						<span aria-hidden="true">&times;</span> <!-- 모달에서 닫기는 이렇게 생성 x로 닫기버튼이 생성된다.-->
					</button>
					<h3 class="modal-title" id="modal">문의사항</h3>
				</div>
				
				<div class="modal-body">
					<form action="./userReport" method="post" onSubmit="return btnDisabled();">					
						<div class="form-group">
							<label>제목</label>
							<input type="text" name="rTitle" class="form-control" maxlength="50">
							<input type="hidden" name="userID" value="<%= userID %>">
						</div>
						
						<div class="form-group">
							<label>내용</label>
							<textarea name="rContent" class="form-control" maxlength="4096" style="height: 180px;"></textarea>
						</div>
						
						<div class="modal-footer">
							<button type="button" class="btn btn-secondary" data-dismiss="modal">취소</button>
							<button type="submit" class="btn btn-success" id="reportBtn">접수</button>
							<!--btn-danger = 빨간색 버튼으로 설정 -->
							<h5 style="color: red; text-align: left;" id="emailMessage"></h5>
						</div>
					</form>
				</div>
			</div>
		</div>	
	</div>
	
</body>
</html>