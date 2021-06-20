package likey;

import java.sql.Connection;
import java.sql.PreparedStatement;

import util.DatabaseUtil;

public class LikeyDAO { //database access object
	//databaseUtil클래스로 인해 DB와 연동되여 DB에 기록되고 수정되고 데이터를 가져오는 클래스
	
	public int like(String userID, String boardID, String userIP) {
		String SQL = "INSERT INTO LIKEY VALUES (?, ?, ?)";
		//쿼리문 SQL에 대입
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DatabaseUtil.getConnection(); //DatabaseUtil클래스의 get함수(DB연결)를 호출하여 대입
			pstmt = conn.prepareStatement(SQL);//위의 SQL변수를 가져와 쿼리문을 실행하게 준비
			pstmt.setString(1, userID); //위의 쿼리문 첫번쨰 ? 대입
			pstmt.setInt(2, Integer.parseInt(boardID)); //위의 쿼리문 두번쨰 ? 대입
			pstmt.setString(3, userIP); //위의 쿼리문 세번쨰 ? 대입
			return pstmt.executeUpdate(); //INSERT, UPDATE, DELETE문은 executeUpdate()통해서 바로 리턴해주면 된다.
			//executeUpdate()는 영향을 받은 레코드의 개수를 반환한다.즉 INSERT문이기에 1개의 레코드가 테이블에 추가된다, 1이 반환된다.
			
		} catch(Exception e) {
			e.printStackTrace(); //오류 잡히면 해당 오류 출력
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			//1번 사용했으면 자원을 헤제해줘야 함
		}
		return -1; //추천 중복 오류
	}
	
	// 해당 게시글의 추천 DB 레코드 삭제(게시글 삭제)
	public int likeDelete(String boardID) {
		String SQL = "DELETE FROM LIKEY WHERE boardID = ?";
		//쿼리문 SQL에 대입
		Connection conn = null;
		PreparedStatement pstmt = null;
			
		try {
			conn = DatabaseUtil.getConnection(); //DatabaseUtil클래스의 get함수(DB연결)를 호출하여 대입
			pstmt = conn.prepareStatement(SQL);//위의 SQL변수를 가져와 쿼리문을 실행하게 준비
			pstmt.setInt(1, Integer.parseInt(boardID));
			return pstmt.executeUpdate(); //INSERT, UPDATE, DELETE문은 executeUpdate()통해서 바로 리턴해주면 된다.
			//executeUpdate()는 영향을 받은 레코드의 개수를 반환한다.즉 INSERT문이기에 1개의 레코드가 테이블에 추가된다, 1이 반환된다.
			// 1이 리턴되면 회원가입이 성공한 것이다.
				
		} catch(Exception e) {
			e.printStackTrace(); //오류 잡히면 해당 오류 출력
		} finally {
			try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
			try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
			//1번 사용했으면 자원을 헤제해줘야 함
		}
		return -1; //데이터베이스 오류
	}
	
	// 회원탈퇴시
		public int delete(String userID) {
			String SQL = "DELETE FROM LIKEY WHERE userID = ?";
			//쿼리문 SQL에 대입
			Connection conn = null;
			PreparedStatement pstmt = null;
				
			try {
				conn = DatabaseUtil.getConnection(); //DatabaseUtil클래스의 get함수(DB연결)를 호출하여 대입
				pstmt = conn.prepareStatement(SQL);//위의 SQL변수를 가져와 쿼리문을 실행하게 준비
				pstmt.setString(1, userID);
				return pstmt.executeUpdate(); //INSERT, UPDATE, DELETE문은 executeUpdate()통해서 바로 리턴해주면 된다.
				//executeUpdate()는 영향을 받은 레코드의 개수를 반환한다.즉 INSERT문이기에 1개의 레코드가 테이블에 추가된다, 1이 반환된다.
				// 1이 리턴되면 회원가입이 성공한 것이다.
					
			} catch(Exception e) {
				e.printStackTrace(); //오류 잡히면 해당 오류 출력
			} finally {
				try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
				try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
				//1번 사용했으면 자원을 헤제해줘야 함
			}
			return -1; //데이터베이스 오류
		}
}
