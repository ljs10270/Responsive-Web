package chat;

import java.io.IOException;

import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import user.UserDAO;
import user.UserDTO;

@WebServlet("/ChatUnreadServlet")
public class ChatUnreadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); //매개변수들(넘어온 값들을) UTF-8으로 처리
		response.setContentType("text/html;charset=UTF-8");
		
		String userID = request.getParameter("userID");
		String chatName = null;
		
		ChatDAO chatDAO = new ChatDAO();
		
		if(userID != null) {
			userID = URLDecoder.decode(userID, "UTF-8");
			UserDTO user = new UserDAO().getUser(userID);
			chatName = user.getUserName() + '(' + user.getUserDepartment() + ')';
		} else {
			response.getWriter().write("0");
		}
		
		if(chatName == null || chatName.equals("")) {
			response.getWriter().write("0");
		}
		else {
			response.getWriter().write(chatDAO.getAllUnreadChat(chatName) + "");
		}
	}

}
