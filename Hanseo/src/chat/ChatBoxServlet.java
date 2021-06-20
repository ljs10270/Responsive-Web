package chat;

import java.io.IOException;

import java.net.URLDecoder;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import user.UserDAO;
import user.UserDTO;

@WebServlet("/ChatBoxServlet")
public class ChatBoxServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); //매개변수들(넘어온 값들을) UTF-8으로 처리
		response.setContentType("text/html;charset=UTF-8");
		
		String userID = request.getParameter("userID");
		String chatName = null;
		
		if(userID == null || userID.equals("")) {
			response.getWriter().write("");
		}
		else {
			userID = URLDecoder.decode(userID, "UTF-8");
			HttpSession session = request.getSession();
			if(!userID.equals((String) session.getAttribute("userID"))) {
				response.getWriter().write("");
				return;
			}
			
			UserDTO user = new UserDAO().getUser(userID);
			chatName = user.getUserName() + '(' + user.getUserDepartment() + ')';
			try {
				response.getWriter().write(getBox(chatName));
			} catch(Exception e) {
				response.getWriter().write("");
			}
		}
	}
	
	public String getBox(String chatName) {
		StringBuffer result = new StringBuffer("");
		result.append("{\"result\":["); //결과(result변수를 만들어)에 대한 josn형식의 첫 문장
		UserDAO userDAO = new UserDAO();
		ChatDAO chatDAO = new ChatDAO();
		ArrayList<ChatDTO> chatList = chatDAO.getBox(chatName); 
		
		if(chatList.size() == 0) {
			return "1";
		}

		// 위의 결과들을 결과 변수에 모두 json형식으로 대입, 최신 메시지가 가장 위쪽으로 가야 므로 내림차순으로 보여주기 위해 for문 역순으로 돌림
		for(int i = chatList.size() - 1; i >= 0; i--) {
			String userProfile = "";
			
			//각 대화 상대별 안읽은 메시지 개수 출력을 위해
			String unread = "";
			if(chatName.equals(chatList.get(i).getToID())) {
				unread = chatDAO.getUnreadChat(chatList.get(i).getFromID(), chatName) + "";
				//상대방에 대한 메시지를 다 읽은 상태라면
				if(unread.equals("0"))
					unread = "";
			}
			
			//지난 아이디, 클릭시 보내는 url, 내용, 시간을 DB에서 최상위 chatID 1개(최근 메시지)를 클라이언트에게 보낼건데
			//만약 최근 마지막으로 보낸 사람이 사용자라면 상대방과 대화를 하는 것이기에 상대방의 닉네임과 url(ID)를 그대로 보내고
			if(chatName.equals(chatList.get(i).getFromID())) { 
				String name = chatList.get(i).getToID();
				String[] userName = name.split("[(]"); //특수문자는 이렇게 구분해야 한다 ㅅㅂ
				String toID = userDAO.getUserID(userName[0]);
				userProfile = userDAO.getProfile(toID);
				
				result.append("[{\"value\":\"" + chatList.get(i).getToID() + "\"},");
				result.append("{\"value\":\"" + toID + "\"},");
			}
			else {// 사용자가 메시지를 받았다면 사용자가에게 메시지를 보낸 사람(from)의 닉네임 출력과 ID를 url로
				String name = chatList.get(i).getFromID();
				String userName[] = name.split("[(]"); //특수문자는 이렇게 구분해야 한다 ㅅㅂ
				String fromID = userDAO.getUserID(userName[0]);
				userProfile = userDAO.getProfile(fromID);
				
				result.append("[{\"value\":\"" + chatList.get(i).getFromID() + "\"},");
				result.append("{\"value\":\"" + fromID + "\"},");
			}
			result.append("{\"value\":\"" + chatList.get(i).getChatContent() + "\"},");
			result.append("{\"value\":\"" + chatList.get(i).getChatTime() + "\"},");
			result.append("{\"value\":\"" + unread + "\"},");
			result.append("{\"value\":\"" + userProfile + "\"}]");
			
			if(i != 0) //만약 i가 마지막 원소가 아니라면
				result.append(","); //콤마을 넣어 더 메세지가 있다는 것을 알려준다.
		}
		result.append("], \"last\":\"" + chatList.get(chatList.size() - 1).getChatID() + "\"}");
		
		return result.toString(); //json형식으로 결과를 반환 
	}
}
