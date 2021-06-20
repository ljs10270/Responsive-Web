package user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.SHA256;

@WebServlet("/UserLoginServlet")
public class UserLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		String userID = request.getParameter("userID");
		String userPassword = request.getParameter("userPassword");
		
		if(userID == null || userID.equals("") || userID.trim().equals("") || userPassword == null || userPassword.equals("") || userPassword.trim().equals("")) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "모든 내용을 입력해주세요.");
			response.sendRedirect("login.jsp");
			return;
		}
		
		int result = new UserDAO().login(userID, SHA256.getSHA256(userPassword));
		
		if(result == 1) {
			request.getSession().setAttribute("userID", userID);
			
			// 쿠키 추가
			Cookie newCookie = new Cookie("userCookie", userID);
	        response.addCookie(newCookie);
			
			UserDTO user = new UserDAO().getUser(userID);
			boolean emailChecked = user.isUserEmailChecked();
			
			if(emailChecked){
				request.getSession().setAttribute("messageType", "성공 메시지");
				request.getSession().setAttribute("messageContent", "로그인에 성공했습니다.");
				response.sendRedirect("index.jsp");
			}
			else {
				request.getSession().setAttribute("messageType", "성공 메시지");
				request.getSession().setAttribute("messageContent", "이메일을 인증하지 않은 회원입니다. 이메일을 인증해 주세요.");
				response.sendRedirect("emailSendConfirm.jsp");
			}
		}
		else if(result == 0) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "비밀번호가 틀렸습니다.");
			response.sendRedirect("login.jsp");
		}
		else if(result == -1) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "아이디가 존재하지 않습니다.");
			response.sendRedirect("login.jsp");
		}
		else if(result == -2) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "데이터베이스 오류");
			response.sendRedirect("login.jsp");
		}
	}

}
