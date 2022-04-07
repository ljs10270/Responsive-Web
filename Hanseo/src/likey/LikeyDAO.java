package likey;

import java.sql.Connection;
import java.sql.PreparedStatement;

import util.DatabaseUtil;

public class LikeyDAO { //database access object
	
	
	public int like(String userID, String boardID, String userIP) {
		String SQL = "INSERT INTO LIKEY VALUES (?, ?, ?)";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DatabaseUtil.getConnection(); 
			pstmt = conn.prepareStatement(SQL);//SQL 인젝션 방지
			pstmt.setString(1, userID);
			pstmt.setInt(2, Integer.parseInt(boardID)); 
			pstmt.setString(3, userIP); 
			return pstmt.executeUpdate(); //INSERT, UPDATE, DELETE문은 executeUpdate()통해서 바로 리턴해주면 된다.
			//executeUpdate()는 영향을 받은 레코드의 개수를 반환한다.즉 INSERT문이기에 1개의 레코드가 테이블에 추가된다, 1이 반환된다.
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return -1; //추천 중복 오류
	}
	
	// 해당 게시글의 추천 DB 레코드 삭제(게시글 삭제)
	public int likeDelete(String boardID) {
		String SQL = "DELETE FROM LIKEY WHERE boardID = ?";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
			
		try {
			conn = DatabaseUtil.getConnection(); 
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, Integer.parseInt(boardID));
			return pstmt.executeUpdate();
				
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return -1;
	}
	
	// 회원탈퇴시
	public int delete(String userID) {
		String SQL = "DELETE FROM LIKEY WHERE userID = ?";
			
		Connection conn = null;
		PreparedStatement pstmt = null;
				
		try {
			conn = DatabaseUtil.getConnection(); 
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			return pstmt.executeUpdate(); 
				
		} catch(Exception e) {
			e.printStackTrace(); 
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
				
		}
		return -1;
	}
}
