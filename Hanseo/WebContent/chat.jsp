<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="user.UserDTO" %>
<%@ page import="user.UserDAO" %>
<%@ page import = "java.net.URLDecoder" %>
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
		String fromID = null;
		if(session.getAttribute("userID") != null) { //세션을 가져와 회원이 로그인 된 상태라면
			userID = (String)session.getAttribute("userID");
			UserDTO user = new UserDAO().getUser(userID);
			fromID = user.getUserName() + "(" + user.getUserDepartment() + ")";
		}
		
		String to_ID = null;
		String toID = null;
		String toName = null; //상태방 닉네임으로 채팅방 이름 설정
		if(request.getParameter("toID") != null) {
			to_ID = (String) request.getParameter("toID");
			to_ID = URLDecoder.decode(to_ID, "UTF-8");
			UserDTO user = new UserDAO().getUser(to_ID);
			
			if(user == null) {
				session.setAttribute("messageType", "오류 메시지");
				session.setAttribute("messageContent", "대화 상대가 존재하지 않습니다.");
				response.sendRedirect("index.jsp");
				return;
			}
			
			toName = user.getUserName();
			toID = toName + "(" + user.getUserDepartment() + ")";
		}
		
		if(userID == null) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "현재 로그인이 되어 있는 않습니다.");
			response.sendRedirect("login.jsp");
			return;
		}
		
		if(to_ID == null || toID == null) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "대화 상대가 존재하지 않습니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		
		UserDTO user = new UserDAO().getUser(userID);
		if(!user.isUserEmailChecked()) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "이메일 인증을 하지 않은 회원 입니다.");
			response.sendRedirect("emailSendConfirm.jsp");
			return;
		}
		
		int existIdCheck = new UserDAO().registerCheck(to_ID); //URL에서 바로 한글로 들어오면 인코딩 안된 한글이 디코딩 되어서 에러 뜸
		if(existIdCheck == 1) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "존재하지 않는 회원에게 메시지를 보낼 수 없습니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		
		if(userID.equals(to_ID)) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "본인에게 메시지를 보낼 수 없습니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		
		//프로필 사진 가져오기
		String fromProfile = new UserDAO().getProfile(userID);
		String toProfile = new UserDAO().getProfile(to_ID);
	%>
	
	<script type="text/javascript">
		// 채팅창 자바스크립트 함수들
		
		//alert창 안보이게 처리, 선택된 alert 셀렉터를 처리한 부트스트랩으로 보여줌
		function autoClosingAlert(selector, delay) { //delay는 몇 초 동안 알림 창을 띄울지 결정
			var alert = $(selector).alert();
			alert.show();
			window.setTimeout(function() {alert.hide()}, delay);
		}
		
		function submitFunction() { //메세지 보내기 버튼을 누르면 호출됨
			var fromID = '<%= fromID %>';
			var toID = '<%= toID %>';
			var chatContent = $('#chatContent').val();
	
			//url(서버 즉 서블릿)으로 ajax를 이용하여 데이터를 보내고 결과를 리턴받음
			$.ajax({
				type: "POST",
				url: "./chatSubmitServlet",
				data: {
					fromID: encodeURIComponent(fromID), //특수문자까지 데이터로 제대로 보내고 받으려면 인코딩 필수
					toID: encodeURIComponent(toID),					
					chatContent: encodeURIComponent(chatContent) //서블릿에서는 디코딩을 해주어서 데이터를 겟파라미터로 받아야 함
					//디코더는 URLDecoder.decode(~ , "UTF-8")
				},
				success: function(result) {
					if(result == 1) { //결과를 result에 받아옴
						autoClosingAlert('#successMessage', 2000);// 2초당 밑의 부트스트랩을 이용해 만든 창의 id가 successMessage인 것을 매개변수로 보냄
					}
					else if(result == 0){
						autoClosingAlert('#dangerMessage', 2000);
					}
					else {
						autoClosingAlert('#warningMessage', 2000);
					}
				}
			});
			//메세지를 전송한 다음 내용 입력 창 비우기
			$('#chatContent').val('');
		}
		
		var lastID = 0; //가장 마지막으로 받은 메세지 카운트
		function chatListFunction(type) { //처음 페이지 로딩되면 type는 ten이고 그 다음부터는 메세지 id값이 매개변수가 됨(반복출력)
			//url(서버 즉 서블릿)으로 ajax를 이용하여 데이터를 보내고 결과를 리턴받음
			var fromID = '<%= fromID %>';
			var toID = '<%= toID %>';
			
			$.ajax({
				type: "POST",
				url: "./chatListServlet",
				data: {
					fromID: encodeURIComponent(fromID), //특수문자까지 데이터로 제대로 보내고 받으려면 인코딩 필수
					toID: encodeURIComponent(toID),
					listType: type
				},
				success: function(data) { //서블릿에 보낸 데이터를 받으면(json data)
					if(data == "")
						return; //종료 즉 파싱가능한 데이터만 파싱할 수 있도록 함
				
					var parsed = JSON.parse(data); //json형식으로 보낸 데이터(타입)의 결과를 파싱
					var result = parsed.result; //결과가 json형식의 result변수에 담겨서 오게 되는데 새로운 result에 대입
					
					for(var i = 0; i < result.length; i++) {
						if(result[i][0].value == fromID) {
							result[i][0].value = result[i][0].value + "- 본인";
						}
						
						addChat(result[i][0].value, result[i][2].value, result[i][3].value);
						// 각각 chatName(fromID), chatContent, chatTime 이 된다. [i][1]은 받는 상대 toID
					}
					lastID = Number(parsed.last); //서블릿에서 받은 결과 데이터 중 lask변수에 대입된 데이터를 반환
					//즉 가장 마지막으로 받은 메세지의 ID 번호가 대입되며 클라이언트에서 마지막으로 받았다고 데이터 갱신
				}
			});
		}
		
		function addChat(chatName, chatContent, chatTime) {
			var result = chatName.split('-');
			
			if(result[1] == ' 본인') {
				//밑의 id가 chatList인 곳에 추가
				$('#chatList').append('<div class="row">' +
						'<div class="col-lg-12">' +
						'<div class="media">' +
							'<a class="pull-left" href="#">' +
								'<img class="media-object img-circle" style="width: 30px; height: 30px;" src="<%= fromProfile %>" alt="">' +
							'</a>' +
							
							'<div class="media-body">' +
								'<h4 class="media-heading">' + chatName +
									'<span class="small pull-right">' + chatTime + '</span>' +
								'</h4>' +
								'<p>' + chatContent + '</p>' +
							'</div>' +
						'</div>' +
					'</div>' +
				'</div>' +
				'<hr>');
			} else {
				//밑의 id가 chatList인 곳에 추가
				$('#chatList').append('<div class="row">' +
						'<div class="col-lg-12">' +
						'<div class="media">' +
							'<a class="pull-left" href="#">' +
								'<img class="media-object img-circle" style="width: 30px; height: 30px;" src="<%= toProfile %>" alt="">' +
							'</a>' +
							
							'<div class="media-body">' +
								'<h4 class="media-heading">' + chatName +
									'<span class="small pull-right">' + chatTime + '</span>' +
								'</h4>' +
								'<p>' + chatContent + '</p>' +
							'</div>' +
						'</div>' +
					'</div>' +
				'</div>' +
				'<hr>');
			}
			
			//새 메세지를 받으면 스크롤을 자동으로 내림
			$('#chatList').scrollTop($('#chatList')[0].scrollHeight);
		}
		
		//1초 마다 서블릿과 통신하여 새로운 메세지가 있는지 확인하기
		function getInfiniteChat() {
			setInterval(function() { //일정 시간동안 반복적으로 수행
				chatListFunction(lastID); //마지막으로 받은 메세지의 id를 통해 
			}, 1000); // 1초마다 반복 수행
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
	
	<!-- 채팅방 디자인 -->
	<div class = "container bootstrap snippet">
		<div class="row">
			<div class="col-xs-12">
				<div class="portlet portlet-default">
					<div class="portlet-heading">
						
						<div class="portlet-title">
							<h4><i class="fa fa-circle text-green"></i><%= toName %> 님과의 채팅방 입니다.</h4>
						</div>	
						<div class="clearfix"></div>
					</div>
						
					<div id="chat" class="panel-collapse collapse in">
						<div id = "chatList" class="portlet-body chat-widget" style="overflow-y: auto; width: auto; height: 600px;">
							<!-- 위의 addChat() 자바스크립트 함수로 추가함 -->	
						</div>
							
						<div class="portlet-footer">
							<div class="row" style="height: 90px">
								<div class="form-group col-xs-10">
									<textarea style="height: 80px;" id="chatContent" class="form-control" placeholder="메세지를 입력하세요." maxlength="100" required></textarea>
								</div>
									
								<div class="form-group col-xs-2">
									<button type="button" class="btn btn-success pull-right" onclick="submitFunction();">전송</button>
									<div class="clearfix"></div>
								</div>
							</div>
						</div>
						
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 채팅방에서 메세지를 전송하면 alert 창이 떠서 불편하다. 부트스트랩을 이용해 자동알림으로 변경한다. -->
	<div class="alert alert-success" id="successMessage" style="display: none;">
		<strong>메세지 전송에 성공하였습니다.</strong>
	</div>
	
	<div class="alert alert-danger" id="dangerMessage" style="display: none;">
		<strong>내용을 입력해주세요.</strong>
	</div>
	
	<div class="alert alert-warning" id="warningMessage" style="display: none;">
		<strong>메세지 전송에 실패하였습니다.(데이터베이스 오류)</strong>
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
	
	<script type="text/javascript">
	//페이지가 로딩이 되었을 때 시간에 맞춰서(위의 자바스크립트 함수 getInfiniteChat) 메세지 출력하게 만들기
	$(document).ready(function() {
		getUnread(); //안읽은 메시지 4초마다 실행하기 전 바로 실행하기 위해
		chatListFunction('0'); //getTem() 메소드 필요 없음, ten이 아닌 0을 보내 최근 10개까지만 메세지 가져오기
		getInfiniteChat(); //위에 작성한 반복수행 함수 실행
		//즉 이떄부터 반복적으로 lastID 변수를 통해 정수를 매개변수로 대입함
		getInfiniteUnread(); //메시지함 안읽은 메시지 개수
	});
	</script>
</body>
</html>