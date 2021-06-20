<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="user.UserDTO" %>
<%@ page import="user.UserDAO" %>

<%@ page import="board.BoardDTO" %>
<%@ page import="board.BoardDAO" %>

<!DOCTYPE html>
<html>
<%
	String userID = null;
	if(session.getAttribute("userID") != null) { //세션을 가져와 회원이 로그인 된 상태라면
		userID = (String)session.getAttribute("userID");
	}
	
	if(userID == null) {
		session.setAttribute("messageType", "오류 메시지");
		session.setAttribute("messageContent", "현재 로그인이 되어 있는 않습니다.");
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
	
	 String boardID = request.getParameter("boardID");
	 if(boardID == null || boardID.equals("")) {
		session.setAttribute("messageType", "오류 메시지");
		session.setAttribute("messageContent", "접근할 수 없습니다.");
		response.sendRedirect("index.jsp");
		return;
	 }
	 
	 BoardDAO boardDAO = new BoardDAO();
	 BoardDTO board = boardDAO.getBoard(boardID);
	 
	 if(!userID.equals(board.getUserID())) {
		session.setAttribute("messageType", "오류 메시지");
		session.setAttribute("messageContent", "본인 이외에 게시글을 수정할 수 없습니다.");
		response.sendRedirect("index.jsp");
		return;
	 }
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
		<!-- 파일 전송을 위해 enctype="multipart/form-data" 추가-->
		<form method="post" action="./boardUpdate" enctype="multipart/form-data">
			<table class="table table-bordered table-hover" style="text-align: center; border: 1px solid #dddddd">
				<thead>
					<tr>
						<th colspan="2"><h4>게시글 수정</h4></th>
					</tr>
				</thead>
				
				<tbody>
	
					<tr>
						<td style="width: 110px;"><h5>제목</h5></td>
						<td><input class="form-control" type="text" maxlength="50" name="boardTitle" placeholder="제목을 입력하세요." value="<%= board.getBoardTitle().replaceAll("<br>", "\n") %>" required>
						<input type="hidden" name="userID" value="<%= user.getUserID() %>">
						<input type="hidden" name="boardID" value="<%= boardID %>"></td>
					</tr>
					
					<tr>
						<td style="width: 110px;"><h5>교과목</h5></td>
						<td>
							<select name="lectureType" class="form-control">
							<option value="전공" <% if(board.getLectureType().equals("전공")) out.println("selected"); %>>전공</option>
							<!-- 만약 사용자가 전공을 검색하였으면 전공이 선택이되게 한다. -->
							<option value="교양" <% if(board.getLectureType().equals("교양")) out.println("selected"); %>>교양</option>
							<option value="Cyber/OCU" <% if(board.getLectureType().equals("Cyber/OCU")) out.println("selected"); %>>Cyber/OCU</option>
						</select>
						
						</td>
						
					</tr>	
					
					<tr>
						<td style="width: 110px;"><h5>내용</h5></td>
						<td><textarea class="form-control" rows="10" name="boardContent" maxlength="2048" placeholder="내용을 입력하세요." required><%= board.getBoardContent().replaceAll("<br>", "\n") %></textarea></td>
					</tr>
					
					<tr>
						<td style="width: 110px;"><h5 class="glyphicon glyphicon-floppy-save pull-center" aria-hidden="true"></h5></td>
						<td colspan="2">
							<input type="file" name="boardFile" class="file">
							<div class = "input-group col-xs-12">
								<span class="input-group-addon"><i class="glyphicon glyphicon-picture"></i></span>
								<input type="text" class="form-control input-lg" disabled placeholder="<%= board.getBoardFile() %>">
								<span class="input-group-btn">
									<button class="browse btn btn-primary input-lg" type="button"><i class="glyphicon glyphicon-search"></i></button>
								</span>
							</div>
						</td>
					</tr>
					
					<tr>
						<td style="text-align: center;" colspan="3">
						<a href="boardShow.jsp?boardID=<%= boardID %>" class="btn btn-primary pull-left"><span class="glyphicon glyphicon-arrow-left pull-center" aria-hidden="true"></span></a>
						<input class="btn btn-success pull-right" type="submit" value="수정">
						</td>
						
					</tr>
				</tbody>
			</table>
		</form>
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
	
	<!-- 파일 업로드 버튼의 클래스를 browse로 설정해서 수동으로 액션 처리 해주어야 함 -->
	<script type="text/javascript">
		$(document).on('click', '.browse', function() {
			var file = $(this).parent().parent().parent().find('.file');
			file.trigger('click');
		});
		
		//파일의 체인지 이벤트
		$(document).on('change', '.file', function() {
			$(this).parent().find('.form-control').val($(this).val().replace(/C:\\fakepath\\/i, ''));
		});
	</script>
</body>
</html>