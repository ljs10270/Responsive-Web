package reply;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import util.DatabaseUtil;

public class ReplyDAO {
		// 게시글 댓글
		public int reply(String userID, String boardID, String replyContent) { //게시글 답변
			String SQL = "INSERT INTO REPLY SELECT ?, ?, ?, now(), IFNULL((SELECT MAX(replyGroup) + 1 FROM REPLY), 1), 0, 0, 0, 1";
			//마지막 1인 댓글 삭제되면 0으로 수정 = 대댓글 형식 잡아주기 위함
			Connection conn = null;
			PreparedStatement pstmt = null;
			
			try {
				conn = DatabaseUtil.getConnection();
				pstmt = conn.prepareStatement(SQL);//SQL 인젝션 방지
				pstmt.setString(1, userID); 
				pstmt.setString(2, boardID); 
				pstmt.setString(3, replyContent);
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
		
		public ArrayList<ReplyDTO> getReplyList(String boardID) {
			ArrayList<ReplyDTO> replyList = null;
			String SQL = "SELECT * FROM REPLY WHERE boardID = ? ORDER BY replyGroup ASC, replySequence ASC";
			
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null; 
			
			try {
				conn = DatabaseUtil.getConnection(); 
				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, boardID);
				rs = pstmt.executeQuery(); 
				
				replyList = new ArrayList<ReplyDTO>();
				
				while(rs.next()) {
					ReplyDTO reply = new ReplyDTO();
					
					reply.setUserID(rs.getString("userID"));
					reply.setBoardID(rs.getInt("boardID"));
					reply.setReplyContent(rs.getString("replyContent").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
					
					int replyTime = Integer.parseInt(rs.getString("replyDate").substring(11, 13));
					
					String timeType = "오전";
					if(replyTime >= 12) {
						timeType = "오후";
						replyTime -= 12;
					}
					
					reply.setReplyDate(rs.getString("replyDate").substring(0,11) 
							+ " " + timeType + " " + replyTime + ":"
							+ rs.getString("replyDate").substring(14, 16) + "");
					
					reply.setReplyGroup(rs.getInt("replyGroup"));
					reply.setReplySequence(rs.getInt("replySequence"));
					reply.setReplyLevel(rs.getInt("replyLevel"));
					reply.setLikeCount(rs.getInt("likeCount"));
					reply.setReplyAvailable(rs.getInt("replyAvailable"));
					
					replyList.add(reply);
				}
			} catch(Exception e) {
				e.printStackTrace(); 
			} finally {
				try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
				try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
				try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
				
			}
			return replyList;
		}
		
		// 내 활동의 내 댓글은 내림차순 보여줌
		public ArrayList<ReplyDTO> getMyReplyList(String userID) {
			ArrayList<ReplyDTO> replyList = null;
			String SQL = "SELECT * FROM REPLY WHERE userID = ? AND replyAvailable = 1 ORDER BY replyDate DESC";
			
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null; 
			
			try {
				conn = DatabaseUtil.getConnection(); 
				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, userID);
				rs = pstmt.executeQuery(); 
				
				replyList = new ArrayList<ReplyDTO>();
				
				while(rs.next()) {
					ReplyDTO reply = new ReplyDTO();
					
					reply.setUserID(rs.getString("userID"));
					reply.setBoardID(rs.getInt("boardID"));
					reply.setReplyContent(rs.getString("replyContent").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
					
					int replyTime = Integer.parseInt(rs.getString("replyDate").substring(11, 13));
					
					String timeType = "오전";
					if(replyTime >= 12) {
						timeType = "오후";
						replyTime -= 12;
					}
					
					reply.setReplyDate(rs.getString("replyDate").substring(0,11) 
							+ " " + timeType + " " + replyTime + ":"
							+ rs.getString("replyDate").substring(14, 16) + "");
					
					reply.setReplyGroup(rs.getInt("replyGroup"));
					reply.setReplySequence(rs.getInt("replySequence"));
					reply.setReplyLevel(rs.getInt("replyLevel"));
					reply.setLikeCount(rs.getInt("likeCount"));
					reply.setReplyAvailable(rs.getInt("replyAvailable"));
					
					replyList.add(reply);
				}
			} catch(Exception e) {
				e.printStackTrace(); 
			} finally {
				try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
				try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
				try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
				
			}
			return replyList;
		}
		
		// 댓글 좋아요
		public int replyLike(String boardID, String replyGroup) {
			String SQL = "UPDATE REPLY SET likeCount = likeCount + 1 WHERE boardID = ? AND replyGroup = ? AND replySequence = 0";
			
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
		
		// 댓글의 답글(대댓글) 좋아요
		public int replyCommentLike(String boardID, String replyGroup, String replySequence) {
			String SQL = "UPDATE REPLY SET likeCount = likeCount + 1 WHERE boardID = ? AND replyGroup = ? AND replySequence = ?";
			
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
				
		// 댓글에 대한 답변은 제외하고 댓글만 가져옴 = 부모 댓글
		public ReplyDTO getReply(String boardID, String replyGroup) {
			ReplyDTO reply = new ReplyDTO();
			String SQL = "SELECT * FROM REPLY WHERE boardID = ? AND replyGroup = ? AND replySequence = 0";
			
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null; 
			
			try {
				conn = DatabaseUtil.getConnection(); 
				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, boardID); 
				pstmt.setString(2, replyGroup);
				rs = pstmt.executeQuery(); 
				
				if(rs.next()) {
					reply.setUserID(rs.getString("userID"));
					reply.setBoardID(rs.getInt("boardID"));
					reply.setReplyContent(rs.getString("replyContent").replaceAll(" ", "&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br>"));
					
					int replyTime = Integer.parseInt(rs.getString("replyDate").substring(11, 13));
					
					String timeType = "오전";
					if(replyTime >= 12) {
						timeType = "오후";
						replyTime -= 12;
					}
					
					reply.setReplyDate(rs.getString("replyDate").substring(0,11) 
							+ " " + timeType + " " + replyTime + ":"
							+ rs.getString("replyDate").substring(14, 16) + "");
					
					reply.setReplyGroup(rs.getInt("replyGroup"));
					reply.setReplySequence(rs.getInt("replySequence"));
					reply.setReplyLevel(rs.getInt("replyLevel"));
					reply.setLikeCount(rs.getInt("likeCount"));
					reply.setReplyAvailable(rs.getInt("replyAvailable"));
					
				}
			} catch(Exception e) {
				e.printStackTrace(); 
			} finally {
				try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
				try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
				try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
				
			}
			return reply;
		}
	
		// 댓글의 답변 DB 삽입
		public int replyAnswer(String userID, String boardID, String replyContent, ReplyDTO parent) {
			String SQL = "INSERT INTO REPLY SELECT ?, ?, ?, now(), ?, IFNULL((SELECT MAX(replySequence) + 1 FROM REPLY GROUP BY replyGroup HAVING replyGroup = ?), 1), 0, 0, 1";
			
			Connection conn = null;
			PreparedStatement pstmt = null;
			
			try {
				conn = DatabaseUtil.getConnection();
				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, userID); 
				pstmt.setString(2, boardID);
				pstmt.setString(3, replyContent);
				pstmt.setInt(4, parent.getReplyGroup());
				pstmt.setInt(5, parent.getReplyGroup());
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
		
		//해당 댓글의 답변이 달릴 때마다 댓글의 레벨 +1
		public int replyLevelUpdate(ReplyDTO parent) {
			String SQL = "UPDATE REPLY SET replyLevel = replyLevel + 1 WHERE boardID = ? AND replyGroup = ? AND replySequence = 0";
			
			Connection conn = null;
			PreparedStatement pstmt = null;
			
			try {
				conn = DatabaseUtil.getConnection();
				pstmt = conn.prepareStatement(SQL);
				pstmt.setInt(1, parent.getBoardID());
				pstmt.setInt(2, parent.getReplyGroup());
				return pstmt.executeUpdate(); 
				
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
				try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
				
			}
			return -1; 
		}
		
		// 댓글에 답변 삭제하면 댓글의 레벨 감소 업데이트
		public int replyLevelDelete(ReplyDTO parent) {
			String SQL = "UPDATE REPLY SET replyLevel = replyLevel - 1 WHERE boardID = ? AND replyGroup = ? AND replySequence = 0";
			
			Connection conn = null;
			PreparedStatement pstmt = null;
			
			try {
				conn = DatabaseUtil.getConnection(); 
				pstmt = conn.prepareStatement(SQL);
				pstmt.setInt(1, parent.getBoardID());
				pstmt.setInt(2, parent.getReplyGroup());
				return pstmt.executeUpdate(); 
				
			} catch(Exception e) {
				e.printStackTrace(); 
			} finally {
				try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
				try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
				
			}
			return -1;
		}
		
		// 댓글의 삭제(답변(대댓글) 삭제 x)
		public int replyDelete(ReplyDTO parent, int replyLevel) {
			String SQL = "";
			
			if(replyLevel == 0) {
				SQL = "DELETE FROM REPLY WHERE boardID = ? AND replyGroup = ?";
			} else {
				SQL = "UPDATE REPLY SET replyAvailable = 0 WHERE boardID = ? AND replyGroup = ? AND replySequence = 0";
			}
			
			
			Connection conn = null;
			PreparedStatement pstmt = null;
			
			try {
				conn = DatabaseUtil.getConnection(); 
				pstmt = conn.prepareStatement(SQL);
				pstmt.setInt(1, parent.getBoardID()); 
				pstmt.setInt(2, parent.getReplyGroup());
				return pstmt.executeUpdate(); 
				
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
				try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
				
			}
			return -1;
		}
		
		// 댓글의 답글(대댓글) 삭제
		public int replyCommentDelete(String boardID, String replyGroup, String replySequence) {
			String SQL = "DELETE FROM REPLY WHERE boardID = ? AND replyGroup = ? AND replySequence = ?";
			
			
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
		
		// 게시글 삭제, 게시글의 댓글과 답글(대댓글) 모두 삭제
		public int replyDelete(String boardID) {
			String SQL = "DELETE FROM REPLY WHERE boardID = ?";
			
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
		
		// 게시글 댓글 수
		public int replyCount(int boardID) {
			String SQL = "SELECT COUNT(*) FROM REPLY WHERE boardID = ?";
			
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null; 
			
			try {
				conn = DatabaseUtil.getConnection();
				pstmt = conn.prepareStatement(SQL);
				pstmt.setInt(1, boardID);
				rs = pstmt.executeQuery(); 
				
				if(rs.next()) {
					return rs.getInt(1);
				}
				
			} catch(Exception e) {
				e.printStackTrace(); 
			} finally {
				try {if(conn != null) conn.close();} catch (Exception e) {e.printStackTrace();}
				try {if(pstmt != null) pstmt.close();} catch (Exception e) {e.printStackTrace();}
				try {if(rs != null) rs.close();} catch (Exception e) {e.printStackTrace();}
				
			}
			
			return 0;
		}
}
