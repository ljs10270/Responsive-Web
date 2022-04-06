<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="user.UserDTO" %>
<%@ page import="user.UserDAO" %>
<%@ page import="board.BoardDAO" %>
<%@ page import="board.BoardDTO" %>

<%@ page import="reply.ReplyDAO" %>
<%@ page import="reply.ReplyDTO" %>
<%@ page import="util.SHA256" %>
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
	
	String boardID = null;
	if (request.getParameter("boardID") != null) {
		boardID = (String) request.getParameter("boardID");
	}
	
	if(boardID == null || boardID.equals("")) {
		session.setAttribute("messageType", "오류 메시지");
		session.setAttribute("messageContent", "게시글을 선택하지 않았습니다.");
		response.sendRedirect("boardView.jsp");
		return;
	}
	
	BoardDAO boardDAO = new BoardDAO();
	BoardDTO board = boardDAO.getBoard(boardID);
	
	if(board == null) {
		session.setAttribute("messageType", "오류 메시지");
		session.setAttribute("messageContent", "게시글이 존재하지 않습니다.");
		response.sendRedirect("boardView.jsp");
		return;
	}
	
	// user는 글쓴이임
	UserDTO user = new UserDAO().getUser(board.getUserID());
	
	//댓글 리스트, 댓글쓴이는 replyUser라고 밑에 따로 잡아둠
	ArrayList<ReplyDTO> replyList = new ReplyDAO().getReplyList(boardID);
	
	// 조회수 중복 증가 쿠키 처리
	Cookie viewCookie=null;
	Cookie[] cookies=request.getCookies();
	SHA256 sha = new SHA256();
	
	if(cookies !=null && cookies.length > 0) {
		for (int i = 0; i < cookies.length; i++) {
			
			if(cookies[i].getName().equals(sha.getSHA256("viewCookie" + userID)) && cookies[i].getValue().equals(boardID)) {
				viewCookie=cookies[i];
			}
		}
	}
	
	boolean hitSequence = false;
	if (viewCookie == null) {    
        // 쿠키 생성(이름, 값)
        Cookie newCookie = new Cookie(sha.getSHA256("viewCookie" + userID), boardID);
        // 쿠키 추가
        response.addCookie(newCookie);

        if(!userID.equals(user.getUserID())) {
        	boardDAO.hit(boardID); //조회수 증가
        	hitSequence = true;
        }
        
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
	<script src="https://cdnjs.cloudflare.com/ajax/libs/bootbox.js/4.4.0/bootbox.min.js"></script>

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
		
		deleteConfirm = function(url, confiTitle, confiContent) {
			bootbox.confirm({
		    	title: confiTitle,
		    	message: confiContent,
		    	buttons: {
		        	cancel: {
		            	label: '<i class="fa fa-times"></i> 취소'
		        	},
		        	confirm: {
		            	label: '<i class="fa fa-check"></i> 삭제'
		        	}
		    	},
		    	callback: function(okay) {
			        if(okay)
			             location.href = url;
		    	} 
			}).find('.modal-content').css({
			    
			    'margin-top': function (){
			        var w = $( window ).height();
			        var b = $(".modal-dialog").height();
			        // should not be (w-h)/2
			        var h = (w-b)/2 -100;
			        return h+"px";
			    }
			});
			return false;
		}
		
		replyPrompt = function(url, promTitle) {
			bootbox.prompt({
		    	title: promTitle,
		    	inputType: 'textarea',
		    	buttons: {
		        	cancel: {
		            	label: '<i class="fa fa-times"></i> 취소'
		        	},
		        	confirm: {
		            	label: '<i class="fa fa-check"></i> 등록'
		        	}
		    	},
		    	callback: function (result) {
		    		if(result)
		    			location.href = url + '&replyContent=' + result;
		    	} 
			}).find('.modal-content').css({
			    
			    'margin-top': function (){
			        var w = $( window ).height();
			        var b = $(".modal-dialog").height();
			        // should not be (w-h)/2
			        var h = (w-b)/2 -100;
			        return h+"px";
			    }
			});
			return false;
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
		<table class="table table-bordered table-hover" style="text-align: center; border: 1px solid #dddddd">
			<thead>
				<tr>
					<th colspan="4">
					<h4><%= user.getUserName() %>(<%= user.getUserDepartment() %>)님의 게시물</h4>
					</th>
				</tr>
				
				<tr>
					<td style="background-color: #fafafa; color: #000000; width: 80px;"><h5>제목</h5></td>
					<td colspan="3"><h5><%= board.getBoardTitle() %></h5></td>
				</tr>
				
				<tr>
					<td style="background-color: #fafafa; color: #000000; width: 80px;"><h5>작성일</h5></td>
					<td><h5><%= board.getBoardDate() %></h5></td>
				
					<td style="background-color: #fafafa; color: #000000; width: 80px;"><h5 class="glyphicon glyphicon-sunglasses pull-center" aria-hidden="true"></h5></td>
				<%
					if(hitSequence) {
				%>	
					<td><h5><%= board.getBoardHit() + 1 %></h5></td>
				<%
					} else {
				%>
					<td><h5><%= board.getBoardHit() %></h5></td>
				<%
					}
				%>	
				</tr>
					
				<tr>
					<td style="background-color: #fafafa; color: #000000; width: 80px;"><h5>교과목</h5></td>
					<td><h5><%= board.getLectureType() %></h5></td>
					
					<td style="background-color: #fafafa; color: #000000; width: 80px;"><h5 class="glyphicon glyphicon-thumbs-up pull-center" aria-hidden="true"></h5></td>
					<td><h5><%= board.getLikeCount() %></h5></td>
				</tr>
				
				<tr>
					<td style="vertical-align: middle; min-height: 150px; background-color: #fafafa; color: #000000; width: 80px;"><h5>내용</h5></td>
					<td colspan="3" style="text-align: left;"><h5><%= board.getBoardContent() %></h5></td>
				</tr>
				
				<tr>
					<td style="background-color: #fafafa; color: #000000; width: 80px;"><h5 class="glyphicon glyphicon-download-alt pull-center" aria-hidden="true"></h5></td>
					<td colspan="3"><h5><a href="boardDownload.jsp?boardID=<%= board.getBoardID() %>"><%= board.getBoardFile() %></a></h5></td>
				</tr>	
			</thead>
			
			<tbody>
			
				<tr>
					<td colspan="5" style="text-align: right;">
					<a href="boardView.jsp" class="btn btn-primary pull-left"><span class="glyphicon glyphicon-arrow-left pull-center" aria-hidden="true"></span></a>
					<%
						if(userID.equals(user.getUserID())) { //서블릿으로 href 할 때는 get 방식으로 .jsp 안붙이면 된다.
							
					%>
						<a href="boardUpdate.jsp?boardID=<%= board.getBoardID() %>" class="btn btn-success"><span class="glyphicon glyphicon-pencil pull-center" aria-hidden="true"></span></a>
						<a href="boardDelete?boardID=<%= board.getBoardID() %>" class="btn btn-default" onclick="return deleteConfirm(this.href, '게시글 삭제', '게시글을 삭제 하시겠습니까?');"><span class="glyphicon glyphicon-trash pull-center" aria-hidden="true"></span></a>
					<%
						} else { 
					%>
						<a href="likeAction?userID=<%= userID %>&boardID=<%= board.getBoardID() %>" class="btn btn-danger"><span class="glyphicon glyphicon-thumbs-up pull-center" aria-hidden="true"></span></a>	
						<a href="chat.jsp?toID=<%= board.getUserID() %>" class="btn btn-success"><span class="glyphicon glyphicon-envelope pull-center" aria-hidden="true"></span></a>
					<%
						}
					%>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	
	<div class="container">
		<form method="post" action="./replyWrite">
			<table class="table table-bordered table-hover" style="text-align: center; border: 1px solid #dddddd">
				<thead>
					<tr>
						<th colspan="4">
						<h4 class="glyphicon glyphicon-comment pull-center" aria-hidden="true"></h4>
						</th>
					</tr>
				</thead>
			
				<tbody>
					<tr>
						<td><textarea class="form-control" rows="3" name="replyContent" maxlength="2048" placeholder="댓글의 내용을 입력하세요." required></textarea></td>
						<td style="width: 110px;"><input class="btn btn-info" type="submit" value="등록">
						<input type="hidden" name="userID" value="<%= userID %>">
						<input type="hidden" name="boardID" value="<%= boardID %>"></td>
					<tr>	
				</tbody>
					
			</table>
		</form>
	</div>
	
	
	<div class = "container bootstrap snippet">
		<div class="row">
			<div class="col-xs-12">	
				<div class="panel-collapse collapse in">
					
	<%
	if(replyList != null)
		for(int i = 0; i < replyList.size(); i++) {
			ReplyDTO reply = replyList.get(i);
			UserDTO replyUser = new UserDAO().getUser(reply.getUserID());
	%>
		<div class="portlet-body chat-widget" style="overflow-y: hidden; overflow-x: hidden; width: auto; height: auto;">
						<div class="row">
							<div class="col-lg-12">
								<div class="media">
			<%
				if(reply.getReplySequence() != 0) {
			%>
							<div>	
								<h4 class="glyphicon glyphicon-arrow-right pull-left" aria-hidden="true" style="width: 30px; height: 30px;"></h4>
							</div>
			<%
				}
			%>		
									<a class="pull-left">
										<img class="media-object img-circle" style="width: 30px; height: 30px;" src="<%= new UserDAO().getProfile(replyUser.getUserID()) %>" alt="">
									</a>
							
									<div class="media-body">
									
									<% 
										if(reply.getReplyAvailable() == 0 && reply.getReplyLevel() != 0) {
									%>
										<h5 class="media-heading"> <%= replyUser.getUserName() %>(<%= replyUser.getUserDepartment() %>)
											<span class="small pull-right"> <%= reply.getReplyDate() %><h5 style="color: red; text-align: right;"></h5></span>
										</h5>
										
										<p>(삭제된 댓글 입니다.)</p>
									<%
										} else {
									%>
										<h5 class="media-heading"> <%= replyUser.getUserName() %>(<%= replyUser.getUserDepartment() %>)
											<span class="small pull-right"> <%= reply.getReplyDate() %><h5 style="color: red; text-align: right;"><span class="glyphicon glyphicon-thumbs-up pull-center" aria-hidden="true"></span> <%= reply.getLikeCount() %></h5></span>
										</h5>
													
										<p><%= reply.getReplyContent() %></p>
									<%
										}
									%>
									</div>
									
									<div class="media-footer">
											<%
											if(reply.getUserID().equals(userID)) {
												if(reply.getReplySequence() == 0 && reply.getReplyAvailable() == 1) {
											%>
												<a href="replyAnswer?boardID=<%= boardID %>&replyGroup=<%= reply.getReplyGroup() %>" class="btn btn-info pull-left" onclick="return replyPrompt(this.href, '<%= replyUser.getUserName() %> 님의 댓글에 대한 답글');"><span class="glyphicon glyphicon-comment pull-center" aria-hidden="true"></span></a>
												<a href="replyDelete?boardID=<%= boardID %>&replyGroup=<%= reply.getReplyGroup() %>" class="btn btn-default pull-right" onclick="return deleteConfirm(this.href, '댓글 삭제', '댓글을 삭제 하시겠습니까?');"><span class="glyphicon glyphicon-trash pull-center" aria-hidden="true"></span></a>
											
											<%
												} if(reply.getReplySequence() != 0 && reply.getReplyAvailable() == 1){
												
											%>
												<a href="replyCommentDelete?boardID=<%= boardID %>&replyGroup=<%= reply.getReplyGroup() %>&replySequence=<%= reply.getReplySequence() %>" class="btn btn-default pull-right" onclick="return deleteConfirm(this.href, '댓글에 대한 답글 삭제', '댓글에 대한 답글을 삭제 하시겠습니까?');"><span class="glyphicon glyphicon-trash pull-center" aria-hidden="true"></span></a>
											<%		
												}	
											} else {
													if(reply.getReplySequence() == 0 && reply.getReplyAvailable() == 1) {
											%>
												
												<a href="replyAnswer?boardID=<%= boardID %>&replyGroup=<%= reply.getReplyGroup() %>" class="btn btn-info pull-left" onclick="return replyPrompt(this.href, '<%= replyUser.getUserName() %> 님의 댓글에 대한 답글');"><span class="glyphicon glyphicon-comment pull-center" aria-hidden="true"></span></a>
											<%
												}
											%>	
											
														
												<div class="dropup">
  													<button class="btn btn-default btn-sm dropdown-toggle pull-right" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-expanded="true">
    													<span class="glyphicon glyphicon-menu-hamburger pull-center" aria-hidden="true"></span>
    													<span class="caret"></span>
  													</button>
  													
  													
  													<div class="dropdown-menu pull-right" aria-labelledby="dropdownMenu1">
  													
  													
													
													<a style="width:80px;" href="chat.jsp?toID=<%= reply.getUserID() %>" class="btn btn-success pull-right"><span class="glyphicon glyphicon-envelope pull-center" aria-hidden="true"></span></a>
												 	
												
												<%
													if(reply.getReplySequence() == 0 && reply.getReplyAvailable() == 1) {
												%>
													<a style="width:80px;" href="replyLike?boardID=<%= boardID %>&replyGroup=<%= reply.getReplyGroup() %>" class="btn btn-danger pull-right"><span class="glyphicon glyphicon-thumbs-up pull-center" aria-hidden="true"></span></a>
											
												<%
													} else {
												%>
													<a style="width:80px;" href="replyCommentLike?boardID=<%= boardID %>&replyGroup=<%= reply.getReplyGroup() %>&replySequence=<%= reply.getReplySequence() %>" class="btn btn-danger pull-right"><span class="glyphicon glyphicon-thumbs-up pull-center" aria-hidden="true"></span></a>
					
												<%
													}
												%>
												
												
												</div>	
											</div>
										</div>
											<%
											}
										
									%>	
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
	</div></div>
		
	
	<%
		if(replyList.size() == 0) {
	%>
		<section class="container" style="max-width: 560px;"> <!-- 창 최대 크기 설정 -->
		<div class = "alert alert-warning" role="alert" style="text-align: center;">
			<h4>해당 게시글에 댓글이 없습니다.</h4>
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
	
	<!-- 댓글 빈칸 체크 모달 -->
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
