package user;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.SHA256;
import user.UserDAO;
import util.EmailSend;

@WebServlet("/UserRegisterServlet")
public class UserRegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		String userID = request.getParameter("userID");
		String userPassword1 = request.getParameter("userPassword1");
		String userPassword2 = request.getParameter("userPassword2");
		String userName = request.getParameter("userName");
		String userDepartment = request.getParameter("userDepartment");
		String userGender = request.getParameter("userGender");
		String userProfile = request.getParameter("userProfile");
		String userEmail = request.getParameter("userEmail");
		
		if(userID == null || userID.equals("") || userID.trim().equals("") || userPassword1 == null || userPassword1.equals("") || userPassword1.trim().equals("") || userPassword2 == null || userPassword2.equals("") 
				|| userPassword2.trim().equals("") || userName == null || userName.equals("") || userName.trim().equals("") || userDepartment == null || userDepartment.equals("") || userGender == null || userGender.equals("") ||
				userEmail == null || userEmail.equals("") || userEmail.trim().equals("")) {
			
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "모든 내용을 입력하세요.");
			response.sendRedirect("join.jsp");
			return;
		}
		
		if(!userPassword1.equals(userPassword2)) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "비밀번호가 서로 다릅니다.");
			response.sendRedirect("join.jsp");
			return;
		}
		
		UserDAO userDAO = new UserDAO();

		// 회원가입 완료
		int result = userDAO.register(userID, SHA256.getSHA256(userPassword1), userName, userDepartment, userGender, "", userEmail, SHA256.getSHA256(userEmail));
		// 프로필은 회원가입 할 때만 ""로
		
		if(result == 1) { //성공했다면 인증 메일 해시해서 보내기
			UserDTO user = new UserDAO().getUser(userID);
			boolean emailChecked = user.isUserEmailChecked();
			
			if(emailChecked == true) {
				request.getSession().setAttribute("messageType", "확인 메시지");
				request.getSession().setAttribute("messageContent", "이미 인증된 회원 입니다.");
				response.sendRedirect("index.jsp");
				return;
			}
			
			boolean emailSendChecked = new EmailSend().EmailSendAction(userID);
			
			if(emailSendChecked) {
				request.getSession().setAttribute("userID", userID); //세션값으로 userID 값을 넣어서 서버에서 로그인된 사용자를 관리
				request.getSession().setAttribute("messageType", "성공 메시지");
				request.getSession().setAttribute("messageContent", "입력하신 이메일로 인증 코드가 전송 되었습니다. 이메일을 인증해 주세요.");
				response.sendRedirect("emailSendConfirm.jsp");
			}
			else {
				request.getSession().setAttribute("messageType", "오류 메시지");
				request.getSession().setAttribute("messageContent", "이메일 전송 오류 발생");
				response.sendRedirect("join.jsp");
			}
		}
	}
}
