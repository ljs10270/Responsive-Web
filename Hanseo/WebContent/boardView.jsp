<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="user.UserDTO" %>
<%@ page import="user.UserDAO" %>
<%@ page import="board.BoardDAO" %>
<%@ page import="board.BoardDTO" %>
<%@ page import="reply.ReplyDAO" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import = "java.net.URLEncoder"%>

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
	
	request.setCharacterEncoding("UTF-8");
	String lectureType = "전체";
	String searchType = "최신순";
	String search = "";
	int pageNumber = 0;
	
	if(request.getParameter("lectureType") != null) { //사용자가 검색을 했다면
		lectureType = request.getParameter("lectureType");
	}
	if(request.getParameter("searchType") != null) { //사용자가 검색을 했다면
		searchType = request.getParameter("searchType");
	}
	if(request.getParameter("search") != null) { //사용자가 검색을 했다면
		search = request.getParameter("search");
	}
	if(request.getParameter("pageNumber") != null) { //사용자가 검색을 했다면
		try {
			pageNumber = Integer.parseInt(request.getParameter("pageNumber"));	
		} catch (Exception e) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "페이지 번호가 잘못되었습니다.");
			response.sendRedirect("boardView.jsp");
			return;
		}
	}
	
	ArrayList<BoardDTO> boardList = new BoardDAO().getList(lectureType, searchType, search, pageNumber);
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
			$('#emailMessage').html('신고 접수 중 입니다. 잠시만 기다려 주세요.');
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
				<li class="active"><a href="boardView.jsp">족보 공유</a></li>
				<li><a href="find.jsp">학우 찾기</a></li>
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
	
	<div class="container">
		<form method="get" action="./boardView.jsp">
		<table class="table table-bordered table-hover" style="text-align: center; border: 1px solid #dddddd">
			<thead>
				<tr>
					<th colspan="4"><h4>족보 공유</h4></th>
				</tr>	
			</thead>
			
			<tbody>
				<tr>
					<td>	 
						<!-- 사용자가 검색을 통해 전송되는 데이터를 GET방식으로 서버로 전송한다. MT-3은 위쪽으로 3마진이다.-->
						<select name="lectureType" class="form-control" style="text-align: senter;">
						<!-- 사용자가 어떤 강의를 선택해야하므로 강의를 구분하는 것을 lectureDivide으로 설정함 -->
							<option value="전체">전체</option>
							<option value="전공" <% if(lectureType.equals("전공")) out.println("selected"); %>>전공</option>
							<!-- 만약 사용자가 전공을 검색하였으면 전공이 선택이되게 한다. -->
							<option value="교양" <% if(lectureType.equals("교양")) out.println("selected"); %>>교양</option>
							<option value="Cyber/OCU" <% if(lectureType.equals("Cyber/OCU")) out.println("selected"); %>>Cyber/OCU</option>
						</select>		
						
						<select name="searchType" class="form-control">
							<option value="최신순">최신순</option>
							<option value="추천순" <% if(searchType.equals("추천순")) out.println("selected"); %>>추천순</option>
						</select>
					</td>
				</tr>
						
				<tr>
					<td>
						<input type="text" name="search" class="form-control" placeholder="검색할 내용을 입력하세요.">
						<!-- 여기서 SEARCH는 사용자가 강의에 대한 정보를 찾기 위해 검색한 내용이 된다 . -->
					</td>
				</tr>
					
				<tr>
					<td>
						<button type="submit" class="btn btn-info form-control"><span class="glyphicon glyphicon-search pull-center" aria-hidden="true"></span></button>
					</td>	
				</tr>
			</tbody>
		</table>
		</form>
	</div>
			
			
	<div class="container">
		<table class="table table-bordered table-hover" style="text-align: center; border: 1px solid #dddddd">				
			<tbody>
				<tr>
				<td colspan="3"><a href="boardWrite.jsp" class="btn btn-success pull-right" type="submit"><span class="glyphicon glyphicon-pencil pull-center" aria-hidden="true"></span></a>
				
				<ul class = "pagination" style="margin: 0 auto;">
<%
	if(pageNumber <= 0) {	
%>
	<li><span class="glyphicon glyphicon-chevron-left" style="color: gray;"></span></li>
<%
	} else {
%>
	<li><a href="./boardView.jsp?lectureType=<%= URLEncoder.encode(lectureType, "UTF-8") %>
	&searchType=<%= URLEncoder.encode(searchType, "UTF-8") %>&search=<%= URLEncoder.encode(search, "UTF-8") %>
	&pageNumber=<%= pageNumber - 1 %>"><span class="glyphicon glyphicon-chevron-left"></span></a></li>
<%
	}
%>
		
<%
	if(boardList.size() < 11) {	 //한페이지에 10개만 보인다. 11개의 게시글이 넘어오는데 11개보다 적으면 다음페이지가 존재하지 않는 것이다.
%>
	<li><span class="glyphicon glyphicon-chevron-right" style="color: gray;"></span></li>
<%
	} else {
%>
	<li><a href="./boardView.jsp?lectureType=<%= URLEncoder.encode(lectureType, "UTF-8") %>
	&searchType=<%= URLEncoder.encode(searchType, "UTF-8") %>&search=<%= URLEncoder.encode(search, "UTF-8") %>
	&pageNumber=<%= pageNumber + 1 %>"><span class="glyphicon glyphicon-chevron-right"></span></a></li>
<%
	}
%>
	</ul>
				
				<a class="btn btn-danger mx-1 mt-2 pull-left" data-toggle="modal" href="#reportModal">신고</a></td>
			</tr>
			
			</tbody>
		</table>
	</div>
		
	
	
	<div class = "container bootstrap snippet">
		<div class="row">
			<div class="col-xs-12">	
				<div class="panel-collapse collapse in">
				
					
			
			<%
				if(boardList != null)
					for(int i = 0; i < boardList.size(); i++) {
						if(i == 10) break; //한 페이지에 10개까지만 출력
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
	
	<footer class="text-center">
	<!-- 페이지네이션 사용 - 부트스트랩 웹 디자인 프레임워크에서 제공됨 -->
	<ul class = "pagination" style="margin: 0 auto;">
<%
	if(pageNumber <= 0) {	
%>
	<li><span class="glyphicon glyphicon-chevron-left" style="color: gray;"></span></li>
<%
	} else {
%>
	<li><a href="./boardView.jsp?lectureType=<%= URLEncoder.encode(lectureType, "UTF-8") %>
	&searchType=<%= URLEncoder.encode(searchType, "UTF-8") %>&search=<%= URLEncoder.encode(search, "UTF-8") %>
	&pageNumber=<%= pageNumber - 1 %>"><span class="glyphicon glyphicon-chevron-left"></span></a></li>
<%
	}
%>
		
<%
	if(boardList.size() < 11) {	 //한페이지에 10개만 보인다. 11개의 게시글이 넘어오는데 11개보다 적으면 다음페이지가 존재하지 않는 것이다.
%>
	<li><span class="glyphicon glyphicon-chevron-right" style="color: gray;"></span></li>
<%
	} else {
%>
	<li><a href="./boardView.jsp?lectureType=<%= URLEncoder.encode(lectureType, "UTF-8") %>
	&searchType=<%= URLEncoder.encode(searchType, "UTF-8") %>&search=<%= URLEncoder.encode(search, "UTF-8") %>
	&pageNumber=<%= pageNumber + 1 %>"><span class="glyphicon glyphicon-chevron-right"></span></a></li>
<%
	}
%>
	</ul>
	</footer>
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
					<h3 class="modal-title" id="modal"><%= userID %>님의 신고 접수서</h3>
				</div>
				
				<div class="modal-body">
					<form action="./boardReport" method="post" onSubmit="return btnDisabled();">					
						<div class="form-group">
							<label>신고 제목</label>
							<input type="text" name="rTitle" class="form-control" maxlength="50">
							<input type="hidden" name="userID" value="<%= userID %>">
						</div>
						
						<div class="form-group">
							<label>신고 내용</label>
							<textarea name="rContent" class="form-control" maxlength="4096" style="height: 180px;"></textarea>
						</div>
						
						<div class="modal-footer">
							<button type="button" class="btn btn-secondary" data-dismiss="modal">취소</button>
							<button type="submit" class="btn btn-danger" id="reportBtn">신고하기</button>
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
