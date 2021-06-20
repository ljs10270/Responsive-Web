<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import = "user.UserDAO"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>한서대학교 족보 공유 커뮤니티</title>
</head>
<body>
	<%
	
		String userID = null;
		if(session.getAttribute("userID") != null) { //세션을 가져와 회원이 로그인 된 상태라면
			userID = (String)session.getAttribute("userID");
		}
		
		String userCheckID = null;
		if(request.getParameter("userID") != null) {
			userCheckID = (String) request.getParameter("userID");
		}
		
		if(userID == null || userID.equals("") || userCheckID == null || userCheckID.equals("")) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "비정상적인 접근 입니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		
		if(!userID.equals(userCheckID)) {
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "본인 이외에 접근할 수 없습니다.");
			response.sendRedirect("index.jsp");
			return;
		}
			
		Cookie[] cookies = request.getCookies() ;

		if(cookies != null && cookies.length > 0) {
			for (int i = 0; i < cookies.length; i++) {
					cookies[i].setMaxAge(0);                        // 특정 쿠키를 더 이상 사용하지 못하게 하기 위해 쿠키의 유효시간을 만료
					response.addCookie(cookies[i]);            // 해당 쿠키를 응답에 추가(수정)한다.		
			}
		}
		
		session.invalidate();
	%>
	<script>
		location.href = "index.jsp";
	</script>
</body>
</html>