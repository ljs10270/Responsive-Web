package reply;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import likey.ReplyCommentLikeyDAO;

@WebServlet("/ReplyCommentDeleteServlet")
public class ReplyCommentDeleteServlet extends HttpServlet {
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
		String replySequence = request.getParameter("replySequence");
		
		if(userID == null || userID.equals("") || boardID == null || boardID.equals("") || replyGroup == null || replyGroup.equals("")
				|| replySequence == null || replySequence.equals("")) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "접근할 수 없습니다.");
			response.sendRedirect("index.jsp");
			return;
		}
			
		ReplyDAO replyDAO = new ReplyDAO();
		replyDAO.replyCommentDelete(boardID, replyGroup, replySequence);
		
		ReplyDTO parent = replyDAO.getReply(boardID, replyGroup);
		
		if(parent.getReplyLevel() != 0) {
			replyDAO.replyLevelDelete(parent);
			
			ReplyDTO checkParent = new ReplyDAO().getReply(boardID, replyGroup);
			if(checkParent.getReplyAvailable() == 0 && checkParent.getReplyLevel() == 0) {
				int level = 0;
				replyDAO.replyDelete(parent, level);
			}
		}
		
		ReplyCommentLikeyDAO replyCommentLikeyDAO = new ReplyCommentLikeyDAO();
		replyCommentLikeyDAO.SequenceLikeDelete(boardID, replyGroup, replySequence);
		
		request.getSession().setAttribute("messageType", "성공 메시지");
		request.getSession().setAttribute("messageContent", "댓글의 답변을 삭제하였습니다.");
		response.sendRedirect("boardShow.jsp?boardID="+ boardID);
	}

}
