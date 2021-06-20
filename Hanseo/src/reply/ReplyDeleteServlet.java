package reply;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import likey.ReplyLikeyDAO;

@WebServlet("/ReplyDeleteServlet")
public class ReplyDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response); //.jsp로 이동 안하고 URL로 바로 이 서블릿으로 요청하였음, GET 방식에서 POST 호출 
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		HttpSession session = request.getSession();
		String userID = (String) session.getAttribute("userID");
		
		String boardID = request.getParameter("boardID");
		String replyGroup = request.getParameter("replyGroup");
		
		if(userID == null || userID.equals("") || boardID == null || boardID.equals("") || replyGroup == null || replyGroup.equals("")) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "접근할 수 없습니다.");
			response.sendRedirect("index.jsp");
			return;
		}
			
		ReplyDAO replyDAO = new ReplyDAO();
		ReplyDTO reply = replyDAO.getReply(boardID, replyGroup);
		
		int level; //레벨에 따라 쿼리문 달라짐. 플래그
		if(reply.getReplyLevel() == 0) { //답변이 업는 댓글이라면
			level = 0;
			replyDAO.replyDelete(reply, level);
		} 
		else {
			level = 1;
			replyDAO.replyDelete(reply, level);
		}
		
		ReplyLikeyDAO replyLikeyDAO = new ReplyLikeyDAO();
		replyLikeyDAO.groupLikeDelete(boardID, replyGroup);
		
		request.getSession().setAttribute("messageType", "성공 메시지");
		request.getSession().setAttribute("messageContent", "댓글을 삭제하였습니다.");
		response.sendRedirect("boardShow.jsp?boardID="+ boardID);
	}

}
