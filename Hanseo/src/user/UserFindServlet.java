package user;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/UserFindServlet")
public class UserFindServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		String userName = request.getParameter("userName");
		 
		if(userName != null) {
			if(userName.equals("") || userName.trim().equals("")) {
				return; //아래 코드를 실행하지 않기에 find.jsp에서 result 값이 자동으로 null이 됨
			}
			
			String userID = new UserDAO().getUserID(userName);
			
			if (userID == null || userID.equals("")) {
				response.getWriter().write(0 + "");
				return;
			}
			else {
				String userDepartment = new UserDAO().getUserDepartment(userName);
				String userProfile = new UserDAO().getProfile(userID);
				response.getWriter().write(userID);
				response.getWriter().write(',' + userDepartment);
				response.getWriter().write(',' + userProfile);
				userName = null;
			}
		} 
	}

}
