package chat;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ChatListServlet")
public class ChatListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); //매개변수들(넘어온 값들을) UTF-8으로 처리
		response.setContentType("text/html;charset=UTF-8");
		
		String fromID = request.getParameter("fromID");
		String toID = request.getParameter("toID");
		String listType = request.getParameter("listType");
		
		if(fromID == null || fromID.equals("") || toID == null || toID.equals("") || listType == null || listType.equals("")) {
			response.getWriter().write("");
		} 
		else if(listType.equals("ten")) { //실행안되도록 함, getTen()은 없어도 됨
			response.getWriter().write(getTen(URLDecoder.decode(fromID, "UTF-8"), URLDecoder.decode(toID, "UTF-8")));
		}
		else {
			try {
				response.getWriter().write(getID(URLDecoder.decode(fromID, "UTF-8"), URLDecoder.decode(toID, "UTF-8"), listType));
			} catch(Exception e) {
				response.getWriter().write("");
			}
		}
	}
	
	//첫 페이지가 로딩 되었을 때 10개의 메세지만 출력
	public String getTen(String fromID, String toID) {
		StringBuffer result = new StringBuffer("");
		result.append("{\"result\":["); //결과(result변수를 만들어)에 대한 josn형식의 첫 문장
		ChatDAO chatDAO = new ChatDAO();
		ArrayList<ChatDTO> chatList = chatDAO.getChatListByRecent(fromID, toID, 100); 
		//db에 입력된 메세지를 가져오고 해당 년월일을 현재 날짜로 포맷, 마지막은 10에서 100으로 수정하여 100개까지 메시지 가져오기
		
		if(chatList.size() == 0) {
			return "";
		}
		
		// 위의 결과들을 결과 변수에 모두 json형식으로 대입
		for(int i = 0; i < chatList.size(); i++) {
			result.append("[{\"value\":\"" + chatList.get(i).getFromID() + "\"},");
			result.append("{\"value\":\"" + chatList.get(i).getToID() + "\"},");
			result.append("{\"value\":\"" + chatList.get(i).getChatContent() + "\"},");
			result.append("{\"value\":\"" + chatList.get(i).getChatTime() + "\"}]");
			
			if(i != chatList.size() -1) //만약 i가 마지막 원소가 아니라면
				result.append(","); //콤마을 넣어 더 메세지가 있다는 것을 알려준다.
		}
		result.append("], \"last\":\"" + chatList.get(chatList.size() - 1).getChatID() + "\"}");
		//클라이언트에게 chatID까지 보내 가장 최근에 받은 메세지가 무엇인지 파악하게 한다.
		//즉 클라이언트한테 가장 마지막 메세지의 ID값을 last변수에 담아서 보낸다.
		
		chatDAO.readChat(fromID, toID); //메시지 읽음 처리
		return result.toString(); //json형식으로 결과를 반환 
	}
	
	//특정한 메세지id 이후의 10개를 출력하기 위한 메서드, 특정한 메세지id 를 매개변수로 설정
	//주기적으로 메세지가 도착했는지 확인하면서 계속 호출됨
	public String getID(String fromID, String toID, String chatID) {
		StringBuffer result = new StringBuffer("");
		result.append("{\"result\":["); //결과(result변수를 만들어)에 대한 josn형식의 첫 문장
		ChatDAO chatDAO = new ChatDAO();
		ArrayList<ChatDTO> chatList = chatDAO.getChatListByID(fromID, toID, chatID); 
		//db에 입력된 메세지를 가져오고 해당 년월일을 현재 날짜로 포맷
		
		if(chatList.size() == 0) {
			return "";
		}
		
		// 위의 결과들을 결과 변수에 모두 json형식으로 대입
		for(int i = 0; i < chatList.size(); i++) {
			result.append("[{\"value\":\"" + chatList.get(i).getFromID() + "\"},");
			result.append("{\"value\":\"" + chatList.get(i).getToID() + "\"},");
			result.append("{\"value\":\"" + chatList.get(i).getChatContent() + "\"},");
			result.append("{\"value\":\"" + chatList.get(i).getChatTime() + "\"}]");
			
			if(i != chatList.size() -1) //만약 i가 마지막 원소가 아니라면
				result.append(","); //콤마을 넣어 더 메세지가 있다는 것을 알려준다.
		}
		result.append("], \"last\":\"" + chatList.get(chatList.size() - 1).getChatID() + "\"}");
		//클라이언트에게 chatID까지 보내 가장 최근에 받은 메세지가 무엇인지 파악하게 한다.
		//즉 클라이언트한테 가장 마지막 메세지의 ID값을 last변수에 담아서 보낸다.
		
		chatDAO.readChat(fromID, toID); //메시지 읽음 처리
		return result.toString(); //json형식으로 결과를 반환 
	}

}
