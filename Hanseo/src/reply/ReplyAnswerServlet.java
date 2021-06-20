package reply;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ReplyAnswerServlet")
public class ReplyAnswerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response); //.jsp로 이동 안하고 URL로 바로 이 서블릿으로 요청하였음, GET 방식에서 POST 호출 
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); //매개변수들(넘어온 값들을) UTF-8으로 처리
		response.setContentType("text/html;charset=UTF-8");
		
		HttpSession session = request.getSession();
		String userID = (String) session.getAttribute("userID");
		
		String boardID = request.getParameter("boardID");
		String replyGroup = request.getParameter("replyGroup");
		
		if(userID == null || userID.equals("") || boardID == null || boardID.equals("") || replyGroup == null || replyGroup.equals("")) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "정상적이지 못한 접근 입니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		
		String replyContent = request.getParameter("replyContent");
		
		if(replyContent == null || replyContent.equals("") || replyContent.trim().equals("")) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "내용을 입력해 주세요.");
			response.sendRedirect("boardShow.jsp?boardID=" + boardID);
			return;
		}
		
		ReplyDAO replyDAO = new ReplyDAO();
		ReplyDTO parent = replyDAO.getReply(boardID, replyGroup);
		
		replyDAO.replyAnswer(userID, parent.getBoardID() + "", replyContent, parent);
		replyDAO.replyLevelUpdate(parent); //레벨 값 +1
		
		request.getSession().setAttribute("messageType", "성공 메시지");
		request.getSession().setAttribute("messageContent", "댓글에 대한 답변이 작성 되었습니다.");
		response.sendRedirect("boardShow.jsp?boardID=" + parent.getBoardID() + "");
		return;
	}

}
