package chat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import util.DatabaseUtil;

public class ChatDAO {
	
	// 채팅 보내기
	public int submit(String fromID, String toID, String chatContent) {
		String SQL = "INSERT INTO CHAT VALUES (NULL, ?, ?, ?, NOW(), 0)";
		//쿼리문 SQL에 대입. NULL 값을 넣는 chatID는 int형 기본키로 자동 증가하게 DB 설계함
		// 마지막 0은 읽지 않은 메시지를 의미
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(SQL);//SQL 인젝션 방지
			pstmt.setString(1, fromID);
			pstmt.setString(2, toID);
			pstmt.setString(3, chatContent);
			return pstmt.executeUpdate();
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return -1; 
	}
	
	// 채팅 내역 가져오기
	public ArrayList<ChatDTO> getChatListByID(String fromID, String toID, String chatID) {
		String SQL = "SELECT * FROM CHAT WHERE ((fromID = ? AND toID = ?) OR (fromID = ? AND toID = ?)) AND chatID > ? ORDER BY chatTime";
		
		
		ArrayList<ChatDTO> chatList = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = DatabaseUtil.getConnection(); 
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, fromID); 
			pstmt.setString(2, toID);
			pstmt.setString(3, toID);
			pstmt.setString(4, fromID); //메시지를 받던 보내던 항상 참이 되도록
			pstmt.setInt(5, Integer.parseInt(chatID)); //int로 바꿈 DB에서 chatID는 int형 기본키에 자동 증가임
			
			rs = pstmt.executeQuery(); //위의 결과를 rs에 담음, 조회(SELECT)문은 executeQuery()를 이용해  리서트셋을 통해 담아야 한다.
			chatList = new ArrayList<ChatDTO>();
			
			while(rs.next()) {
				ChatDTO chat = new ChatDTO();
				chat.setChatID(rs.getInt("chatID"));
				
				// 크로스 사이트 스크립트 공격(XSS) 보안을 위해 특수문자를 html로 치환 
				chat.setFromID(rs.getString("fromID")
						.replaceAll(" ", "&nbsp;")
						.replaceAll("<", "&lt;")
						.replaceAll(">", "&gt;")
						.replaceAll("\n", "<br>"));
				
				chat.setToID(rs.getString("toID")
						.replaceAll(" ", "&nbsp;")
						.replaceAll("<", "&lt;")
						.replaceAll(">", "&gt;")
						.replaceAll("\n", "<br>"));
				
				chat.setChatContent(rs.getString("chatContent")
						.replaceAll(" ", "&nbsp;")
						.replaceAll("<", "&lt;")
						.replaceAll(">", "&gt;")
						.replaceAll("\n", "<br>"));
				
				int chatTime = Integer.parseInt(rs.getString("chatTime").substring(11, 13));
				
				String timeType = "오전";
				if(chatTime >= 12) {
					timeType = "오후";
					chatTime -= 12;
				}
				
				chat.setChatTime(rs.getString("chatTime").substring(0,11) 
						+ " " + timeType + " " + chatTime + ":"
						+ rs.getString("chatTime").substring(14, 16) + "");
				chatList.add(chat);
			}
		} catch(Exception e) {
			e.printStackTrace(); 
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return chatList; //채팅 리스트 반환
	}
	
	// number 파라미터로 chatID를 뺀 결과인 상위 채팅 리스트를 보여줌
	public ArrayList<ChatDTO> getChatListByRecent(String fromID, String toID, int number) {
		String SQL = "SELECT * FROM CHAT WHERE ((fromID = ? AND toID = ?) OR (fromID = ? AND toID = ?)) AND chatID > (SELECT MAX(chatID) - ? FROM CHAT WHERE (fromID = ? AND toID = ?) OR (fromID = ? AND toID = ?)) ORDER BY chatTime";
		//두명만 대화가 가능하도록 수정함
		
		ArrayList<ChatDTO> chatList = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, fromID); 
			pstmt.setString(2, toID);
			pstmt.setString(3, toID);
			pstmt.setString(4, fromID); //메시지를 받던 보내던 항상 참이 되도록
			pstmt.setInt(5, number);
			pstmt.setString(6, fromID); 
			pstmt.setString(7, toID);
			pstmt.setString(8, toID);
			pstmt.setString(9, fromID);
			
			rs = pstmt.executeQuery(); //위의 결과를 rs에 담음, 조회(SELECT)문은 executeQuery()를 이용해  리서트셋을 통해 담아야 한다.
			chatList = new ArrayList<ChatDTO>();
			
			while(rs.next()) {
				ChatDTO chat = new ChatDTO();
				chat.setChatID(rs.getInt("chatID"));
				
				// 크로스 사이트 스크립트 공격 보안을 위해 특수문자를 html로 치환 
				chat.setFromID(rs.getString("fromID")
						.replaceAll(" ", "&nbsp;")
						.replaceAll("<", "&lt;")
						.replaceAll(">", "&gt;")
						.replaceAll("\n", "<br>"));
				
				chat.setToID(rs.getString("toID")
						.replaceAll(" ", "&nbsp;")
						.replaceAll("<", "&lt;")
						.replaceAll(">", "&gt;")
						.replaceAll("\n", "<br>"));
				
				chat.setChatContent(rs.getString("chatContent")
						.replaceAll(" ", "&nbsp;")
						.replaceAll("<", "&lt;")
						.replaceAll(">", "&gt;")
						.replaceAll("\n", "<br>"));
				
				int chatTime = Integer.parseInt(rs.getString("chatTime").substring(11, 13));
				
				String timeType = "오전";
				if(chatTime >= 12) {
					timeType = "오후";
					chatTime -= 12;
				}
				
				chat.setChatTime(rs.getString("chatTime").substring(0,11) 
						+ " " + timeType + " " + chatTime + ":"
						+ rs.getString("chatTime").substring(14, 16) + "");
				chatList.add(chat);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return chatList; //채팅 리스트 반환
	}

	// 메시지 읽음 처리
	public int readChat(String fromID, String toID) {
		String SQL = "UPDATE CHAT SET chatRead = 1 WHERE (fromID = ? AND toID = ?)";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; 
		
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, toID); 
			pstmt.setString(2, fromID);
			return pstmt.executeUpdate(); 
			
		} catch(Exception e) {
			e.printStackTrace(); 
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return -1; //DB 오류
	}
	
	// 읽지 않은 모든 메시지 개수 반환
	public int getAllUnreadChat(String chatName) {
		String SQL = "SELECT COUNT(chatID) FROM CHAT WHERE toID = ? AND chatRead = 0";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, chatName);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return rs.getInt("COUNT(chatID)");
			}
			return 0;
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return -1; //DB 오류
	}
	
	// 각각의 사용자들에게 받은 메시지 별로 안읽은 메시지 개수 메시지 함에서 출력을 위함
	public int getUnreadChat(String fromID, String toID) {
		String SQL = "SELECT COUNT(chatID) FROM CHAT WHERE fromID = ? AND toID = ? AND chatRead = 0";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, fromID); 
			pstmt.setString(2, toID);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return rs.getInt("COUNT(chatID)");
			}
			return 0;
		} catch(Exception e) {
			e.printStackTrace(); 
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return -1; //DB 오류
	}
	
	// 첫 메시지함에서 새로운 메시지 중 가장 최근의 메시지가 대표적으로 보여지도록
	public ArrayList<ChatDTO> getBox(String chatName) {
		String SQL = "SELECT * FROM CHAT WHERE chatID IN (SELECT MAX(chatID) FROM CHAT WHERE toID = ? OR fromID = ? GROUP BY fromID, toID)";
		
			
		ArrayList<ChatDTO> chatList = null;
			
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
			
		try {
			conn = DatabaseUtil.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, chatName);
			pstmt.setString(2, chatName);
				
			rs = pstmt.executeQuery(); //위의 결과를 rs에 담음, 조회(SELECT)문은 executeQuery()를 이용해  리서트셋을 통해 담아야 한다.
			chatList = new ArrayList<ChatDTO>();
				
			while(rs.next()) {
				ChatDTO chat = new ChatDTO();
				chat.setChatID(rs.getInt("chatID"));
					
				// 크로스 사이트 스크립트 공격 보안을 위해 특수문자를 html로 치환 
				chat.setFromID(rs.getString("fromID")
						.replaceAll(" ", "&nbsp;")
						.replaceAll("<", "&lt;")
						.replaceAll(">", "&gt;")
						.replaceAll("\n", "<br>"));
					
				chat.setToID(rs.getString("toID")
						.replaceAll(" ", "&nbsp;")
						.replaceAll("<", "&lt;")
						.replaceAll(">", "&gt;")
						.replaceAll("\n", "<br>"));
					
				chat.setChatContent(rs.getString("chatContent")
						.replaceAll(" ", "&nbsp;")
						.replaceAll("<", "&lt;")
						.replaceAll(">", "&gt;")
						.replaceAll("\n", "<br>"));
					
				int chatTime = Integer.parseInt(rs.getString("chatTime").substring(11, 13));
					
				String timeType = "오전";
				if(chatTime >= 12) {
					timeType = "오후";
					chatTime -= 12;
				}
					
				chat.setChatTime(rs.getString("chatTime").substring(0,11) 
						+ " " + timeType + " " + chatTime + ":"
						+ rs.getString("chatTime").substring(14, 16) + "");
				chatList.add(chat);
			}
			
			for(int i = 0; i < chatList.size(); i++) {
				ChatDTO x = chatList.get(i);
				
				for(int j = 0; j < chatList.size(); j++) {
					ChatDTO y = chatList.get(j);
					
					if(x.getFromID().equals(y.getToID()) && x.getToID().equals(y.getFromID())) { //chat DB에서 겹치는 경우
						if(x.getChatID() < y.getChatID()) {
							chatList.remove(x);
							i--;
							break;
						} else {
							chatList.remove(y);
							j--;
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace(); 
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return chatList; //채팅 리스트 반환
	}
}
