package chat;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ChatSubmitServlet")
public class ChatSubmitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); //매개변수들(넘어온 값들을) UTF-8으로 처리
		response.setContentType("text/html;charset=UTF-8");
		
		String fromID = request.getParameter("fromID");
		String toID = request.getParameter("toID");
		String chatContent = request.getParameter("chatContent");
		
		chatContent = URLDecoder.decode(chatContent, "UTF-8");
		
		if(fromID == null || fromID.equals("") || toID == null || toID.equals("") || chatContent == null || chatContent.equals("")
				|| chatContent.trim().equals("")) {
			response.getWriter().write("0");
		} 
		else if(fromID.equals(toID)) { //자기 자신한테 메시지 못보내게 함
			response.getWriter().write("-1");
		} 
		else {
			fromID = URLDecoder.decode(fromID, "UTF-8"); //디코딩
			toID = URLDecoder.decode(toID, "UTF-8");
			response.getWriter().write(new ChatDAO().submit(fromID, toID, chatContent) + "");
			//write는 문자열만 가능 DAO의 submit매소드(메시지 전송)는 숫자를 반환하기에 마지막에 공백을 넣어 문자로 변환
		}
		
		
	}

}
