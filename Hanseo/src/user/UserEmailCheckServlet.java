package user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import user.UserDAO;
import util.EmailSend;
import util.SHA256;

@WebServlet("/UserEmailCheckServlet")
public class UserEmailCheckServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		String userID = request.getParameter("userID");
		String userEmailCheck = request.getParameter("userEmailCheck");
		String userNewEmail = request.getParameter("userNewEmail");
		
		if(userID == null || userID.equals("")) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "정상적인 접근이 아닙니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		
		if(userEmailCheck == null && (userNewEmail == null || userNewEmail.equals("") || userNewEmail.trim().equals(""))) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "새로운 이메일을 제대로 입력해 주세요.");
			response.sendRedirect("emailSendConfirm.jsp");
			return;
		}
		
		if(userNewEmail == null && (userEmailCheck == null || userEmailCheck.equals("") || userEmailCheck.trim().equals(""))) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "인증코드를 제대로 입력해 주세요.");
			response.sendRedirect("emailSendConfirm.jsp");
			return;
		}
		
		UserDAO userDAO = new UserDAO();
		
		if(userEmailCheck != null) {
			UserDTO user = userDAO.getUser(userID);
			String userEmailHash = user.getUserEmailHash();
			
			if(!userEmailCheck.equals(userEmailHash)) {
				request.getSession().setAttribute("messageType", "오류 메시지");
				request.getSession().setAttribute("messageContent", "인증코드가 올바르지 않습니다.");
				response.sendRedirect("emailSendConfirm.jsp");
				return;
			}
			
			// 쿠키 추가
			Cookie newCookie = new Cookie("userCookie", userID);
			response.addCookie(newCookie);
			
			userDAO.setUserEmailChecked(userID); //인증여부를 true로 설정
			userEmailCheck = null;
			request.getSession().setAttribute("messageType", "성공 메시지");
			request.getSession().setAttribute("messageContent", "이메일 인증에 성공하여 회원가입이 완료 되었습니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		
		if(userNewEmail != null) {
			userDAO.setUserEmail(userID, userNewEmail); //사용자 이메일 수정
			
			String userNewEmailHash = SHA256.getSHA256(userNewEmail);
			userDAO.setUserEmailHash(userID, userNewEmailHash);
			
			boolean emailSendChecked = new EmailSend().EmailSendAction(userID);
			
			if(emailSendChecked) {
				userNewEmail = null;
				request.getSession().setAttribute("messageType", "성공 메시지");
				request.getSession().setAttribute("messageContent", "새로운 이메일로 인증 코드가 발송 되었습니다.");
				response.sendRedirect("emailSendConfirm.jsp");
				return;
			}
			else {
				userNewEmail = null;
				request.getSession().setAttribute("messageType", "오류 메시지");
				request.getSession().setAttribute("messageContent", "새로운 이메일로 인증 코드 발송 실패.");
				response.sendRedirect("emailSendConfirm.jsp");
				return;
			}
			
			
		}
		
	}

}
