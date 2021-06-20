<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="user.UserDTO" %>
<%@ page import="user.UserDAO" %>
<%@ page import="board.BoardDAO" %>
<%@ page import="board.BoardDTO" %>
<%@ page import="reply.ReplyDAO" %>
<%@ page import="reply.ReplyDTO" %>
<%@ page import="java.util.ArrayList" %>

<!DOCTYPE html>
<html>
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
	
	if(!new UserDAO().getUser(userID).isUserEmailChecked()) {
		session.setAttribute("messageType", "오류 메시지");
		session.setAttribute("messageContent", "이메일 인증을 하지 않은 회원 입니다.");
		response.sendRedirect("emailSendConfirm.jsp");
		return;
	}
	
	UserDTO user = new UserDAO().getUser(userID);
	
	//본인 게시글 리스트
	ArrayList<BoardDTO> boardList = new BoardDAO().getMyList(userID);
	
	//본인 댓글 리스트
	ArrayList<ReplyDTO> replyList = new ReplyDAO().getMyReplyList(userID);
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
				<li><a href="find.jsp">학우 찾기</a></li>
				<li><a href="box.jsp">메시지함<span id="unread" class="label label-info"></span></a></li>
				<!-- 메시지함 네브 바 옆에 라벨로 안읽은 메시지 보여줌 -->
				<li class="active"><a href="myActivity.jsp">내 활동</a></li>
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
							if(userID.equals("ljs10270")) {
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
					<th colspan="4"><h4><%= user.getUserName() %> 님이 작성한 게시글</h4></th>
				</tr>	
			</thead>
		</table>
	</div>
	
	<div class = "container bootstrap snippet">
		<div class="row">
			<div class="col-xs-12">	
				<div class="panel-collapse collapse in">
				
					
			
			<%
				if(boardList != null) {
					for(int i = 0; i < boardList.size(); i++) {
						BoardDTO board = boardList.get(i);
							if(board.getUserID().equals(userID)) {
							
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
									
									<h5 style="color: red;">[<%= board.getLectureType() %>]</h5>			
									<h4><%= board.getBoardTitle() %></h4>
								</div>
							</div>
							
						</div>
					</div>
					</div>
					<hr>
	<%
				}
			}
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
			<h4>작성하신 글이 없습니다.</h4>
		</div>
	</section>
	<%
		}
	%>
	
	<hr>
	
	<div class="container">
		<table class="table table-bordered table-hover" style="text-align: center; border: 1px solid #dddddd">
			<thead>
				<tr>
					<th colspan="4"><h4><%= user.getUserName() %> 님이 작성한 댓글</h4></th>
				</tr>	
			</thead>
		</table>
	</div>
	
	<div class = "container bootstrap snippet">
		<div class="row">
			<div class="col-xs-12">	
				<div class="panel-collapse collapse in">
					
	<%
	if(replyList != null){
		for(int i = 0; i < replyList.size(); i++) {
			ReplyDTO reply = replyList.get(i);
				if(reply.getUserID().equals(userID)) {
	%>
					<div class="portlet-body chat-widget" style="overflow-y: hidden; overflow-x: hidden; width: auto; height: auto;">	
						<div class="row" onclick="location.href='boardShow.jsp?boardID=<%= reply.getBoardID() %>';">
							<div class="col-lg-12">
							<!--<span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>-->
								<div class="media">
									<a class="pull-left">
										<img class="media-object img-circle" style="width: 30px; height: 30px;" src="<%= new UserDAO().getProfile(user.getUserID()) %>" alt="">
									</a>
							
									<div class="media-body">
										<h5 class="media-heading"> <%= user.getUserName() %>(<%= user.getUserDepartment() %>)
											<span class="small pull-right"> <%= reply.getReplyDate() %><h5 style="color: red; text-align: right;"><span class="glyphicon glyphicon-thumbs-up pull-center" aria-hidden="true"></span> <%= reply.getLikeCount() %></h5></span>
										</h5>
													
										<p><%= reply.getReplyContent() %></p>
									
									</div>
								</div>	
							</div>
							</div>
						</div>
						<hr>
			<%
						}
					}
				}
			%>		
					
				</div>
			</div>
		</div>
	</div>
	
	<%
		if(replyList.size() == 0) {
	%>
	<section class="container" style="max-width: 560px;"> <!-- 창 최대 크기 설정 -->
	<!-- 섹션은 본문을 작성할 떄 사용하고 컨테이너는 요소들이 알아서 화면 크기에 맞게 작아지도록 만들어준다. MT는 마진이며 위쪽으로 3올라가게 위치조절 -->
		<div class = "alert alert-warning" role="alert" style="text-align: center;">
			<h4>작성하신 댓글이 없습니다.</h4>
		</div>
	</section>
	<%
		}
	%>
	
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
	
</body>
</html>