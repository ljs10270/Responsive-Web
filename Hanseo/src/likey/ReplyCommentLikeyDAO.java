package likey;

import java.sql.Connection;
import java.sql.PreparedStatement;

import util.DatabaseUtil;

public class ReplyCommentLikeyDAO {
	
	public int replyCommentLike(String userID, String boardID, String replyGroup, String replySequence, String userIP) {
		String SQL = "INSERT INTO REPLYCOMMENTLIKEY VALUES (?, ?, ?, ?, ?)";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DatabaseUtil.getConnection(); 
			pstmt = conn.prepareStatement(SQL);//SQL 인젝션 방지
			pstmt.setString(1, userID); 
			pstmt.setInt(2, Integer.parseInt(boardID)); 
			pstmt.setInt(3, Integer.parseInt(replyGroup)); 
			pstmt.setInt(4, Integer.parseInt(replySequence)); 
			pstmt.setString(5, userIP); 
			return pstmt.executeUpdate(); //INSERT, UPDATE, DELETE문은 executeUpdate()통해서 바로 리턴해주면 된다.
			//executeUpdate()는 영향을 받은 레코드의 개수를 반환한다.즉 INSERT문이기에 1개의 레코드가 테이블에 추가된다, 1이 반환된다.
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return -1; 
	}
	
	// 해당 게시글의 삭제
	public int boardLikeDelete(String boardID) {
		String SQL = "DELETE FROM REPLYCOMMENTLIKEY WHERE boardID = ?";
		
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
	
	// 해당 게시글의 댓글 삭제
	public int groupLikeDelete(String boardID, String replyGroup) {
		String SQL = "DELETE FROM REPLYCOMMENTLIKEY WHERE boardID = ? AND replyGroup = ?";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
			
		try {
			conn = DatabaseUtil.getConnection(); 
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, Integer.parseInt(boardID));
			pstmt.setInt(2, Integer.parseInt(replyGroup));
			return pstmt.executeUpdate();
				
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return -1;
	}
	
	// 해당 게시글의 댓글의 답변 삭제
	public int SequenceLikeDelete(String boardID, String replyGroup, String replySequence) {
		String SQL = "DELETE FROM REPLYCOMMENTLIKEY WHERE boardID = ? AND replyGroup = ? AND replyGroup = ?";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
			
		try {
			conn = DatabaseUtil.getConnection(); 
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, Integer.parseInt(boardID));
			pstmt.setInt(2, Integer.parseInt(replyGroup));
			pstmt.setInt(3, Integer.parseInt(replySequence));
			return pstmt.executeUpdate(); 
				
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			
		}
		return -1;
	}
	
	// 회원 탈퇴시
	public int delete(String userID) {
		String SQL = "DELETE FROM REPLYCOMMENTLIKEY WHERE userID = ?";
		
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
