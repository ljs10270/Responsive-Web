package likey;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import board.BoardDAO;

@WebServlet("/LikeActionServlet")
public class LikeActionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response); //.jsp로 이동 안하고 URL로 바로 이 서블릿으로 요청하였음, GET 방식에서 POST 호출 
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		HttpSession session = request.getSession(); //get으로 받고 post로 처리할 때 세션을 이렇게 받아와야 세션 넘기기 가능
		String userID = (String) session.getAttribute("userID");
		
		String boardID = request.getParameter("boardID");
		
		if(userID == null || userID.equals("") || boardID == null || boardID.equals("")) { //로그인된 상태가 아닌 상태
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "접근할 수 없습니다.");
			response.sendRedirect("boardView.jsp");
			return;
		}
		
		BoardDAO boardDAO = new BoardDAO();
		LikeyDAO likeyDAO = new LikeyDAO();
		int result = likeyDAO.like(userID, boardID, getClientIP(request));
		
		if(result == 1) { //성공적으로 DB에 IP 삽입이 되었으면
			result = boardDAO.like(boardID); //추천 수 증가
			
			if(result == 1) {
				session.setAttribute("messageType", "성공 메시지");
				session.setAttribute("messageContent", "해당 게시글을 추천 하였습니다.");
				response.sendRedirect("boardShow.jsp?boardID=" + boardID);
				return;
			}
			else {
				session.setAttribute("messageType", "오류 메시지");
				session.setAttribute("messageContent", "데이터베이스 오류.");
				response.sendRedirect("boardView.jsp");
				return;
			}
		}	
		else { //추천을 한 글에 또 추천을 누른 경우
			session.setAttribute("messageType", "오류 메시지");
			session.setAttribute("messageContent", "해당 게시글을 이미 추천 하셨습니다.");
			response.sendRedirect("boardShow.jsp?boardID=" + boardID);
			return;
		}
	}
	
	public static String getClientIP(HttpServletRequest request) {
		//사이트에 접속한 사용자의 IP주소를 알아내는 매소드
			String ip = request.getHeader("X-FORWARDED-FOR");
			
			//프록시 서버를 이용하는 사용자의 IP도 알아낸다.
			if (ip == null || ip.length() == 0) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			//위의 결과에도 불구하고 IP가 널이거나 길이가 0이면
			if (ip == null || ip.length() == 0) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			//위의 결과에도 불구하고 IP가 널이거나 길이가 0이면
			if (ip == null || ip.length() == 0) {
				ip = request.getRemoteAddr(); //기본 IP로 설정
			}
			return ip; //현재 접속한 사용자의 IP주소를 리턴
	}
}
