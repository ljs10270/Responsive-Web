package board;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import likey.LikeyDAO;
import likey.ReplyCommentLikeyDAO;
import likey.ReplyLikeyDAO;
import reply.ReplyDAO;


@WebServlet("/BoardDeleteServlet")
public class BoardDeleteServlet extends HttpServlet {
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
		if(boardID == null || boardID.equals("")) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "접근할 수 없습니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		
		BoardDAO boardDAO = new BoardDAO();
		BoardDTO board = boardDAO.getBoard(boardID);
		
		if(!userID.equals(board.getUserID())) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "작성자 본인 이외에 접근할 수 없습니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		
		// 업로드 경로를 WAS의 /upload 폴더로 경로 설정, WAS마다 경로 다를 수 있음
		String savePath = request.getRealPath("/upload").replaceAll("\\\\", "/");
		String prev = boardDAO.getRealFile(boardID);
		int result = boardDAO.delete(boardID);
		
		if(result == -1) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "작성자 본인 이외에 접근할 수 없습니다.");
			response.sendRedirect("index.jsp");
			return;
		} else {
			File prevFile = new File(savePath + "/" + prev);
			if(prevFile.exists()) {
				prevFile.delete();
			}
			
			LikeyDAO likeyDAO = new LikeyDAO();
			likeyDAO.likeDelete(boardID); //게시글 추천 DB 레코드 삭제
			
			ReplyLikeyDAO replyLikeyDAO = new ReplyLikeyDAO();
			replyLikeyDAO.boardLikeDelete(boardID);
			
			ReplyCommentLikeyDAO replyCommentLikeyDAO = new ReplyCommentLikeyDAO();
			replyCommentLikeyDAO.boardLikeDelete(boardID);
			
			ReplyDAO replyDAO = new ReplyDAO();
			replyDAO.replyDelete(boardID);
			
			request.getSession().setAttribute("messageType", "성공 메시지");
			request.getSession().setAttribute("messageContent", "게시글을 삭제하였습니다.");
			response.sendRedirect("boardView.jsp");
		}
	}

}
