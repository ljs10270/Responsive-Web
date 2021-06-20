package reply;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ReplyWriteServlet")
public class ReplyWriteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); //매개변수들(넘어온 값들을) UTF-8으로 처리
		response.setContentType("text/html;charset=UTF-8");
		
		String userID = request.getParameter("userID");
		String boardID = request.getParameter("boardID");
		String replyContent = request.getParameter("replyContent");
		
		if(userID == null || userID.equals("") || boardID == null || boardID.equals("")) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "정상적이지 못한 접근 입니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		
		if(replyContent == null || replyContent.equals("") || replyContent.trim().equals("")) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "댓글을 입력해 주세요.");
			response.sendRedirect("boardShow.jsp?boardID=" + boardID);
			return;
		}
		
		new ReplyDAO().reply(userID, boardID, replyContent);
		request.getSession().setAttribute("messageType", "성공 메시지");
		request.getSession().setAttribute("messageContent", "댓글이 작성 되었습니다.");
		response.sendRedirect("boardShow.jsp?boardID=" + boardID);
		return;
	}

}
